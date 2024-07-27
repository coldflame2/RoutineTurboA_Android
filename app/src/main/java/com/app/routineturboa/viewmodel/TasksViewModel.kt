package com.app.routineturboa.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.routineturboa.data.local.RoutineRepository
import com.app.routineturboa.data.model.TaskEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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

    fun getTaskAbove(clickedTask: TaskEntity): TaskEntity? {
        val currentTasks = tasks.value
        val clickedTaskIndex = currentTasks.indexOf(clickedTask)

        return if (clickedTaskIndex > 0) {
            currentTasks[clickedTaskIndex - 1]
        } else {
            null // Return null if the clicked task is already at the top
        }
    }

    fun getTaskBelow(clickedTask: TaskEntity): TaskEntity? {
        val currentTasks = tasks.value
        val clickedTaskIndex = currentTasks.indexOf(clickedTask)

        return if (clickedTaskIndex < currentTasks.size - 1) {
            currentTasks[clickedTaskIndex + 1]
        } else {
            null // Return null if the clicked task is already at the bottom
        }
    }

    private val firstTaskId: StateFlow<Int?> = repository.getAllTasks()
        .map { tasks -> tasks.minByOrNull { it.startTime }?.id }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val lastTaskId: StateFlow<Int?> = repository.getAllTasks()
        .map { tasks -> tasks.maxByOrNull { it.startTime }?.id }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun insertTask(task: TaskEntity) {
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
                    Log.d(TAG, "Starting new task operations. Clicked task: ${clickedTask.taskName}, New task: '${newTask.taskName}'")

                    // 1. Validate duration
                    Log.d(TAG, "Validating duration...")
                    val taskBelowToBeShifted = getTaskBelow(clickedTask)
                        ?: throw IllegalStateException("Task below clicked task not found")
                    Log.d(TAG, "taskBelowToBeShifted: ${taskBelowToBeShifted.taskName}")

                    if (newTask.duration >= taskBelowToBeShifted.duration)  {
                        return@runAsTransaction Result.failure(Exception("Invalid new task duration"))
                    }

                    Log.d(TAG, "Task below position before updating: ${taskBelowToBeShifted.position}")

                    // 2. Update Positions of all the tasks below the new Task
                    Log.d(TAG, "Updating positions...")
                    updateTaskPositions(clickedTask)

                    // 3. Inserting new task...
                    insertTask(newTask)


                    // 4. Updating duration and startTime of shifted task
                    val updatedDuration = taskBelowToBeShifted.duration - newTask.duration
                    val updatedStartTime = newTask.endTime
                    val taskBelowNowShifted = taskBelowToBeShifted.copy(
                        duration = updatedDuration,
                        startTime = updatedStartTime,
                        position = newTask.position + 1
                    )

                    Log.d(TAG, "taskBelowNowShifted Position before update: ${taskBelowNowShifted.position}")

                    updateTask(taskBelowNowShifted)

                    Result.success(true)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error inserting task", e)
                Result.failure(e)
            }
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            Log.d(TAG, "Updating task '${task.taskName}'. task ID: ${task.id}")
            repository.updateTask(task)
        }
    }

    private fun updateTaskPositions(clickedTask: TaskEntity?) {

        if (clickedTask == null) {
            Log.e(TAG, "updateTaskPositions: clickedTask is null")
            return
        }

        viewModelScope.launch {
            Log.d(TAG, "Updating task positions below clickedTask: ${clickedTask.taskName}")

            val clickedTaskPosition = clickedTask.position

            val currentTasks = tasks.value
            val clickedTaskIndex = currentTasks.indexOfFirst { it.position == clickedTaskPosition }

            // Exclude the last task from the update
            val tasksToUpdate = if (clickedTaskIndex < currentTasks.size - 2) {
                currentTasks.subList(clickedTaskIndex + 1, currentTasks.size - 1)
            } else {
                emptyList()
            }

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

    fun isTaskFirst(task: TaskEntity): Boolean = task.id == firstTaskId.value

    fun isTaskLast(task: TaskEntity): Boolean = task.id == lastTaskId.value

    fun insertDemoTasks() {
        viewModelScope.launch {
            repository.initializeDemoTasks()
        }
    }
}
