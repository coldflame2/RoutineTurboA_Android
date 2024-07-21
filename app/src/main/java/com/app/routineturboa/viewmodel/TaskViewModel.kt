package com.app.routineturboa.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.routineturboa.data.local.RoutineRepository
import com.app.routineturboa.data.model.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

const val TAG = "TaskViewModel"

class TaskViewModel(private val repository: RoutineRepository) : ViewModel() {

    init {
        viewModelScope.launch { repository.initializeDefaultTasks() }
    }

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
                Log.d("TaskViewModel", "Updating task with ID: $existingTaskId. Task Name: ${task.taskName}")
                repository.updateTask(task.copy(id = existingTaskId))
            } else {
                repository.insertTask(task)
            }
        }
    }

    fun reloadDatabase(newDatabasePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Close the existing database connection
            repository.closeDatabase()

            // Copy the new database file to the app's database file
            val destinationFile = File(newDatabasePath).parentFile?.resolve("routine_database.db")
                ?: throw IllegalStateException("Unable to determine destination file")
            File(newDatabasePath).copyTo(destinationFile, overwrite = true)

            // Reopen the database
            repository.reopenDatabase()

            // The tasks StateFlow will automatically update with the new data
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            Log.d(TAG, "Updating task with ID: ${task.id}. Task Name: ${task.taskName}")
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
