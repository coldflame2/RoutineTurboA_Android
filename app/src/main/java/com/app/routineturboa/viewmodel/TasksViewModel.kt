package com.app.routineturboa.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.routineturboa.data.room.entities.TaskEntity
import com.app.routineturboa.data.onedrive.MsalApp
import com.app.routineturboa.data.repository.AppRepository
import com.app.routineturboa.data.repository.TaskCreationOutcome
import com.app.routineturboa.data.room.entities.TaskCompletionHistory
import com.app.routineturboa.shared.states.ActiveUiComponent
import com.app.routineturboa.shared.states.TaskCreationState
import com.app.routineturboa.shared.states.UiState
import com.app.routineturboa.ui.models.TaskFormData
import com.app.routineturboa.utils.getBasicTasksList
import com.app.routineturboa.utils.getSampleTasksList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    private val tag = "TasksViewModel"

    // region: ----------------------- Tasks StateFlow -------------------------


    // MutableStateFlow to handle the UI state
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

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

    private suspend fun getFirstTask(): TaskEntity? {
        val firstTask = repository.getTaskAtPosition(1)
        return firstTask
    }

    // endregion


    // region: ----------------------- Utility Methods -------------------------

    // Helper function to create a TaskEntity from TaskFormData
    private fun createTaskEntityFromForm(
        taskFormData: TaskFormData, // Default to taskFormData's ID if not provided
        taskId: Int = taskFormData.id
    ): TaskEntity {
        Log.d(tag, "Creating TaskEntity from TaskFormData for task ${taskFormData.name}. Id:${taskFormData.id}")
        return TaskEntity(
            id = taskId,
            position = taskFormData.position,
            name = taskFormData.name,
            notes = taskFormData.notes,
            startTime = taskFormData.startTime,
            endTime = taskFormData.endTime,
            duration = taskFormData.duration,
            reminder = taskFormData.reminder,
            type = taskFormData.taskType,
            mainTaskId = taskFormData.mainTaskId,
            startDate = taskFormData.startDate,
            isRecurring = taskFormData.isRecurring,
            recurrenceType = taskFormData.recurrenceType,
            recurrenceInterval = taskFormData.recurrenceInterval,
            recurrenceEndDate = taskFormData.recurrenceEndDate
        )
    }

    fun insertSampleTasks() {
        Log.d(tag, "Inserting demo tasks...")
        val sampleTasks = getSampleTasksList(selectedDate = selectedDate.value)
        insertBasicOrSampleTasks(sampleTasks)
    }

    fun insertBasicTasks() {
        Log.d(tag, "Inserting basic tasks...")
        val basicTasks = getBasicTasksList(selectedDate = selectedDate.value)
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

    private suspend fun getTaskAtPosition(taskPosition: Int): TaskEntity? {
        return repository.getTaskAtPosition(taskPosition)
    }

    private suspend fun getTaskById(taskId: Int): TaskEntity? {
        return repository.getTaskById(taskId)
    }

    private suspend fun isTaskLast(task: TaskEntity): Boolean {
        return repository.isTaskLast(task)
    }

    suspend fun logAllTasks() {
        Log.d(tag, "**********Logging all tasks***********")
        val allTasksList = withContext(Dispatchers.IO) {
            repository.getAllTasksList()
        }

        // Log each task with a line break between them
        allTasksList.forEach { task ->
            Log.d(tag, "......")
            Log.d(tag, "$task")
        }
    }


    // endregion


    // region: ------------- On Database operations (Add, edit, and Delete) ----------------

    // Confirm a new task and add it to the database
    suspend fun onNewTaskConfirmClick(
        clickedTask: TaskEntity,
        taskBelowClickedTask: TaskEntity,
        newTaskFormData: TaskFormData,
    ): Result<TaskCreationOutcome> {
        Log.d(tag, "onNewTaskConfirmClick...")

        val newTaskEntity = createTaskEntityFromForm(taskFormData = newTaskFormData)
        val result = repository.beginNewTaskOperations(
            newTaskEntity, clickedTask, taskBelowClickedTask
        )

        updateStatesAfterNewTaskOperation(result, clickedTask)

        return result
    }

    /**
     * Steps:
     * - Create a new task entity from the form data.
     * - Begin new task operations by interacting with the repository.
     * - Update the UI state based on the result of the operation.
     * - Return the result of the task creation operation.
     */
    private suspend fun updateStatesAfterNewTaskOperation(
        result: Result<TaskCreationOutcome>,
        clickedTask: TaskEntity
    ) {
        Log.d(tag, "Updating States after New Task Operation...")
        result.fold(
            onSuccess = { taskCreationOutcome ->
                taskCreationOutcome.newTaskId?.let { newId ->
                    Log.d(tag, "Task confirmed successfully with ID: $newId")
                    val newTask = getTaskById(newId)

                    // Update the UI state
                    _uiState.update { currentState ->
                        currentState.copy(
                            uiTaskReferences = currentState.uiTaskReferences.copy(
                                clickedTask = newTask,
                                inEditTask = null,
                                latestTask = newTask,
                                taskBelowClickedTask = null
                            ),
                            activeUiComponent = ActiveUiComponent.None, // Update as needed
                            taskCreationState = TaskCreationState.Success(newId)
                        )
                    }

                    Log.d(tag, "Message containing updated Task Below: ${taskCreationOutcome.message}")
                } ?: run {
                    _uiState.update { currentState ->
                        currentState.copy(
                            uiTaskReferences = currentState.uiTaskReferences.copy(
                                clickedTask = clickedTask,
                                inEditTask = null,
                                latestTask = null,
                                taskBelowClickedTask = null
                            ),
                            activeUiComponent = ActiveUiComponent.None, // Update as needed
                            taskCreationState = TaskCreationState.Error("Error")
                        )
                    }
                }
            },

            onFailure = { exception ->
                _uiState.update { currentState ->
                    currentState.copy(
                        uiTaskReferences = currentState.uiTaskReferences.copy(
                            clickedTask = clickedTask,
                            inEditTask = null,
                            latestTask = null,
                            taskBelowClickedTask = null
                        ),
                        activeUiComponent = ActiveUiComponent.None, // Update as needed
                        taskCreationState = TaskCreationState.Error(exception.message.toString())
                    )
                }
            }
        )
    }

    // Confirm an edit for an existing task
    suspend fun onUpdateTaskConfirmClick(updatedTaskFormData: TaskFormData) {
        Log.d(tag, "OnUpdateTaskConfirm Clicked during edit. Id: ${updatedTaskFormData.id}. Name: ${updatedTaskFormData.name}")

        val updatedTaskEntity = createTaskEntityFromForm(taskFormData = updatedTaskFormData)
        val result = repository.onEditUpdateTaskCurrentAndBelow(updatedTaskEntity)

        Log.d(tag, "Id: ${updatedTaskEntity.id}")
        if (result.isSuccess) {
            val taskOperationResult = result.getOrNull()
            val success = taskOperationResult?.success
            val message = taskOperationResult?.message

            Log.d(tag, "success: $success. Message: $message")
        }

        // Update the UI state
        _uiState.update { currentState ->
            currentState.copy(
                uiTaskReferences = currentState.uiTaskReferences.copy(
                    clickedTask = updatedTaskEntity,
                    inEditTask = null,
                    latestTask = null,
                    taskBelowClickedTask = null
                ),
                activeUiComponent = ActiveUiComponent.None, // Update as needed
                taskCreationState = TaskCreationState.Idle
            )
        }
    }

    // Delete a task from the database
    suspend fun onDeleteTaskConfirmClick(task: TaskEntity) {
        Log.d(tag, "Confirm task delete clicked...")
        task.let {
            Log.d(tag, "Deleting task: ${task.name}")
            repository.deleteTask(task)
            _uiState.update { currentState ->
                currentState.copy(
                    uiTaskReferences = currentState.uiTaskReferences.copy(
                        clickedTask = getFirstTask(),
                        inEditTask = null,
                        latestTask = null,
                        taskBelowClickedTask = null
                    ),
                    activeUiComponent = ActiveUiComponent.None, // Update as needed
                    taskCreationState = TaskCreationState.Idle
                )
            }
        }
    }

    // endregion


    // region: ----------------------- UI State Handling -------------------------

    // Handle task click events
    fun onTaskClick(taskClicked: TaskEntity) {
        // Some task in-edit
        if (_uiState.value.activeUiComponent is ActiveUiComponent.QuickEditOverlay) {
            // Task in-edit is same as task clicked
            if (taskClicked == _uiState.value.uiTaskReferences.inEditTask) {
                return // no changes are needed
            }

            // Task in-edit is not task clicked
            else {
                _uiState.update { currentState ->
                    currentState.copy(
                        uiTaskReferences = currentState.uiTaskReferences.copy(
                            clickedTask = taskClicked,
                            inEditTask = null,
                            latestTask = null,
                            taskBelowClickedTask = null
                        ),
                        activeUiComponent = ActiveUiComponent.None, // Update as needed
                        taskCreationState = TaskCreationState.Idle,
                        
                    )
                }

            }
        }

        // No Task in Edit Mode
        else {
            _uiState.update { currentState ->
                currentState.copy(
                    uiTaskReferences = currentState.uiTaskReferences.copy(
                        clickedTask = taskClicked,
                        inEditTask = null,
                        latestTask = null,
                        taskBelowClickedTask = null
                    ),
                    activeUiComponent = ActiveUiComponent.None, // Update as needed
                    taskCreationState = TaskCreationState.Idle,
                    
                )
            }

        }
    }

    // Show the UI to add a new task
    suspend fun onShowAddNewTaskClick() {
        Log.d(tag, "Show Add New Task Clicked")

        val clickedTask = _uiState.value.uiTaskReferences.clickedTask
        val posOfClickedTask = clickedTask?.position

        val posOfTaskBelow = posOfClickedTask?.plus(1)
        val taskBelow = posOfTaskBelow?.let { getTaskAtPosition(it) }

        // if no clicked Task or no task below clicked, don't show addNew Task screen
        if (clickedTask != null) {
            if (taskBelow != null) {
                _uiState.update { currentState ->
                    currentState.copy(
                        uiTaskReferences = currentState.uiTaskReferences.copy(
                            taskBelowClickedTask = taskBelow
                        ),
                        activeUiComponent = ActiveUiComponent.AddingNew,
                        taskCreationState = TaskCreationState.Loading,
                        
                    )
                }
            }
        }
    }

    // Show Full Edit UI for a task
    suspend fun onShowFullEditClick(taskInEdit: TaskEntity) {
        val clickedTaskPosition = _uiState.value.uiTaskReferences.clickedTask?.position
        val taskBelowClickedTask = clickedTaskPosition?.let { getTaskAtPosition(it + 1) }

        if (taskBelowClickedTask != null) {
            Log.d(tag, "FullEdit: Task below clicked task: $taskBelowClickedTask")
            _uiState.update { currentState ->
                currentState.copy(
                    uiTaskReferences = currentState.uiTaskReferences.copy(
                        inEditTask = taskInEdit,
                        taskBelowClickedTask = taskBelowClickedTask,
                        clickedTask = taskInEdit
                    ),
                    activeUiComponent = ActiveUiComponent.FullEditing,
                    
                )
            }
        } else {
            Log.d(tag, "FullEdit: No Task below. This is the last task.")
            // Handle the case for the last task (could add additional logic here if needed)
            _uiState.update { currentState ->
                currentState.copy(
                    uiTaskReferences = currentState.uiTaskReferences.copy(
                        inEditTask = taskInEdit,
                        clickedTask = taskInEdit,
                        taskBelowClickedTask = null
                    ),
                    activeUiComponent = ActiveUiComponent.FullEditing,
                    

                )
            }
        }
    }

    // Show Quick Edit UI for a task
    suspend fun onShowQuickEditClick(task: TaskEntity) {
        Log.d(tag, "Quick edit click for task: $task")

        val clickedTaskPosition = _uiState.value.uiTaskReferences.clickedTask?.position
        val taskBelowPosition = clickedTaskPosition?.plus(1)
        val taskBelowClickedTask = taskBelowPosition?.let { getTaskAtPosition(it) }

        // Update the UI state
        _uiState.update { currentState ->
            currentState.copy(
                uiTaskReferences = currentState.uiTaskReferences.copy(
                    inEditTask = task,
                    clickedTask = task,
                    taskBelowClickedTask = taskBelowClickedTask
                ),
                activeUiComponent = ActiveUiComponent.QuickEditOverlay,
                taskCreationState = TaskCreationState.Idle,
                
            )
        }
    }


    // Show task details
    fun onTaskDetailsClick(task: TaskEntity) {
        Log.d(tag, "Show task details clicked: $task")
        _uiState.update { currentState ->
            currentState.copy(
                uiTaskReferences = currentState.uiTaskReferences.copy(
                    showingDetailsTask = task
                ),
                activeUiComponent = ActiveUiComponent.DetailsView,
                
            )
        }
    }

    fun onShowCompletedTasksClick() {
        Log.d(tag, "Show completed tasks clicked")
        _uiState.update { currentState ->
            currentState.copy(
                activeUiComponent = ActiveUiComponent.FinishedTasks,
                
            )
        }
    }

    fun onTaskLongPress(task: TaskEntity) {
        Log.d(tag, "Long press on task: $task")
        onTaskClick(task)  // Handle the click on the task

        _uiState.update { currentState ->
            currentState.copy(
                uiTaskReferences = currentState.uiTaskReferences.copy(
                    longPressedTask = task
                ),
                
            )
        }
    }

    fun resetTaskCreationState() {
        _uiState.update { currentState ->
            currentState.copy(
                taskCreationState = TaskCreationState.Idle,
                
            )
        }
    }

    fun onCancelClick() {
        viewModelScope.launch {
            val firstTask = getFirstTask()
            // Optionally delay the reset for 3 seconds, if required
            delay(200)

            Log.d(tag, "Cancel clicked")

            _uiState.update { currentState ->
                currentState.copy(
                    activeUiComponent = ActiveUiComponent.None,
                    uiTaskReferences = currentState.uiTaskReferences.copy(
                        clickedTask = firstTask,  // Reset to the first task
                        longPressedTask = null,
                        taskBelowClickedTask = null,
                        inEditTask = null,
                        showingDetailsTask = null
                    ),
                    
                )
            }
        }
    }

    // Date Picker
    fun onDatePickerClick() {
        Log.d(tag, "Date picker clicked")
        _uiState.update { currentState ->
            currentState.copy(
                activeUiComponent = ActiveUiComponent.DatePicker,
                
            )
        }
    }

    // set the selected Date
    fun onDateChangeClick(date: LocalDate) {
        Log.d(tag, "Date changed to: $date")

        _uiState.update { currentState ->
            currentState.copy(
                activeUiComponent = ActiveUiComponent.None,  // Reset active component after date change

                
            )
        }

        _selectedDate.value = date  // Update the selected date in the ViewModel
    }

    fun setUiStateToDefault() {
        _uiState.value = _uiState.value.copy(
            activeUiComponent = ActiveUiComponent.None,
            )
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