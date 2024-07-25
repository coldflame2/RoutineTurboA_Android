package com.app.routineturboa.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.routineturboa.data.local.RoutineRepository
import com.app.routineturboa.data.model.TaskEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

const val TAG = "TasksViewModel"

class TasksViewModel(private val repository: RoutineRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.initializeDefaultTasks()
        }
    }

    val tasks: StateFlow<List<TaskEntity>> = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            Log.d(TAG, "Updating task '${task.taskName}'. task ID: ${task.id}")
            repository.updateTask(task)
        }
    }

    fun updateTaskPositions(clickedTask: TaskEntity?) {
        viewModelScope.launch {
            val clickedTaskPosition = clickedTask?.position ?: run {
                Log.e(TAG, "updateTaskPositions: Clicked task is null")
                return@launch
            }

            val currentTasks = tasks.value
            val clickedTaskIndex = currentTasks.indexOfFirst { it.position == clickedTaskPosition }

            if (clickedTaskIndex == -1) {
                Log.e(TAG, "updateTaskPositions: Clicked task not found in the list")
                return@launch
            }

            val tasksToUpdate = currentTasks.subList(clickedTaskIndex + 1, currentTasks.size - 1)
            Log.d(TAG, "Tasks to update: ${tasksToUpdate.map { "${it.taskName}:${it.position}" }}")

            var newPosition = clickedTaskPosition
            val updatedTasks = tasksToUpdate.map { task ->
                newPosition++
                val updatedTask = task.copy(position = newPosition)
                Log.d(TAG, "Updating task '${task.taskName}' ID from ${task.position} to ${updatedTask.position}")
                updatedTask
            }

            Log.d(TAG, "Updated tasks: ${updatedTasks.map { "${it.taskName}:${it.position}" }}")

            // Update all affected tasks in a single transaction
            val updateResult = repository.updateTasksWithNewPositions(updatedTasks)

            Log.d(TAG, "Update result: $updateResult")

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
