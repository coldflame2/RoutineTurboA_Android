package com.app.routineturboa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.routineturboa.data.local.RoutineRepository
import com.app.routineturboa.data.model.Task
import com.app.routineturboa.utils.TimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: RoutineRepository) : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            val taskList = repository.getAllTasks()
            _tasks.value = taskList
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.addTask(task)
            loadTasks()
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
            loadTasks()
        }
    }

    fun updatePositions(startPosition: Int) {
        viewModelScope.launch {
            repository.updatePositions(startPosition)
            loadTasks()
        }
    }

    fun adjustSubsequentTasks(startPosition: Int, endTime: String) { // Adjust subsequent tasks
        viewModelScope.launch {
            val tasks = repository.getAllTasks()
            var previousEndTime = endTime
            for (task in tasks.filter { it.position > startPosition }) {
                task.startTime = previousEndTime
                task.endTime = TimeUtils.addDurationToTime(task.startTime, task.duration)
                previousEndTime = task.endTime
                repository.updateTask(task)
            }
            loadTasks()
        }
    }
}
