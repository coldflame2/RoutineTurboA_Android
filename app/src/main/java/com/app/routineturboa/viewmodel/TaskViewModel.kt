package com.app.routineturboa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.routineturboa.data.local.RoutineRepository
import com.app.routineturboa.data.model.TaskEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: RoutineRepository) : ViewModel() {
    val tasks: StateFlow<List<TaskEntity>> = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val firstTaskId: StateFlow<Int?> = repository.getAllTasks()
        .map { tasks -> tasks.minByOrNull { it.startTime }?.id }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val lastTaskId: StateFlow<Int?> = repository.getAllTasks()
        .map { tasks -> tasks.maxByOrNull { it.startTime }?.id }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun handleSaveTask(task: TaskEntity, existingTaskId: Int?) {
        viewModelScope.launch {
            if (existingTaskId != null) {
                repository.updateTask(task.copy(id = existingTaskId))
            } else {
                repository.insertTask(task)
            }
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun isTaskFirst(task: TaskEntity): Boolean = task.id == firstTaskId.value
    fun isTaskLast(task: TaskEntity): Boolean = task.id == lastTaskId.value
}
