package com.app.routineturboa.viewmodel

import android.util.Log
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

    fun deleteTask(task: Task) {
        Log.d("TaskViewModel", "Deleting task: ${task.taskName}")
        viewModelScope.launch {
            //Delete the task
            repository.deleteTask(task)
            updatePositions(task.position)
            loadTasks()
        }

    }
    fun updateTask(task: Task) {
        Log.d("TaskViewModel", "Updating task: ${task.taskName}")
        viewModelScope.launch {
            repository.updateTask(task)
            loadTasks()
        }
    }

    private fun updatePositions(startPosition: Int) {
        Log.d("TaskViewModel", "Updating positions from $startPosition")
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

    fun handleSaveTask(
        newTask: Task,
        selectedTaskForDisplay: Task?
    ) {
        Log.d("MainScreen", "Handling save task: ${newTask.taskName}")
        val currentTasks = _tasks.value
        selectedTaskForDisplay?.let { selectedTask ->
            val newStartTime = selectedTask.endTime
            newTask.startTime = newStartTime
            newTask.endTime = TimeUtils.addDurationToTime(newStartTime, newTask.duration)
            updatePositions(selectedTask.position + 1)
            newTask.position = selectedTask.position + 1
            addTask(newTask)
            adjustSubsequentTasks(newTask.position, newTask.endTime)
        } ?: run {
            if (currentTasks.isNotEmpty()) {
                val lastTask = currentTasks.last()
                newTask.startTime = lastTask.endTime
                newTask.endTime = TimeUtils.addDurationToTime(newTask.startTime, newTask.duration)
                newTask.position = currentTasks.size + 1
                addTask(newTask)
            } else {
                newTask.startTime = "08:00 AM"
                newTask.endTime = TimeUtils.addDurationToTime(newTask.startTime, newTask.duration)
                newTask.position = 1
                addTask(newTask)
            }
        }
        Log.d("MainScreen", "Task saved: ${newTask.taskName}")
    }
}
