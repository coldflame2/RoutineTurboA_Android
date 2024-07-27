package com.app.routineturboa.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.routineturboa.data.local.RoutineRepository
import com.app.routineturboa.data.model.TaskEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.time.LocalTime

const val TAG = "TasksViewModel"

class TasksViewModel(private val repository: RoutineRepository) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init {
        viewModelScope.launch {
            repository.initializeDefaultTasks()
        }
    }

    fun refreshTasks() {
        Log.d(TAG, "Refreshing tasks in ViewModel...")
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                repository.refreshTasks()
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing tasks", e)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    val tasks: StateFlow<List<TaskEntity>> = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getTaskBelow(clickedTask: TaskEntity): TaskEntity? {
        Log.d(TAG, "Getting task below clickedTask...")
        Log.d(TAG, "clickedTask: $clickedTask")

        viewModelScope.launch {
            repository.refreshTasks()
        }

        if (isTaskLast(clickedTask)){
            return null
        }

        val currentTasks = tasks.value
        val clickedTaskIndex = currentTasks.indexOf(clickedTask)
//        Log.d("getTaskBelow", "Clicked Task Index: $clickedTaskIndex")

        return if (clickedTaskIndex != -1 && clickedTaskIndex < currentTasks.size - 1) {
            val nextTask = currentTasks[clickedTaskIndex + 1]
            Log.d("getTaskBelow", "Next Task: ${nextTask.taskName}")
            nextTask
        } else {
            Log.d("getTaskBelow", "No task found below or clicked task is not in the list.")
            null // Return null if the clicked task is not found or is at the bottom
        }
    }


    private fun insertTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    /**
     * This function begins the new task operations. The order of operations matter.
     * # Function: beginNewTaskOperations
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

                    val taskBelowToBeShifted = getTaskBelow(clickedTask)
                        ?: return@runAsTransaction Result.failure(Exception("Task below clicked task not found"))

                    Log.d(TAG, " **** Clicked task: '${clickedTask.taskName}', " +
                            "New task: '${newTask.taskName}'," +
                            "Task Below: '${taskBelowToBeShifted.taskName}'")

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
                    updateTaskPositions(clickedTask)

                    // 3. Inserting new task...
                    Log.d(TAG, " **** 3rd Step) Inserting new task...")
                    insertTask(newTask)

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
                        updateTask(taskBelowNowShiftedUpdated)
                    } else {
                        Log.d(TAG, "Task Below is the last task. Updating with new duration and start Time.")
                        val taskBelowNowShiftedUpdated = taskBelowToBeShifted.copy(
                            duration = updatedDuration,
                            startTime = updatedStartTime
                        )
                        updateTask(taskBelowNowShiftedUpdated)
                    }

                    Result.success(true)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error inserting task", e)
                Result.failure(e)
            }
        }
    }

    private fun updateTaskBelowAfterEdit(editedTask: TaskEntity) {
        Log.d(TAG, "Updating the task below after editing '${editedTask.taskName}' task. ")

        viewModelScope.launch {
            try {
                repository.runAsTransaction {
                    // Find the task below the edited task
                    val taskBelow = getTaskBelow(editedTask)
                    if (taskBelow == null) {
                        Log.d(TAG, "No task found below the edited task")
                        return@runAsTransaction
                    }

                    Log.d(TAG, "Task below: ${taskBelow.taskName}")

                    val newStartTime = editedTask.endTime

                    // Calculate the new duration for the task below
                    val newDuration = Duration.between(newStartTime, taskBelow.endTime)

                    // Create an updated task entity with the new duration and start time
                    val updatedTaskBelow = taskBelow.copy(
                        duration = newDuration.toMinutes().toInt(),
                        startTime = newStartTime
                    )

                    // Log the updated task details
                    Log.d(TAG, "Updated task below: ${updatedTaskBelow.taskName}, " +
                            "old duration: ${taskBelow.duration}, new duration: ${updatedTaskBelow.duration}, " +
                            "old startTime: ${taskBelow.startTime}, new start time: ${updatedTaskBelow.startTime}")

                    // Update the task below in the repository
                    repository.updateTask(updatedTaskBelow)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating task below after editing task", e)
            }
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            Log.d(TAG, "Updating task '${task.taskName}'. task ID: ${task.id}")
            repository.updateTask(task)
            updateTaskBelowAfterEdit(task)
        }
    }

    private fun updateTaskPositions(clickedTask: TaskEntity?) {

        if (clickedTask == null) {
            Log.e(TAG, "updateTaskPositions: clickedTask is null")
            return
        }

        val taskBelowClickedTask = getTaskBelow(clickedTask)

        if (isTaskLast(taskBelowClickedTask!!)) {
            Log.e(TAG, "updateTaskPositions: taskBelowClickedTask is last task. Skipping updating positions.")
            return
        }

        viewModelScope.launch {
            Log.d(TAG, "Updating task positions below clickedTask: ${clickedTask.taskName}")

            val clickedTaskPosition = clickedTask.position

            val currentTasks = tasks.value

            // Find the index of the clicked task
            val clickedTaskIndex = currentTasks.indexOfFirst { it.position == clickedTaskPosition }

            // Get the position of the last task using isTaskLast
            val lastTask = currentTasks.find { isTaskLast(it) }
            Log.d(TAG, "Last task in the list: ${lastTask?.taskName}")
            val maxPosition = lastTask?.position ?: Int.MAX_VALUE
            Log.d(TAG, "lastTaskPosition: $maxPosition")

            // Exclude the last task from the update
            val tasksToUpdate = if (clickedTaskIndex < currentTasks.size - 1) {
                currentTasks.subList(clickedTaskIndex + 1, currentTasks.size)
                    .filterNot { isTaskLast(it) }
            } else {
                emptyList()
            }

            Log.d(TAG, "Maximum position in the list: $maxPosition")
            Log.d(TAG, "Tasks to update (name:current position): ${tasksToUpdate.map { "${it.taskName}:${it.position}" }}")


            var newPosition = clickedTaskPosition + 1

            val updatedTasks = tasksToUpdate.map { task ->
                newPosition++
                val updatedTask = task.copy(position = newPosition)
                Log.d(TAG, "Updating task '${task.taskName}' position from ${task.position} to ${updatedTask.position}")
                updatedTask
            }

            Log.d(TAG, "Updated tasks: ${updatedTasks.map { "${it.taskName}:${it.position}" }}")

            if (updatedTasks.isNotEmpty()) {
                // Update all affected tasks in a single transaction
                val updateResult = repository.updateTasksWithNewPositions(updatedTasks)
                Log.d(TAG, "Update result: $updateResult")
            } else {
                Log.d(TAG, "No tasks to update")
            }
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    private fun getLastTask(): TaskEntity? {
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
            Log.e(TAG, "Last task (${lastTask.taskName}) end time is not 23:59. Actual end time: ${lastTask.endTime}")
        }
        val expectedTaskID = -2
        if (lastTask.id != expectedTaskID) {
            Log.e(TAG, "Last task (${lastTask.taskName}) has an unexpected ID. Expected: $expectedTaskID, Actual: ${lastTask.id}")
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
            Log.e(TAG, "First task (${task.taskName}) start time is not 00:01. Actual start time: ${task.startTime.toLocalTime()}")
        }

        // Check if the ID is -1
        val expectedTaskID = -1
        if (task.id != expectedTaskID) {
            Log.e(TAG, "First task (${task.taskName}) has an unexpected ID. Expected: $expectedTaskID, Actual: ${task.id}")
        }

        return true
    }

    fun isTaskLast(task: TaskEntity): Boolean {
        val lastTask = getLastTask()
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
            Log.e(TAG, "Last task (${task.taskName}) end time is not 23:59. Actual end time: ${task.endTime.toLocalTime()}")
        }

        // Check if the ID is -2
        val expectedTaskID = -2
        if (task.id != expectedTaskID) {
            Log.e(TAG, "Last task (${task.taskName}) has an unexpected ID. Expected: $expectedTaskID, Actual: ${task.id}")
        }

        return true
    }

    fun insertDemoTasks() {
        viewModelScope.launch {
            repository.initializeDemoTasks()
        }
    }
}
