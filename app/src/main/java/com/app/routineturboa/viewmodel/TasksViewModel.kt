package com.app.routineturboa.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.routineturboa.data.room.entities.TaskEntity
import com.app.routineturboa.data.onedrive.MsalApp
import com.app.routineturboa.data.repository.AppRepository
import com.app.routineturboa.data.room.entities.TaskCompletionHistory
import com.app.routineturboa.core.models.UiScreen
import com.app.routineturboa.core.models.TaskContext
import com.app.routineturboa.core.models.TaskOperationState
import com.app.routineturboa.core.models.TaskOperationType.EDIT_TASK
import com.app.routineturboa.core.models.TaskOperationType.NEW_TASK
import com.app.routineturboa.core.models.UiState
import com.app.routineturboa.ui.models.TaskFormData
import com.app.routineturboa.core.dbutils.TaskTypes
import com.app.routineturboa.core.models.MsalAuthState
import com.app.routineturboa.core.models.OnedriveSyncState
import com.app.routineturboa.core.models.SignInStatus
import com.app.routineturboa.core.utils.getBasicTasksList
import com.app.routineturboa.core.utils.getSampleTasksList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val repository: AppRepository,
    private val msalApp: MsalApp
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
            Log.d(tag, "Selected date: $selectedDate")
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
            type = taskFormData.type,
            linkedMainIfHelper = taskFormData.linkedMainIfHelper,
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

    private suspend fun getFirstTask(): TaskEntity? {
        val firstTask = repository.getTaskAtPosition(1)
        return firstTask
    }

    suspend fun onMainTasksRequested(): List<TaskEntity> {
        Log.d(tag, "Main tasks requested...")
        return repository.getAllMainTasks()
    }

    suspend fun onNameRequested(taskId: Int): String {
        return getTaskById(taskId)?.name ?: "--"
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

    private fun updateUiState(
        taskOperationState: TaskOperationState? = null,
        uiScreen: UiScreen? = null,
        msalAuthState: MsalAuthState? = null,
        onedriveSyncState: OnedriveSyncState? = null,
        taskContextUpdater: (TaskContext) -> TaskContext = { it }
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                taskOperationState = taskOperationState ?: currentState.taskOperationState,
                uiScreen = uiScreen ?: currentState.uiScreen,
                msalAuthState = msalAuthState ?: currentState.msalAuthState,
                onedriveSyncState = onedriveSyncState ?: currentState.onedriveSyncState,
                taskContext = taskContextUpdater(currentState.taskContext)
            )
        }
    }

    // endregion


    // region: ------------- Add New Task ----------------

    // Show the UI to add a new task
    suspend fun onShowAddNewTaskClick() {
        Log.d(tag, "Show Add New Task Clicked")

        val clickedTask = _uiState.value.taskContext.clickedTask
        val posOfClickedTask = clickedTask?.position

        val posOfTaskBelow = posOfClickedTask?.plus(1)
        val taskBelow = posOfTaskBelow?.let { getTaskAtPosition(it) }

        // if no clicked Task or no task below clicked, don't show addNew Task screen
        if (clickedTask != null) {
            if (taskBelow != null) {
                Log.d(tag, "Creating Initial form Data based on clickedTask.")
                val initialFormData = TaskFormData(
                    name = "New",
                    startTime = clickedTask.endTime,
                    endTime = clickedTask.endTime?.plusMinutes(1),
                    notes = "",
                    type = TaskTypes.QUICK,
                    linkedMainIfHelper = null,
                    position = posOfClickedTask + 1,
                    duration = 1,
                    reminder = clickedTask.endTime,
                    startDate = LocalDate.now(),
                    isRecurring = false,
                    recurrenceType = null,
                    recurrenceInterval = null,
                    recurrenceEndDate = null,
                )

                updateUiState(
                    uiScreen = UiScreen.AddingNew,
                    taskOperationState = TaskOperationState.FillingDetails(
                        operationType = NEW_TASK,
                        formData = initialFormData,
                    ),
                    taskContextUpdater = { taskContext ->
                        taskContext.copy(
                            clickedTask = clickedTask,
                            taskBelowClickedTask = taskBelow
                        )
                    }
                )

            } else {
                Log.e(tag, "No taskBelow")
            }
        } else {
            Log.e(tag, "No clicked Task.")
        }
    }

    /**
     * Steps:
     * - Create a new task entity from the form data.
     * - Begin new task operations by interacting with the repository.
     * - Update the UI state based on the result of the operation.
     * - Return the result of the task creation operation.
     */
    suspend fun onNewTaskConfirmClick(
        newTaskFormData: TaskFormData,
    ): TaskOperationState {

        Log.d(tag, "onNewTaskConfirmClick...")
        val clickedTask = _uiState.value.taskContext.clickedTask
        val taskBelow = _uiState.value.taskContext.taskBelowClickedTask

        if (clickedTask == null || taskBelow == null) return TaskOperationState.Error(
            operationType = NEW_TASK, taskId = -1, message = "No clickedTask and/or to insert")

        val toInsertNewTaskEntity = createTaskEntityFromForm(taskFormData = newTaskFormData)
        val returnedOperationState = repository.addNewAndUpdateBelow(
            toInsertNewTaskEntity, clickedTask, taskBelow
        )

        updateStatesAfterNewTaskOperation(returnedOperationState, clickedTask)

        return returnedOperationState
    }

    private suspend fun updateStatesAfterNewTaskOperation(
        returnedOperationState: TaskOperationState,
        clickedTask: TaskEntity
    ) {
        Log.d(tag, "Updating States after New Task Operation...")

        when (returnedOperationState) {

            is TaskOperationState.Error -> {
                Log.d(tag, "Task could not be added. Error message: ${returnedOperationState.message}")
                updateUiState(
                    taskOperationState = returnedOperationState,
                    uiScreen = UiScreen.None,
                    taskContextUpdater = { taskContext ->
                        taskContext.copy(clickedTask = clickedTask)
                    }
                )
            }

            is TaskOperationState.Success -> {
                Log.d(tag, "Task confirmed successfully with ID: ${returnedOperationState.taskId}")
                val newTask = getTaskById(returnedOperationState.taskId)

                updateUiState(
                    taskOperationState = returnedOperationState,
                    uiScreen = UiScreen.None,
                    taskContextUpdater = { taskContext ->
                        taskContext.copy(
                            clickedTask = clickedTask,
                            latestTasks = taskContext.latestTasks + listOfNotNull(newTask)
                        )
                    }
                )
            }

            else -> {
                Log.e(tag, "Error in taskOperationState after adding a new task,")
            }
        }
    }

    // endregion:


    // region: ------------- Editing Task ----------------

    // Show FullEdit Screen, Prepare TaskFormData
    suspend fun onShowFullEditClick() {
        val clickedTask = _uiState.value.taskContext.clickedTask
        if (clickedTask != null) {
            Log.d(tag, "Show Full Edit Clicked for task: ${clickedTask.name}")

            val initialFormData = TaskFormData(
                id = clickedTask.id,
                name = clickedTask.name,
                startTime = clickedTask.startTime,
                endTime = clickedTask.endTime,
                notes = clickedTask.notes,
                type = clickedTask.type,
                linkedMainIfHelper = clickedTask.linkedMainIfHelper,
                position = clickedTask.position,
                duration = clickedTask.duration,
                reminder = clickedTask.reminder,
                startDate = clickedTask.startDate,
                isRecurring = clickedTask.isRecurring?:false,
                recurrenceType = clickedTask.recurrenceType,
                recurrenceInterval = clickedTask.recurrenceInterval,
                recurrenceEndDate = clickedTask.recurrenceEndDate,
            )

            updateUiState(
                uiScreen = UiScreen.FullEditing,
                taskOperationState = TaskOperationState.FillingDetails(
                    operationType = EDIT_TASK,
                    formData = initialFormData
                ),
                taskContextUpdater = { taskContext ->
                    taskContext.copy(
                        clickedTask = clickedTask,
                        inEditTask = clickedTask
                    )
                }
                )
        }


    }

    // Confirm an edit for an existing task
    suspend fun onFullEditConfirm(updatedTaskFormData: TaskFormData) {
        Log.d(
            tag,
            "OnUpdateTaskConfirm Clicked during edit. Id: ${updatedTaskFormData.id}. Name: ${updatedTaskFormData.name}"
        )

        val toUpdateTaskEntity = createTaskEntityFromForm(taskFormData = updatedTaskFormData)
        val returnedOperationState = repository.onEditUpdateTaskCurrentAndBelow(toUpdateTaskEntity)

        stateUpdateAfterFullEditing(returnedOperationState, toUpdateTaskEntity)
    }

    // Updating State after task edit
    private suspend fun stateUpdateAfterFullEditing(
        returnedOperationState: TaskOperationState,
        toUpdateTaskEntity: TaskEntity
    ) {
        Log.d(tag, "Updating States after New Task Operation...")

        when (returnedOperationState) {

            is TaskOperationState.Error -> {
                Log.d(tag, "Task could not be updated. Error message: ${returnedOperationState.message}")
                updateUiState(
                    taskOperationState = returnedOperationState,
                    uiScreen = UiScreen.None,
                    taskContextUpdater = { taskContext ->
                        taskContext.copy(
                            clickedTask = toUpdateTaskEntity,
                        )
                    }

                )
            }

            is TaskOperationState.Success -> {
                Log.d(tag, "Task confirmed successfully with ID: ${returnedOperationState.taskId}")
                val updatedTask = getTaskById(returnedOperationState.taskId)
                updateUiState(
                    taskOperationState = returnedOperationState,
                    uiScreen = UiScreen.None,
                    taskContextUpdater = { taskContext ->
                        taskContext.copy(
                            clickedTask = updatedTask,
                            latestTasks = taskContext.latestTasks + listOfNotNull(updatedTask)
                        )
                    }
                )
            }

            else -> {
                Log.e(tag, "Error in taskOperationState after adding a new task,")
            }
        }



    }

    // endregion


    // region: ------------- Deleting Task ----------------
    // Delete a task from the database
    suspend fun onDeleteTaskConfirmClick(task: TaskEntity) {
        Log.d(tag, "Confirm task delete clicked...")
        task.let {
            Log.d(tag, "Deleting task: ${task.name}")
            repository.deleteTask(task)
            updateUiState(
                taskOperationState = TaskOperationState.Idle,
                uiScreen = UiScreen.None,
            )
        }
    }
    // endregion


    // region: ------------ Msal/OneDrive -----------

    init {
        viewModelScope.launch {
            try {
                // Start initialization
                updateUiState(
                    msalAuthState = MsalAuthState(
                        signInStatus = SignInStatus.SigningIn
                    ),
                    onedriveSyncState = OnedriveSyncState.Idle
                )

                // Wait for MSAL to initialize
                msalApp.waitForInitialization()
                getCurrentMsalAccount()
            } catch (e: Exception) {
                // Update state if initialization fails
                updateUiState(
                    msalAuthState = MsalAuthState(
                        signInStatus = SignInStatus.Error
                    ),
                    onedriveSyncState = OnedriveSyncState.Error("Initialization failed: ${e.message}")
                )
                Log.e(tag, "Error during MSAL initialization", e)
            }
        }
    }


    private fun getCurrentMsalAccount() {
        Log.d(tag, "Fetching current MSAL account...")

        viewModelScope.launch {
            try {
                val account = msalApp.getCurrentAccount()

                if (account != null) {
                    Log.d(tag, "Account found: ${account.username}")
                    updateUiState(
                        msalAuthState = MsalAuthState(
                            isSignedIn = true,
                            username = account.username,
                            profileImageUrl = msalApp.getProfileImageUrl(),
                            signInStatus = SignInStatus.SignedIn
                        ),
                        onedriveSyncState = OnedriveSyncState.Idle
                    )
                } else {
                    Log.d(tag, "No account found.")
                    updateUiState(
                        msalAuthState = MsalAuthState(
                            isSignedIn = false,
                            signInStatus = SignInStatus.Idle
                        ),
                        onedriveSyncState = OnedriveSyncState.Idle
                    )
                }
            } catch (e: Exception) {
                Log.e(tag, "Error fetching MSAL account", e)
                updateUiState(
                    msalAuthState = MsalAuthState(
                        isSignedIn = false,
                        signInStatus = SignInStatus.Error
                    ),
                    onedriveSyncState = OnedriveSyncState.Error("Error fetching account: ${e.message}")
                )
            }
        }
    }


    // Function called when the user clicks the sign-in button
    fun onSignInClick(activity: Activity) {
        Log.d(tag, "Sign-in initiated.")
        viewModelScope.launch {
            try {
                updateUiState(
                    msalAuthState = MsalAuthState(
                        signInStatus = SignInStatus.SigningIn
                    ),
                    onedriveSyncState = OnedriveSyncState.Idle
                )

                // Attempt to sign in
                val result = msalApp.signIn(activity)

                // Update state if sign-in is successful
                updateUiState(
                    msalAuthState = MsalAuthState(
                        isSignedIn = true,
                        username = result.account.username,
                        profileImageUrl = msalApp.getProfileImageUrl(),
                        signInStatus = SignInStatus.SignedIn
                    ),
                    onedriveSyncState = OnedriveSyncState.Idle
                )

                Log.d(tag, "Sign-in successful for ${result.account.username}")
            } catch (e: Exception) {
                Log.e(tag, "Sign-in failed", e)
                updateUiState(
                    msalAuthState = MsalAuthState(
                        isSignedIn = false,
                        signInStatus = SignInStatus.Error
                    ),
                    onedriveSyncState = OnedriveSyncState.Error("Sign-in failed: ${e.message}")
                )
            }
        }
    }


    // Sync tasks from OneDrive
    fun onSyncButtonClick(context: Context) {
        Log.d(tag, "Syncing tasks from OneDrive...")
        viewModelScope.launch {
            try {
                updateUiState(
                    onedriveSyncState = OnedriveSyncState.Loading
                )

                // Perform the sync
                val authResult = msalApp.signIn(context as Activity)
                repository.syncTasksFromOneDrive(authResult, context)

                updateUiState(
                    onedriveSyncState = OnedriveSyncState.Success
                )
                Log.d(tag, "Task sync successful.")
            } catch (e: Exception) {
                Log.e(tag, "Task sync failed", e)
                updateUiState(
                    onedriveSyncState = OnedriveSyncState.Error("Sync failed: ${e.message}")
                )
            }
        }
    }


    // sign out
    fun onSignOutClick(activity: Activity) {
        Log.d(tag, "Sign-out initiated.")
        viewModelScope.launch {
            try {
                updateUiState(
                    msalAuthState = MsalAuthState(
                        signInStatus = SignInStatus.SigningIn // Indicating sign-out is in progress
                    ),
                    onedriveSyncState = OnedriveSyncState.Loading
                )

                // Perform sign-out
                msalApp.signOut {
                    Log.d(tag, "Sign-out successful.")
                    updateUiState(
                        msalAuthState = MsalAuthState(
                            isSignedIn = false,
                            signInStatus = SignInStatus.Idle
                        ),
                        onedriveSyncState = OnedriveSyncState.Idle
                    )
                    Toast.makeText(activity, "Signed out successfully", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(tag, "Sign-out failed", e)
                updateUiState(
                    onedriveSyncState = OnedriveSyncState.Error("Sign-out failed: ${e.message}"),
                    msalAuthState = MsalAuthState(
                        signInStatus = SignInStatus.Error
                    )
                )
                Toast.makeText(activity, "Failed to sign out: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }



    // endregion


    // region: ---------- onStateChangeEvents (UI State Handling) -----------

    // Handle task click events
    fun onTaskClick(taskClicked: TaskEntity) {
        Log.d(tag, "Task clicked: ${taskClicked.name}")

        // Some task in FullEdit
        if (_uiState.value.uiScreen is UiScreen.FullEditing) {
            // No changes no matter what the clicked task is
            return // User has to click 'Cancel' Button
        }

        // Some task in quick edit
        if (_uiState.value.uiScreen is UiScreen.QuickEditOverlay) {
            // No changes if clicked task is same as quick edit task
            if (taskClicked == _uiState.value.taskContext.inEditTask) {
                return // no changes are needed
            }

            // Handle case when a different task is clicked
            else {
                updateUiState(
                    uiScreen = UiScreen.None,
                    taskOperationState = TaskOperationState.Idle,
                    taskContextUpdater = { taskContext ->
                        taskContext.copy(
                            clickedTask = taskContext.inEditTask,
                            inEditTask = null,
                            taskBelowClickedTask = null
                        )
                    }
                )
            }
        }



        // No Task in either Full or quick Edit Mode
        else {
            updateUiState(
                uiScreen = UiScreen.None,
                taskOperationState = TaskOperationState.Idle,
                taskContextUpdater = { taskContext ->
                    taskContext.copy(
                        clickedTask = taskClicked,
                        inEditTask = null,
                        taskBelowClickedTask = null
                    )
                }
            )
        }
    }

    // Show Quick Edit UI for a task
    suspend fun onShowQuickEditClick(taskToEdit: TaskEntity) {
        Log.d(tag, "Quick edit click for task: ${taskToEdit.name}. ID:${taskToEdit.id}")

        val clickedTaskPosition = _uiState.value.taskContext.clickedTask?.position
        val taskBelowPosition = clickedTaskPosition?.plus(1)
        val taskBelowClickedTask = taskBelowPosition?.let { getTaskAtPosition(it) }

        updateUiState(
            uiScreen = UiScreen.QuickEditOverlay,
            taskOperationState = TaskOperationState.Idle,
            taskContextUpdater = { taskContext ->
                taskContext.copy(
                    clickedTask = taskToEdit,
                    inEditTask = taskToEdit,
                    taskBelowClickedTask = taskBelowClickedTask
                )
            }
        )

    }

    // Show task details
    fun onTaskDetailsClick(task: TaskEntity) {
        Log.d(tag, "Show task details clicked: ${task.name}")
        updateUiState(
            uiScreen = UiScreen.DetailsView, // Update as needed
            taskContextUpdater = { taskContext ->
                taskContext.copy(
                    clickedTask = task,
                    showingDetailsTask = task
                )
            }
        )
    }

    fun onShowFinishedTasksView() {
        Log.d(tag, "Show completed tasks clicked")
        updateUiState(
            uiScreen = UiScreen.FinishedTasksView, // Update as needed
        )
    }

    fun onTaskLongPress(task: TaskEntity) {
        Log.d(tag, "Long press on task: ${task.name}")
        updateUiState(
            uiScreen = UiScreen.LongPressMenu, // Update as needed
            taskContextUpdater = { taskContext ->
                taskContext.copy(
                    clickedTask = task,
                    longPressMenuTask = task
                )
            }
        )
    }

    fun onDismissOrReset(task:TaskEntity? = null) {
        if (task != null) {
            Log.d(tag, "Dismiss or Reset from task: ${task.name}")
        } else {
            Log.d(tag, "Dismiss or Reset (no task provided)")
        }

        viewModelScope.launch {
            val newClickedTask = when (_uiState.value.uiScreen) {
                UiScreen.AddingNew -> _uiState.value.taskContext.clickedTask
                UiScreen.FullEditing -> _uiState.value.taskContext.inEditTask
                UiScreen.QuickEditOverlay -> _uiState.value.taskContext.inEditTask
                UiScreen.DetailsView -> _uiState.value.taskContext.showingDetailsTask
                UiScreen.FinishedTasksView -> _uiState.value.taskContext.clickedTask
                UiScreen.None -> _uiState.value.taskContext.clickedTask
                else -> _uiState.value.taskContext.clickedTask
            }

            updateUiState(
                uiScreen = UiScreen.None,
                taskOperationState = TaskOperationState.Idle,
                taskContextUpdater = { taskContext ->
                    taskContext.copy(
                        clickedTask = newClickedTask,
                        longPressMenuTask = null,
                        taskBelowClickedTask = null,
                        inEditTask = null,
                        showingDetailsTask = null
                    )
                }
            )
        }
    }

    // Date Picker
    fun onDatePickerClick() {
        Log.d(tag, "Date picker clicked")
        updateUiState(
            uiScreen = UiScreen.DatePicker,
        )
    }

    // set the selected Date
    fun onDateChangeClick(date: LocalDate) {
        Log.d(tag, "Date changed to: $date")
        _selectedDate.value = date  // Update the selected date in the ViewModel
        updateUiState(
            uiScreen = UiScreen.None,
        )
    }

    suspend fun setUiStateToDefault() {
        viewModelScope.launch {
            val firstTask = getFirstTask()
            updateUiState(
                uiScreen = UiScreen.None, // Update as needed
                taskOperationState = TaskOperationState.Idle,
                taskContextUpdater = { taskContext ->
                    taskContext.copy(
                        clickedTask = firstTask,
                        inEditTask = null,
                        latestTasks = emptyList(),
                        taskBelowClickedTask = null
                    )
                }
            )
        }
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