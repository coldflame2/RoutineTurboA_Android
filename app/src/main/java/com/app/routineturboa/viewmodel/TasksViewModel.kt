package com.app.routineturboa.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.routineturboa.data.local.RoutineRepository
import com.app.routineturboa.data.model.TaskEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.time.LocalTime

const val TAG = "TasksViewModel"

class TasksViewModel(private val repository: RoutineRepository) : ViewModel() {

    val tasks: StateFlow<List<TaskEntity>> = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateTaskAndAdjustNext(initialEditedTask: TaskEntity) {
        Log.d(TAG, "Updating '${initialEditedTask.name}' task and its next task.")

        viewModelScope.launch {
            try {
                // Data preparations

                val taskBelow = fetchNextTask(initialEditedTask)
                if (taskBelow != null) {
                    Log.d(TAG, "Task below to update: ${taskBelow.name}")

                    val startTimeTaskBelow = initialEditedTask.endTime
                    val durationTaskBelow = Duration.between(startTimeTaskBelow, taskBelow.endTime)
                    val durationTaskBelowInt = durationTaskBelow.toMinutes().toInt()

                    // Create a new task entity with the updated duration and start time
                    val taskWithUpdatesForBelow = taskBelow.copy(
                        duration = durationTaskBelowInt,
                        startTime = startTimeTaskBelow
                    )

                    Log.d(TAG, "old duration: ${taskBelow.duration}, new duration: ${taskWithUpdatesForBelow.duration}, " +
                            "old startTime: ${taskBelow.startTime}, new start time: ${taskWithUpdatesForBelow.startTime}")

                    // Run the transaction with both updates
                    repository.runAsTransaction {
                        repository.updateTask(initialEditedTask)
                        repository.updateTask(taskWithUpdatesForBelow)
                    }

                } else {
                    Log.d(TAG, "No task below to update. Updating only the edited task.")

                    // Run the transaction with only the update to taskToUpdate
                    repository.runAsTransaction {
                        repository.updateTask(initialEditedTask)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating tasks", e)
            }
        }
    }

    /**
     * @name beginNewTaskOperations
     * The begins the new task operations. The order of operations matter.
     *
     * @param clickedTask
     * @param newTask
     *
     * ## Purpose
     * This function manages the process of inserting a new task into an ordered list of tasks, ensuring proper positioning and duration management of affected tasks.
     *
     * ## Parameters
     * - `clickedTask: TaskEntity`: The task after which the new task will be inserted.
     * - `newTask: TaskEntity`: The new task to be inserted.
     *
     * ## Return Value
     * - `Result<Boolean>`: A Result object containing a Boolean.
     *   - Success (true) indicates all operations completed successfully.
     *   - Failure contains an Exception with details about what went wrong.
     *
     * ## Process Flow
     *
     * ### 1. Duration Validation
     * - **Timing**: Occurs before any database changes.
     * - **Purpose**: Ensures the new task's duration is valid in relation to the task it will precede.
     * - **Steps**:
     *   a. Retrieves the task currently below the `clickedTask` (referred to as `taskBelowToBeShifted`).
     *   b. Compares `newTask.duration` with `taskBelowToBeShifted.duration`.
     *   c. If `newTask.duration` is greater than or equal to `taskBelowToBeShifted.duration`, the operation fails.
     * - **Failure Handling**: Returns a `Result.failure` with an appropriate error message.
     *
     * ### 2. Update Positions
     *      * - **Timing**: Occurs after all other operations are complete.
     *      * - **Purpose**: Ensures all tasks below the newly inserted task have correct positions.
     *      * - **Steps**:
     *      *   a. Calls `updateTaskPositions(newTask)` to adjust positions of all tasks below the new task.
     *      *   b. Increments the position of each affected task by 1.
     *
     * ### 3. New Task Insertion
     * - **Timing**: Occurs immediately after successful duration validation.
     * - **Purpose**: Adds the new task to the database.
     * - **Note**: The `newTask` already has its position set (clickedTask.position + 1).
     *
     * ### 4. Update Duration of Shifted Task
     * - **Timing**: Occurs right after the new task insertion.
     * - **Purpose**: Adjusts the duration of the task that is now below the newly inserted task.
     * - **Steps**:
     *   a. Calculates the new duration: `taskBelowToBeShifted.duration - newTask.duration`
     *   b. Updates `taskBelowToBeShifted` with this new duration.
     * - **Note**: This task is referred to as "now-shifted" because the new task has been inserted above it.
     *
     *
     * ## Important Considerations
     * 1. **Transaction Management**: The entire process is wrapped in a database transaction. This ensures that either all operations succeed, or none are applied if any step fails.
     *
     * 2. **Error Handling**:
     *    - Validation errors (e.g., invalid duration) and Unexpected exceptions result in a `Result.failure` return.
     *
     * 3. **Positional Awareness**: The function assumes that `newTask.position` is already set correctly (clickedTask.position + 1) before being passed to this function.
     *
     * 6. **Task Relationships**:
     *    - `clickedTask`: The task after which the new task will be inserted.
     *    - `newTask`: The task being inserted.
     *    - `taskBelowToBeShifted`: Initially the task below `clickedTask`, ends up below `newTask` after insertion.
     *
     * ## Usage
     * This function should be called when a user action triggers the insertion of a new task at a specific position in the task list. It handles all necessary validations and updates to maintain the integrity of the task list structure.
     */
    fun beginNewTaskOperations(clickedTask: TaskEntity, newTask: TaskEntity): Result<Boolean> {
        return runBlocking {
            try {
                repository.runAsTransaction {
                    Log.d(TAG, "----Starting new task operations-----")

                    val taskBelowToBeShifted = fetchNextTask(clickedTask)
                        ?: return@runAsTransaction Result.failure(Exception("Task below clicked task not found"))

                    Log.d(TAG, " **** Clicked task: '${clickedTask.name}', " +
                            "New task: '${newTask.name}'," +
                            "Task Below: '${taskBelowToBeShifted.name}'")

                    // 1. Validate duration
                    Log.d(TAG, " **** 1st Step) Validating duration...")
                    val taskBelowToBeShiftedDuration = taskBelowToBeShifted.duration

                    if (newTask.duration >= taskBelowToBeShiftedDuration )  {
                        Log.e(TAG, "Invalid duration. New Task duration: ${newTask.duration}, " +
                                "Task Below Duration: $taskBelowToBeShiftedDuration")
                        return@runAsTransaction Result.failure(Exception("Invalid new task duration"))
                    }

                    // 2. Update Positions of all the tasks below the new Task
                    Log.d(TAG, " **** 2nd Step) Updating positions...")
                    incrementTasksBelow(clickedTask)

                    // 3. Inserting new task...
                    Log.d(TAG, " **** 3rd Step) Inserting new task...")
                    repository.insertTask(newTask)

                    // 4. Updating duration and startTime of shifted task
                    Log.d(TAG, " **** 4th Step) Updating duration and startTime of shifted task...")
                    val updatedDuration = taskBelowToBeShiftedDuration - newTask.duration
                    val updatedStartTime = newTask.endTime

                    val taskBelowNowShifted = taskBelowToBeShifted.copy()

                    Log.d(TAG, "taskBelow-NOW-Shifted Position: ${taskBelowNowShifted.position}" +
                            " taskBelow-To-Be-Shifted Position: ${taskBelowToBeShifted.position}")

                    // Exclude the last task from the update
                    if (!isTaskLast(taskBelowNowShifted)) {
                        Log.d(TAG, "Task Below is not the last task. Updating with new duration, start Time, and new position.")
                        val taskBelowNowShiftedUpdated = taskBelowToBeShifted.copy(
                            duration = updatedDuration,
                            startTime = updatedStartTime,
                            position = newTask.position + 1
                        )
                        updateTaskAndAdjustNext(taskBelowNowShiftedUpdated)
                    } else {
                        Log.d(TAG, "Task Below is the last task. Updating with new duration and start Time.")
                        val taskBelowNowShiftedUpdated = taskBelowToBeShifted.copy(
                            duration = updatedDuration,
                            startTime = updatedStartTime
                        )
                        updateTaskAndAdjustNext(taskBelowNowShiftedUpdated)
                    }

                    Result.success(true)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error inserting task", e)
                Result.failure(e)
            }
        }
    }

    private fun incrementTasksBelow(referenceTask: TaskEntity?) {

        if (referenceTask == null) {
            Log.e(TAG, "incrementTasksBelow: anchorTask is null")
            return
        }

        val taskBelowReferenceTask = fetchNextTask(referenceTask)

        if (taskBelowReferenceTask != null ){
            if (isTaskLast(taskBelowReferenceTask)) {
                Log.e(TAG, "incrementTasksBelow: taskBelowReferenceTask is last task. Skipping updating positions.")
                return
            }
        }

        viewModelScope.launch {
            Log.d(TAG, "Updating task positions below clickedTask: ${referenceTask.name}")

            val currentTasks = tasks.value
            // Find the index of the reference task
            val referenceTaskIndex = currentTasks.indexOfFirst { it.id == referenceTask.id }

            // Exclude the last task from the update
            val listOfTasksToUpdate = currentTasks
                .drop(referenceTaskIndex + 1)  // Skip tasks up to and including the reference task
                .dropLast(1)  // Remove the last task

            Log.d(TAG, "tasks to update - ${listOfTasksToUpdate.map { "Name:${it.name}, Position:${it.position}"}}")

            var newPosition = referenceTask.position + 1

            val tasksWithNewPositions = mutableListOf<TaskEntity>()

            for (task in listOfTasksToUpdate) {
                newPosition++
                val taskWithNewPosition = task.copy(position = newPosition)
                Log.d(TAG, "Updating task '${task.name}' position from ${task.position} to ${taskWithNewPosition.position}")
                tasksWithNewPositions.add(taskWithNewPosition)
            }

            if (tasksWithNewPositions.isNotEmpty()) {
                // Update all affected tasks in a single transaction
                val updateResult = repository.updateTasksWithNewPositions(tasksWithNewPositions)
                Log.d(TAG, "Update result: $updateResult")
            } else {
                Log.d(TAG, "No tasks to update")
            }
        }
    }

    init {
        viewModelScope.launch {
            repository.initializeDefaultTasks()
        }
    }

    fun fetchNextTask(baseTask: TaskEntity): TaskEntity? {
        Log.d(TAG, "Getting task below '${baseTask.name}' task...")

        if (isTaskLast(baseTask)){
            Log.d(TAG, "baseTask is last task. Returning null.")
            return null
        }

        val currentTasks = tasks.value
        val baseTaskIndex = currentTasks.indexOfFirst { it.id == baseTask.id }

        return if (baseTaskIndex != -1 && baseTaskIndex < currentTasks.size - 1) {
            val nextTask = currentTasks[baseTaskIndex + 1]
            Log.d(TAG, "Next Task: ${nextTask.name}")
            nextTask
        } else {
            Log.d(TAG, "No task found below or clicked task is not in the list.")
            null
        }
    }

    private fun fetchLastTask(): TaskEntity? {
        val tasks = tasks.value

        if (tasks.isEmpty()) {
            Log.e(TAG, "Task list is empty")
            return null
        }

        val lastTask = tasks.maxByOrNull { it.position }
        if (lastTask == null) {
            Log.e(TAG, "Failed to find last task by position")
            return null
        }

        // Check if the end time is 23:59
        val expectedEndTime = LocalTime.of(23, 59)
        if (lastTask.endTime.toLocalTime() != expectedEndTime) {
            Log.e(TAG, "Last task (${lastTask.name}) end time is not 23:59. Actual end time: ${lastTask.endTime}")
        }
        val expectedTaskID = -2
        if (lastTask.id != expectedTaskID) {
            Log.e(TAG, "Last task (${lastTask.name}) has an unexpected ID. Expected: $expectedTaskID, Actual: ${lastTask.id}")
        }

        return lastTask
    }

    fun isTaskFirst(task: TaskEntity): Boolean {
        val tasks = tasks.value

        if (tasks.isEmpty()) {
            Log.d(TAG, "Task list is empty")
            return false
        }

        val firstTask = tasks.minByOrNull { it.position }
        if (firstTask == null) {
            Log.e(TAG, "Failed to find first task by position")
            return false
        }

        if (task.position != 1) {
            return false
        }

        // Check if the start time is 00:01
        val expectedStartTime = LocalTime.of(0, 1)
        if (task.startTime.toLocalTime() != expectedStartTime) {
            Log.e(TAG, "First task (${task.name}) start time is not 00:01. Actual start time: ${task.startTime.toLocalTime()}")
        }

        // Check if the ID is -1
        val expectedTaskID = -1
        if (task.id != expectedTaskID) {
            Log.e(TAG, "First task (${task.name}) has an unexpected ID. Expected: $expectedTaskID, Actual: ${task.id}")
        }

        return true
    }

    fun isTaskLast(task: TaskEntity): Boolean {
        val lastTask = fetchLastTask()
        if (lastTask == null) {
            Log.e(TAG, "Failed to get last task")
            return false
        }

        if (task.position != lastTask.position) {
            return false
        }

        // Check if the end time is 23:59
        val expectedEndTime = LocalTime.of(23, 59)
        if (task.endTime.toLocalTime() != expectedEndTime) {
            Log.e(TAG, "Last task (${task.name}) end time is not 23:59. Actual end time: ${task.endTime.toLocalTime()}")
        }

        // Check if the ID is -2
        val expectedTaskID = -2
        if (task.id != expectedTaskID) {
            Log.e(TAG, "Last task (${task.name}) has an unexpected ID. Expected: $expectedTaskID, Actual: ${task.id}")
        }

        return true
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun insertDemoTasks() {
        viewModelScope.launch {
            repository.initializeDemoTasks()
        }
    }
}
