package com.app.routineturboa.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.routineturboa.data.room.TaskEntity
import com.app.routineturboa.data.onedrive.MsalApp
import com.app.routineturboa.data.repository.AppRepository
import com.app.routineturboa.data.repository.TaskOperationResult
import com.app.routineturboa.data.room.TaskCompletionHistory
import com.app.routineturboa.shared.TasksBasedOnState
import com.app.routineturboa.shared.UiStates
import com.app.routineturboa.ui.models.TaskFormData
import com.app.routineturboa.utils.getBasicTasksList
import com.app.routineturboa.utils.getSampleTasksList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.concurrent.Task
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    private val tag = "TasksViewModel"

    // region: ----------------------- Tasks StateFlow -------------------------

    // MutableStateFlow to handle the UI state
    private val _uiStates = MutableStateFlow(UiStates())
    val uiStates: StateFlow<UiStates> = _uiStates.asStateFlow()

    private val _tasksBasedOnState = MutableStateFlow(TasksBasedOnState())
    val tasksBasedOnState: StateFlow<TasksBasedOnState> = _tasksBasedOnState.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> get() = _selectedDate

    // Flow to observe all tasks
    private val allTasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Flow for tasks filtered by selected date
    @OptIn(ExperimentalCoroutinesApi::class)
    val tasksByDate: StateFlow<List<TaskEntity>> = _selectedDate
        .onEach { selectedDate ->
            Log.d(tag, "Selected date changed: $selectedDate")
        }
        .flatMapLatest { date ->
            repository.getTasksForDate(date)
                .onEach { tasks ->
                    Log.d(tag, "Tasks emitted for date $date: ${tasks.size} tasks")
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // endregion


    // region: ------------- On Database operations (Add, edit, and Delete) ----------------

    // Confirm a new task and add it to the database
    suspend fun onNewTaskConfirmClick(
        newTaskFormData: TaskFormData,
        clickedTask: TaskEntity,
        taskBelowClickedTask: TaskEntity
    ): Result<TaskOperationResult> {
        Log.d(tag, "onNewTaskConfirmClick...")
        Log.d(tag,
        "clickedTask: ${clickedTask.name}. Id: ${clickedTask.id}. Position: ${clickedTask.position}")
         Log.d(tag,
             "NewTaskFormData: ${newTaskFormData.name}. Position: ${newTaskFormData.position}")

        Log.d(tag,
            "belowClickedTask: ${taskBelowClickedTask.name}. Id: ${taskBelowClickedTask.id}. Initial Position: ${taskBelowClickedTask.position}")

        val newTaskEntity = createTaskEntityFromForm(newTaskFormData)
        val result = repository.beginNewTaskOperations(newTaskEntity, clickedTask, taskBelowClickedTask)

        // Set clickedTask as NewTask and other UI states updates
        if (result.isSuccess) {
            val taskOperationResult = result.getOrNull()
            val newTaskId = taskOperationResult?.newTaskId
            val newTask = newTaskId?.let { repository.getTaskEntityById(newTaskId) }

            // update ui state
            _uiStates.update { it.copy(
                    isAddingNew = false,
                    isFullEditing = false) }

            _tasksBasedOnState.update{
                it.copy (
                    clickedTask = newTask,
                    longPressedTask = null,
                    taskBelowClickedTask = null,
                    inEditTask = null,
                    showingDetailsTask = null
                )
            }
        }

        // on result.failure: regular UiState and tasksBasedOnState updates
        else {
            // update ui state
            _uiStates.update { it.copy(
                isAddingNew = false,
                isFullEditing = false) }

            _tasksBasedOnState.update{
                it.copy (
                    clickedTask = clickedTask,
                    longPressedTask = null,
                    taskBelowClickedTask = null,
                    inEditTask = null,
                    showingDetailsTask = null
                )
            }
        }

        return result
    }

    // Confirm an edit for an existing task
    suspend fun onUpdateTaskConfirmClick(task: TaskEntity, updatedTaskFormData: TaskFormData) {
        Log.d(tag, "Confirm task edit clicked...")

        val updatedTaskEntity = createTaskEntityFromForm(updatedTaskFormData)
        repository.onEditUpdateTaskCurrentAndBelow(updatedTaskEntity)
        _uiStates.update {
            it.copy(
                isQuickEditing = false,
                isFullEditing = false,
            )
        }

        _tasksBasedOnState.update {
            it.copy(
                inEditTask = null,
            )
        }
    }

    // Delete a task from the database
    suspend fun onDeleteTaskConfirmClick(task: TaskEntity) {
        Log.d(tag, "Confirm task delete clicked...")
        task.let {
            Log.d(tag, "Deleting task: ${task.name}")
            repository.deleteTask(task)

            _uiStates.update {
                it.copy(
                    isQuickEditing = false,
                    isFullEditing = false
                )
            }
        }
    }

    // endregion


    // region: ----------------------- Utility Methods -------------------------

    // Helper function to create a TaskEntity from TaskFormData
    fun createTaskEntityFromForm(form: TaskFormData): TaskEntity {
        Log.d(tag, "Creating TaskEntity from TaskFormData")
        return TaskEntity(
            position = form.position,
            name = form.name,
            notes = form.notes,
            startTime = form.startTime,
            endTime = form.endTime,
            duration = form.duration,
            reminder = form.reminder,
            type = form.taskType,
            mainTaskId = form.mainTaskId,
            startDate = form.startDate,
            isRecurring = form.isRecurring,
            recurrenceType = form.recurrenceType,
            recurrenceInterval = form.recurrenceInterval,
            recurrenceEndDate = form.recurrenceEndDate
        )
    }

    fun insertSampleTasks() {
        Log.d(tag, "Inserting demo tasks...")
        val sampleTasks = getSampleTasksList(selectedDate = selectedDate.value)
        insertBasicOrSampleTasks(sampleTasks)
    }

    fun insertBasicTasks() {
        Log.d(tag, "Inserting basic tasks...")
        val basicTasks = getBasicTasksList()
        insertBasicOrSampleTasks(basicTasks)
    }

    private fun insertBasicOrSampleTasks(tasks: List<TaskEntity>) {
        Log.d(tag, "Inserting basic/sample tasks...")
        viewModelScope.launch {
            try {
                val result = repository.resetWithDemoOrDefaultTasks(tasks)
                if (result.isSuccess) {
                    Log.d(tag, "Demo tasks inserted successfully.")
                } else {
                    Log.e(tag, "Failed to insert demo tasks: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Log.e(tag, "Unexpected error inserting demo tasks: ${e.message}")
            }
        }
    }

    // Sync tasks from OneDrive
    fun syncTasksFromOneDrive(context: Context) {
        Log.d(tag, "Syncing tasks from OneDrive...")
        viewModelScope.launch {
            try {
                val authManager = MsalApp.getInstance(context)
                val authResult = authManager.signIn(context as Activity)
                authResult.let {
                    repository.syncTasksFromOneDrive(it, context)
                }
            } catch (e: Exception) {
                Log.e(tag, "Error syncing tasks from OneDrive: ${e.message}")
            }
        }
    }

    // Delete all tasks from the database
    fun deleteAllTasks() {
        Log.d(tag, "Deleting all tasks...")
        viewModelScope.launch { repository.deleteAllTasks() }
        Log.d(tag, "All tasks deleted.")
    }

    // Get task name by task ID
    suspend fun getTaskName(taskId: Int): String? {
        return repository.getTaskName(taskId)
    }

    suspend fun getTaskAtPosition(taskPosition: Int): TaskEntity? {
        return repository.getTaskAtPosition(taskPosition)
    }

    // endregion


    // region: ----------------------- UI State Handling -------------------------

    // Handle task click events
    fun onTaskClick(taskClicked: TaskEntity) {
        Log.d(tag, "Clicked on task: ${taskClicked.name}")

        // Some task in-edit
        if (uiStates.value.isQuickEditing) {
            // Task in-edit is same as task clicked
            if (taskClicked == tasksBasedOnState.value.inEditTask) {
                Log.d(tag, "Clicked-task is same as in-edit task: ${taskClicked.name}")
                return // no changes are needed
            }

            // Task in-edit is not task clicked
            else {
                _uiStates.update { it.copy(
                    isQuickEditing = false,
                    isFullEditing = false,
                    isShowingDetails = false,
                    isShowingCompletedTasks = false,
                    isAddingNew = false,
                    isShowingDatePicker = false
                ) }

                _tasksBasedOnState.update {
                    it.copy(
                        clickedTask = it.inEditTask,
                        inEditTask = null,
                    )
                }
            }
        }

        // No Task in Edit Mode
        else {
            _uiStates.update { it.copy (
                isQuickEditing = false,
                isFullEditing = false,
                isShowingDetails = false,
                isShowingCompletedTasks = false,
                isAddingNew = false,
            ) }

            _tasksBasedOnState.update {
                it.copy(
                    clickedTask = taskClicked,
                    longPressedTask = null,
                    taskBelowClickedTask = null,
                    inEditTask = null,
                    showingDetailsTask = null
                )
            }
        }
    }

    // Show the UI to add a new task
    suspend fun onShowAddNewTaskClick() {
        Log.d(tag, "Show Add New Task Clicked")
        val clickedTaskPosition = _tasksBasedOnState.value.clickedTask?.position
        val taskBelowPosition = clickedTaskPosition?.plus(1)
        val taskBelowClickedTask = taskBelowPosition?.let { getTaskAtPosition(it) }

        if (taskBelowClickedTask != null) {
            _uiStates.update { it.copy(isAddingNew = true) }
            _tasksBasedOnState.update { it.copy(taskBelowClickedTask = taskBelowClickedTask) }
        } else {
            _uiStates.update { it.copy(isAddingNew = false) }
            _tasksBasedOnState.update { it.copy(taskBelowClickedTask = null) }
        }
    }

    // Show Quick Edit UI for a task
    fun onQuickEditTask(task: TaskEntity) {
        Log.d(tag, "Quick edit click for task: $task")
        _uiStates.update {
            it.copy(
                isQuickEditing = true,
                isFullEditing = false,
                isShowingDetails = false,
                isShowingCompletedTasks = false,
                isAddingNew = false,
                isShowingDatePicker = false
            )
        }

        _tasksBasedOnState.update { it.copy(inEditTask = task) }
    }

    // Show Full Edit UI for a task
    suspend fun onFullEditClick(taskInEdit: TaskEntity) {
        val clickedTaskPosition = _tasksBasedOnState.value.clickedTask?.position
        val taskBelowClickedTask = clickedTaskPosition?.let { getTaskAtPosition(it+1) }

        if (taskBelowClickedTask != null) {
            Log.d(tag, "FullEdit: Task below clicked task: $taskBelowClickedTask")
            _uiStates.update { it.copy(isFullEditing = true, isQuickEditing = false) }
            _tasksBasedOnState.update { it.copy(inEditTask = taskInEdit, taskBelowClickedTask = taskBelowClickedTask) }
        }

        else {
            Log.d(tag, "FullEdit: No Task below. This is last task.")
            //#TODO: Handle the case of last task edit

            // for now, same as above condition
            _uiStates.update { it.copy(isFullEditing = true, isQuickEditing = false) }
            _tasksBasedOnState.update { it.copy(inEditTask = taskInEdit) }
        }
    }

    // Show task details
    fun onTaskDetailsClick(task: TaskEntity) {
        Log.d(tag, "Show task details clicked: $task")
        _uiStates.update { it.copy(isShowingDetails = true) }
        _tasksBasedOnState.update { it.copy(showingDetailsTask = task) }
    }

    fun onShowCompletedTasksClick() {
        Log.d(tag, "Show completed tasks clicked")
        _uiStates.update {
            it.copy(
                isShowingCompletedTasks = true,
                isQuickEditing = false,
                isFullEditing = false
            )
        }
    }

    // Handle long press events on a task
    fun onTaskLongPress(task: TaskEntity) {
        Log.d(tag, "Long press on task: $task")
        onTaskClick(task)
        _tasksBasedOnState.update {
            it.copy(longPressedTask = task)
        }
    }

    // Cancel task editing or viewing
    fun onCancelClick() {
        Log.d(tag, "Cancel clicked")
        _uiStates.update {
            it.copy(
                isQuickEditing = false,
                isFullEditing = false,
                isShowingDetails = false,
                isShowingCompletedTasks = false,
                isAddingNew = false,
                isShowingDatePicker = false
            )
        }
    }

    // show date picker
    fun onDatePickerClick(){
        Log.d(tag, "Date picker clicked")
        _uiStates.update {
            it.copy(isShowingDatePicker = true)
        }
    }

    // Updates the selected date in the UI state
    fun onDateChangeClick(date: LocalDate) {
        Log.d(tag, "Date changed to: $date")
        _uiStates.update { it.copy(isShowingDatePicker = false) }  // Update UiState to hide DatePicker
        _selectedDate.value = date  // Update the selected date in the ViewModel
    }


    // endregion


    // region: ---------------------- Task Completion Handling -------------------------

    // Flow for task completions
    private val _taskCompletions = MutableStateFlow<List<TaskCompletionHistory>>(emptyList())
    val taskCompletions: StateFlow<List<TaskCompletionHistory>> = _taskCompletions.asStateFlow()

    // Load task completions into the flow
    fun loadTaskCompletions() {
        Log.d(tag, "Loading task completions...")
        viewModelScope.launch {
            _taskCompletions.value = repository.getTasksWithCompletionStatus()
        }
    }

    // endregion
}