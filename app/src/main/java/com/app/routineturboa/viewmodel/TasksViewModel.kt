package com.app.routineturboa.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.routineturboa.data.room.TaskEntity
import com.app.routineturboa.data.onedrive.MsalApp
import com.app.routineturboa.data.repository.AppRepository
import com.app.routineturboa.data.room.TaskCompletionHistory
import com.app.routineturboa.ui.models.TaskFormData
import com.app.routineturboa.ui.models.TasksUiState
import com.app.routineturboa.utils.getDemoTasks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor (
    private val repository: AppRepository
) : ViewModel() {
    private val tag = "TasksViewModel"

    // StateFlow for all tasks
    val tasks: StateFlow<List<TaskEntity>> = repository.tasks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Define private internal Mutable state flow with TaskUiState data class
    private val _tasksUiState = MutableStateFlow(TasksUiState())
    // Expose UI state as a StateFlow to be collected by UI
    val tasksUiState: StateFlow<TasksUiState> = _tasksUiState.asStateFlow()

    /**
     * tasksByDate is a StateFlow that emits a list of tasks for the currently selected date.
     * It uses 'transformLatest' to listen to the changes in _selectedDate StateFlow.
     * It reacts to each new date by canceling any ongoing task fetch for the previous date.
     * It then collects the new tasks for the selected date.
     * Finally, it is converted to hot flow (StateFlow) itself using .stateIn
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val tasksByDate: StateFlow<List<TaskEntity>> = tasksUiState.transformLatest { uiState ->
        // Get the selected date from the TasksUiState
        val selectedDate = uiState.selectedDate

        if (selectedDate != null) {
            // Log or perform side-effects if necessary
            Log.d("TasksViewModel", "Fetching tasks for date: $selectedDate")

            // Fetch tasks for the selected date from the repository
            val tasksFlow = repository.getTasksForDate(selectedDate)

            // Collect tasks and emit them
            tasksFlow.collect { tasks ->
                emit(tasks)
            }
        } else {
            // If selectedDate is null, emit an empty list
            emit(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _taskCompletions = MutableStateFlow<List<TaskCompletionHistory>>(emptyList())
    val taskCompletions: StateFlow<List<TaskCompletionHistory>> = _taskCompletions

    fun onDateChange(date: LocalDate) {
        Log.d(tag, "Date changed: $date")
        _tasksUiState.update {
            it.copy(selectedDate = date)
        }
    }

    fun loadTaskCompletions() {
        Log.d(tag, "Loading completed tasks...")
        viewModelScope.launch {
            _taskCompletions.value = repository.getTasksWithCompletionStatus() // Fetch tasks with their completions
        }
    }

    // region: On clicks to update UI state like show dialog boxes


    fun onAnyTaskClick(taskId: Int) {
        Log.d(tag, "onAnyTaskClick: $taskId")
        _tasksUiState.update {
            it.copy(
                clickedTaskId = taskId,
                inEditTaskId = null,
                isQuickEditing = false,
                isFullEditing = false,
            )
        }
    }

    fun onAnyTaskLongPress(taskId: Int) {
        onAnyTaskClick(taskId)
        _tasksUiState.update {
            it.copy(
                longPressedTaskId = taskId,
            )
        }
    }

    fun onAddNewClick(){
        if (_tasksUiState.value.clickedTaskId != null) {
            _tasksUiState.update {
                it.copy(
                    isAddingNew = true,
                    taskBelowClickedTaskId = it.clickedTaskId?.plus(1)
                )
            }
        }

    }

    fun onQuickEditClick(taskId: Int) {
        Log.d(tag, "onQuickEditClick: $taskId")
        _tasksUiState.update {
            it.copy(
                clickedTaskId = taskId,
                inEditTaskId = taskId,
                isQuickEditing = true,
                isFullEditing = false,
            )
        }
    }

    fun onFullEditClick(taskId: Int) {
        Log.d(tag, "onFullEditClick: $taskId")
        _tasksUiState.update {
            it.copy(
                clickedTaskId = null,
                inEditTaskId = taskId,
                isQuickEditing = false,
                isFullEditing = true,
            )
        }
    }

    fun onShowTaskDetails(taskId: Int) {
        Log.d(tag, "onShowTaskDetails: $taskId")
        _tasksUiState.update {
            it.copy(
                isShowingDetails = true,
                showingDetailsTaskId = taskId,
                isQuickEditing = false,
                isFullEditing = false,
            )
        }
    }

    fun onShowCompletedTasks() {
        Log.d(tag, "onShowCompletedTasks")
        _tasksUiState.update {
            it.copy(
                isShowingDetails = false,
                isQuickEditing = false,
                isFullEditing = false,
                isShowingCompletedTasks = true,
            )
        }
    }

    fun onCancelEdit() {
        Log.d(tag, "onCancelEdit")
        _tasksUiState.update {
            it.copy(
                isAddingNew = false,
                isQuickEditing = false,
                isFullEditing = false,
                inEditTaskId = null,
                isShowingDetails = false,
                isShowingCompletedTasks = false,
            )
        }
    }

    // endregion

    // region: On clicks to update tasks database

    fun onConfirmNewTaskClick(newTaskFormData: TaskFormData) {
        Log.d(tag, "Confirm new task Clicked...")
        _tasksUiState.update {
            it.copy(
                isAddingNew = false,
                taskBelowClickedTaskId = it.clickedTaskId?.plus(1)
            )
        }
        beginNewTaskOperations(newTaskFormData)
    }

    fun onConfirmEdit(taskId: Int, updatedTaskFormData: TaskFormData) {
        Log.d(tag, "onConfirmEdit clicked...")

        updateTaskAndAdjustNext(taskId, updatedTaskFormData)

        _tasksUiState.update {
            it.copy(
                isAddingNew = false,
                isQuickEditing = false,
                isFullEditing = false,
                inEditTaskId = null,
            )
        }

    }

    fun onDeleteTask(taskId: Int? = _tasksUiState.value.clickedTaskId) {
        if (taskId == null) return
        val task = tasks.value.find { it.id == taskId } ?: return
        Log.d(tag, "Deleting task: ${task.name}")
        viewModelScope.launch {
            repository.deleteTask(task)
        }

        _tasksUiState.update {
            it.copy(
                inEditTaskId = null,
                isQuickEditing = false,
                isFullEditing = false,
            )
        }
    }

    // Functions to update tasks database and retrieve data

    fun syncTasksFromOneDrive(context: Context) {
        viewModelScope.launch {
            try {
                val authManager = MsalApp.getInstance(context)
                val authResult = authManager.signIn(context as Activity)
                authResult.let {
                    repository.syncTasksFromOneDrive(it, context)
                }
            } catch (e: Exception) {
                Log.e(tag, "Error syncing tasks from OneDrive", e)
            }
        }
    }

    private fun updateTaskAndAdjustNext(taskId: Int, updatedTaskFormData: TaskFormData) {
        Log.d(tag, "Updating task (id = $taskId) with new data...")
        viewModelScope.launch {
            // Create a TaskEntity from the updated form data
            val updatedTaskEntity = createTaskEntityFromForm(updatedTaskFormData).copy(id = taskId)
            // Call the repository to update the task
            repository.updateTaskAndAdjustNext(updatedTaskEntity)
        }
    }

    private fun beginNewTaskOperations(newTaskFormData: TaskFormData): LiveData<Result<Boolean>> {
        val resultLiveData = MutableLiveData<Result<Boolean>>()
        val clickedTaskEntity = tasks.value.find { it.id == _tasksUiState.value.clickedTaskId }
        val newTaskEntity = createTaskEntityFromForm(newTaskFormData)

        if (clickedTaskEntity != null) {
            viewModelScope.launch {
                val result = repository.beginNewTaskOperations(clickedTaskEntity, newTaskEntity)
                resultLiveData.postValue(result)
            }
        }
        return resultLiveData
    }

    private fun createTaskEntityFromForm(form: TaskFormData): TaskEntity {
        return TaskEntity(
            id = 0, // Will be overwritten with the correct ID when updating
            position = form.position,
            name = form.name,
            notes = form.notes,
            startTime = form.startTime,
            endTime = form.endTime,
            duration = form.duration,
            reminder = form.reminder,
            type = form.taskType,
            mainTaskId = form.mainTaskId
        )
    }


    fun deleteAllTasks() {
        Log.d(tag, "Deleting all tasks...")
        viewModelScope.launch {
            repository.deleteAllTasks()
        }
    }

    fun insertDemoTasks(context: Context) {
        Log.d(tag, "Inserting demo tasks.")
        viewModelScope.launch {
            Log.d(tag, "First, deleting the tasks...")
            deleteAllTasks()
            try {
                val demoTasks = getDemoTasks(context)
                demoTasks.forEach { task ->
                    repository.insertTaskWithDate(task, LocalDate.now())
                }
            } catch (e: Exception) {
                Log.e("TasksViewModel", "Error inserting demo tasks", e)
            } finally {
            }
        }
    }

    fun insertDefaultTasks() {
        Log.d(tag, "Initializing Default tasks.")
        viewModelScope.launch {
            try {
                val selectedDate = _tasksUiState.value.selectedDate
                repository.initializeDefaultTasks(selectedDate)
            } finally {
            }
        }
    }

    // Expose a method to get task name by taskId
    suspend fun getTaskName(taskId: Int): String? {
        return repository.getTaskName(taskId)
    }

}
