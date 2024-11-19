# App Overview
My App is called RoutineTurboA ('A' stands for Android) and simply RoutineTurbo (Windows version built on PyQt6).

The app operates on a strict 24-hour (1440-minute) daily timeline. On the first launch, it displays two blocks spanning the entire 24 hours (00:01 - 23:59) with default task names. Users can add new tasks, input start and end times, set reminders, add notes, specify task types (like Main, Quick, Helper, etc.), link Helper tasks to Main Tasks, adjust duration, and more. The app logic ensures there are no gaps in the schedule by automatically allocating unassigned minutes to adjacent task blocks. Editing times or duration, adding or deleting tasks, and other operations adjust the timeline to maintain the continuity. Task types include Main Tasks, Default Tasks (basic everyday activities), Quick Tasks (1-2 minute tasks), and Helper Tasks (supporting Main Tasks), with more types planned for the future.

# Support and Compatibility
The Android version targets API level 34 (Android 14.0) and has a minimum SDK of 30, ensuring compatibility with devices running Android 12 and above. It is developed in Kotlin, using Jetpack Compose for the UI, Room for database management using KSP plugin, and hilt for dependency injection using kapt. OneDrive integration is managed using MSAL, and it's purpose is to sync the routine data between Windows and Android version.

# Database Design Overview: 
Both versions use a SQLite database to manage tasks in way that maintains consistency across both platforms for proper data synchronization.

Table Structure:

| Column     | Data Type | Description                                  |
|------------|------------|----------------------------------------------|
| id         | INTEGER    | Primary key, auto-incremented.               |
| name       | TEXT       | Task name.                                   |
| notes      | TEXT       | Optional task notes (default is empty).      |
| duration   | INTEGER    | Task duration in minutes.                    |
| startTime  | TEXT       | Start time in ISO string format.             |
| endTime    | TEXT       | End time in ISO string format.               |
| reminder   | TEXT       | Reminder time in ISO string format.          |
| type       | TEXT       | Task type (e.g., Main, Quick, Helper, etc.). |
| position   | INTEGER    | Task position in the schedule.               |
| mainTaskId | INTEGER    | Reference to the main task ID (if applicable).|

 - ## DateTime Handling:

   * Windows Version: startTime, endTime, and reminder are stored as ISO 8601 strings. A 'T' is inserted between the date and time to align with Android formatting. These strings are converted to DateTime objects in the app model.
   * Android Version: startTime, endTime, and reminder are LocalDateTime objects in the TaskEntity class. A TypeConverters class handles conversion between LocalDateTime and ISO strings.


## About Me as Developer :

I am a beginner who has developed most of this app using AI tools, like GPT and Claude. I am seeking guidance on refining both the Android version (RoutineTurboA) and the Windows version (RoutineTurbo, built with PyQt6). My goal is to learn as I build. It's imperative that the advice must incorporate the latest APIs, methods, and best practices as of 2024. Please ensure that all suggestions are compatible with the versions I mentioned earlier and avoid any deprecated or outdated approaches.

Today, it's about RoutineTurboA, the android version.

# Hierarchy
Here's the organization of files and modules in Android version:

|-- routineturboa/
|   |-- MainActivity.kt
|   |-- RoutineTurboApp.kt
|   |-- core/
|   |   |-- dbutils/
|   |   |   |-- Converters.kt
|   |   |   |-- DbConstants.kt
|   |   |   |-- RecurrenceType.kt
|   |   |-- di/
|   |   |   |-- AppModule.kt
|   |   |-- models/
|   |   |   |-- EventsHandler.kt
|   |   |   |-- UiState.kt
|   |   |-- utils/
|   |   |   |-- defaultTasks.kt
|   |   |   |-- demoTasks.kt
|   |   |   |-- getTaskColor.kt
|   |   |   |-- NotificationPermissionHandler.kt
|   |   |   |-- SineEasing.kt
|   |   |   |-- TaskTypes.kt
|   |-- data/
|   |   |-- onedrive/
|   |   |   |-- downloadFromOneDrive.kt
|   |   |   |-- MsalApp.kt
|   |   |   |-- OneDriveManager.kt
|   |   |-- repository/
|   |   |   |-- AppRepository.kt
|   |   |-- room/
|   |   |   |-- AppDao.kt
|   |   |   |-- AppData.kt
|   |   |   |-- entities/
|   |   |   |   |-- NonRecurringTaskEntity.kt
|   |   |   |   |-- TaskCompletionEntity.kt
|   |   |   |   |-- TaskCompletionHistory.kt
|   |   |   |   |-- TaskEntity.kt
|   |-- reminders/
|   |   |-- NotificationHelper.kt
|   |   |-- ReminderManager.kt
|   |   |-- receivers/
|   |   |   |-- ReminderReceiver.kt
|   |   |   |-- SnoozeReceiver.kt
|   |   |   |-- TaskCompletionReceiver.kt
|   |   |   |-- TestReceiver.kt
|   |-- ui/
|   |   |-- main/
|   |   |   |-- MainScreen.kt
|   |   |-- models/
|   |   |   |-- TaskFormData.kt
|   |   |-- reusable/
|   |   |   |-- animation/
|   |   |   |   |-- AnimateVisibilityComponent.kt
|   |   |   |   |-- CircularProgressIndicator.kt
|   |   |   |   |-- SuccessIndicator.kt
|   |   |   |-- dropdowns/
|   |   |   |   |-- MainTasksListDropdown.kt
|   |   |   |   |-- SelectRecurrenceTypeDropdown.kt
|   |   |   |   |-- SelectTaskTypeDropdown.kt
|   |   |   |-- fields/
|   |   |   |   |-- CustomTextField.kt
|   |   |   |   |-- QuickEditInputTextField.kt
|   |   |   |-- others/
|   |   |   |   |-- SignInAndSyncButtons.kt
|   |   |   |   |-- SimpleToast.kt
|   |   |   |   |-- TaskCardPlaceholder.kt
|   |   |   |-- pickers/
|   |   |   |   |-- CustomNumberPicker.kt
|   |   |   |   |-- PickDateDialog.kt
|   |   |   |   |-- TimePickerField.kt
|   |   |-- scaffold/
|   |   |   |-- AppBottomBar.kt
|   |   |   |-- AppDrawer.kt
|   |   |   |-- AppTopBar.kt
|   |   |-- tasks/
|   |   |   |-- ParentTaskItem.kt
|   |   |   |-- childItems/
|   |   |   |   |-- HourColumn.kt
|   |   |   |   |-- OptionalTaskTimings.kt
|   |   |   |   |-- PrimaryTaskView.kt
|   |   |   |   |-- QuickEditTask.kt
|   |   |   |-- dialogs/
|   |   |   |   |-- TaskCompletionDialog.kt
|   |   |   |   |-- TaskDetailsDialog.kt
|   |   |   |-- form/
|   |   |   |   |-- FullEditDialog.kt
|   |   |   |   |-- NewTaskCreationScreen.kt
|   |   |   |-- menu/
|   |   |   |   |-- OptionsMenu.kt
|   |   |   |   |-- TaskOptionsDropdownMenu.kt
|   |   |-- theme/
|   |   |   |-- Color.kt
|   |   |   |-- LocalCustomColors.kt
|   |   |   |-- Theme.kt
|   |   |   |-- Type.kt
|   |-- utils/
|   |-- viewmodel/
|   |   |-- TasksViewModel.kt


I will share the database related modules now:

## Room Database

`@Database(
    entities = [
    TaskEntity::class,
    TaskCompletionEntity::class,
    NonRecurringTaskEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppData : RoomDatabase() {
    abstract fun appDao(): AppDao
}`

## Entities

`@Entity(tableName = DbConstants.TASKS_TABLE)
@TypeConverters(Converters::class)
data class TaskEntity(
@PrimaryKey(autoGenerate = true) val id: Int = 0,
val name: String = "New",
val notes: String? = "",
val duration: Int? = 1,
val startTime: LocalTime?,
val endTime: LocalTime?,
val reminder: LocalTime?,
val type: String? = TaskTypes.UNDEFINED,
val position: Int? = 1,
val mainTaskId: Int? = null,
val startDate: LocalDate? = null,
val isRecurring: Boolean? = false,
val recurrenceType: RecurrenceType? = null,
val recurrenceInterval: Int? = null,
val recurrenceEndDate: LocalDate? = null
)`

`@Entity(
tableName = DbConstants.TASK_DATES_TABLE,
foreignKeys = [ForeignKey(
entity = TaskEntity::class,
parentColumns = ["id"],
childColumns = ["taskId"],
onDelete = ForeignKey.CASCADE)]
)
@TypeConverters(Converters::class)
data class NonRecurringTaskEntity(
@PrimaryKey(autoGenerate = true) val id: Int = 0,
val taskId: Int, // Foreign key to TaskEntity
val taskDate: LocalDate,
val isException: Boolean = false
)`

`@Entity(
tableName = DbConstants.TASK_COMPLETIONS_TABLE,
foreignKeys = [
ForeignKey(
entity = TaskEntity::class,
parentColumns = ["id"],
childColumns = ["taskId"],
onDelete = ForeignKey.CASCADE
)
],
indices = [Index(value = ["taskId", "date"], unique = true)]
)`

## Data Classes

`data class TaskCompletionEntity(
@PrimaryKey(autoGenerate = true) val id: Int = 0,
val taskId: Int,  // Task ID this completion refers to (linked to ID col in tasks_table).
val date: LocalDate,
val isCompleted: Boolean
)`


data class TaskCompletionHistory(
// @ Embedded annotation means that TaskEntity is embedded inside TasksWithCompletion. So, the task field contains all the columns from the TaskEntity (like task name, duration, start time, etc.).
@Embedded val task: TaskEntity,  // // A single task

    @Relation(
        parentColumn = "id", // This refers to the primary key id from the TaskEntity (i.e., the tasks_table).
        entityColumn = "taskId"  // This refers to the taskId in the task_completion table. It’s how Room knows which completions are linked to which task.
    )
    val completions: List<TaskCompletionEntity>  // All completion records for this task
)

# Summarized Overview of various modules

The following summaries provide a functional outline of various components in the app, capturing the essentials without delving into specific code details or syntax. Each summary acts as a map of the component’s structure and purpose, listing main parts, significant arguments, and the key tasks each section performs.

By focusing on what each part does and why it’s there, the summaries offer a high-level overview that conveys the flow and function of each component quickly, and more important the relationship between each component. The summaries avoid minor details, like specific implementations or logging, to keep the big picture clear. This format serves as a roadmap, helping collaborators or newcomers understand the overall architecture and role of each component without getting bogged down in technical specifics.




## Dao
Simplified DAO Overview: Key method signatures and annotations are shown without implementation details for a quick reference.

```
// package com.app.routineturboa.data.room
@Dao
interface AppDao {
    @Transaction runTaskTransaction(block: suspend () -> Unit) {
        return block()
    }
    @Query getAllTasks(): Flow<List<TaskEntity>>  // flow
    @Query getAllMainTasks(mainType: String = TaskTypes.MAIN): List<TaskEntity>
    @Query isNonRecurringTaskOnThisDate(taskId: Int, date: LocalDate): Boolean
    @Query getAllTaskNamesAndPositions(): List<TaskNameAndPosition>
    @Query getTaskEntityById(taskId: Int): TaskEntity?
    @Insert insertTask(task: TaskEntity): Long
    @Update updateTask(task: TaskEntity)
    @Delete deleteTask(task: TaskEntity)
    @Query deleteAllTasks()
    // other similar functions
} 
```

## Repository
Simplified Repository Overview: Key method signatures and purpose without detailed implementation.
```
package com.app.routineturboa.data.repository
class AppRepository @Inject constructor(
    private val appDao: AppDao
) {
    // Database Helpers
    private suspend fun <T> runAsTransaction(block: suspend () -> T): T

    // Flow to observe all tasks
    val allTasks: Flow<List<TaskEntity>>

    // ----------------- TasksByDate  -----------------
    fun getTasksForDate(date: LocalDate): Flow<List<TaskEntity>>

    // ------------- Database Operations (Add, update, Delete) -------------
    suspend fun beginNewTaskOperations(newTaskBeforeInsertion: TaskEntity, clickedTask: TaskEntity, originalTaskBelow: TaskEntity): Result<TaskCreationOutcome> {
         runAsTransaction {
             incrementTasksPositionBelow()
             appDao.safeInsertTask()
             updateTaskBelowAfterInsertion()
         }
    }
    private suspend fun updateTaskBelowAfterInsertion(taskToShift: TaskEntity, newTaskEndTime: LocalTime): TaskEntity { appDao.appDao.updateTask() }
    private suspend fun incrementTasksPositionBelow(startingPosition: Int) { appDao.incrementPositionsBelow() }
    suspend fun onEditUpdateTaskCurrentAndBelow(taskToEdit: TaskEntity): Result<TaskCreationOutcome> {
        runAsTransaction {
            updateTask(taskToEdit)
            updateTask(updatedTaskBelow)
        }
    }

    // ------------ Task Retrieval and filtering ----------------
    suspend fun getTaskAtPosition(position: Int): TaskEntity?
    suspend fun getAllMainTasks(): List<TaskEntity>

    // ------------------------- Sync Operations -------------------------
    suspend fun syncTasksFromOneDrive(authResult: IAuthenticationResult, context: Context)
    private fun fetchTasksFromOneDriveDb(db: SQLiteDatabase): List<TaskEntity>

    // ----------------- Task Utilities -------------------------
    // updateTask(), insertTask(), deleteTask()

    // --------------- Task Completion and Others -----------------
    suspend fun markTaskAsCompleted(taskId: Int)
    suspend fun getTasksWithCompletionStatus(): List<TaskCompletionHistory>
}
```

## Data and Sealed Classes for handling States
```
data class UiState(
    activeUiComponent, stateBasedTasks, taskCreationState
) // Tracks active UI component, selected tasks, and task creation state.

sealed class ActiveUiComponent {
    shouldShowLazyColumn() default = true; AddingNew(false), FullEditing, QuickEditOverlay, DetailsView, ContextMenu, FinishedTasks, DatePicker, None
}

data class StateBasedTasks(clickedTask, longPressedTask, taskBelowClickedTask, inEditTask, showingDetailsTask, latestTask)

sealed class TaskCreationState { Idle, Loading, FillingDetails(formData), Success(newTaskId), Error(message) }

data class TaskCreationOutcome(success, newTaskId, message = "Task operation successful")


## EventsHandler: Supplies event handlers linked to TasksViewModel.

class EventsHandler(viewModel) {
    fun onStateChangeEvents(): StateChangeEvents {
        onTaskClick, onTaskLongPress, onShowAddNewTaskClick, onShowQuickEditClick, onShowFullEditClick,
        onShowTaskDetailsClick, onShowCompletedTasksClick, onCancelClick, onShowDatePickerClick,
        resetTaskCreationState, onDateChangeClick = TasksViewModel’s corresponding functions
    }

    fun onDataOperationEvents(): DataOperationEvents {
        onNewTaskConfirmClick, onUpdateTaskConfirmClick, onDeleteTaskConfirmClick, onMainTasksRequested = TasksViewModel’s corresponding functions
    }
}
```

// StateChangeEvents: Functions for UI state changes; some accept TaskEntity, others none.
```
data class StateChangeEvents(
    onTaskClick, onTaskLongPress, onShowAddNewTaskClick, onShowQuickEditClick, onShowFullEditClick,
    onShowTaskDetailsClick, onShowCompletedTasksClick, onCancelClick, onShowDatePickerClick,
    resetTaskCreationState, onDateChangeClick
)
```
// DataOperationEvents: Functions for data operations, handling task actions and retrieval.
```
data class DataOperationEvents(
    onNewTaskConfirmClick, onUpdateTaskConfirmClick, onDeleteTaskConfirmClick, onMainTasksRequested
)
```

## viewModel: TasksViewModel
TasksViewModel handles task data, UI states, and interactions between the UI and AppRepository.
```
package com.app.routineturboa.viewmodel

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    // StateFlows for managing UI states and selected date
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    val selectedDate: StateFlow<LocalDate> get() = _selectedDate
    private val allTasks: StateFlow<List<TaskEntity>> = repository.allTasks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val tasksByDate: StateFlow<List<TaskEntity>> = _selectedDate.flatMapLatest { repository.getTasksForDate(it) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Utility methods for handling tasks
    private fun createTaskEntityFromForm(
        taskFormData: TaskFormData, taskId: Int = taskFormData.id
    ): TaskEntity

    // Sync tasks from OneDrive
    fun syncTasksFromOneDrive(context: Context)

    // Basic task retrieval
    suspend fun onMainTasksRequested(): List<TaskEntity>
    suspend fun onNameRequested(taskId: Int): String
    getTaskName, getTaskAtPosition, getTaskById, and so on.


    // Task data operation events (Add, Edit, Delete)
    // IMPORTANT ONES...CHANGES DATA
    suspend fun onNewTaskConfirmClick(clickedTask: TaskEntity, taskBelowClickedTask: TaskEntity, newTaskFormData: TaskFormData): Result<TaskCreationOutcome>
    private suspend fun updateStatesAfterNewTaskOperation(result: Result<TaskCreationOutcome>, clickedTask: TaskEntity)
    suspend fun onUpdateTaskConfirmClick(updatedTaskFormData: TaskFormData)
    suspend fun onDeleteTaskConfirmClick(task: TaskEntity)
    fun deleteAllTasks()
    // and similar functions

    // UI state change events for task interactions and visibility management in UiState
    fun onTaskClick(taskClicked: TaskEntity) {
        // Sets clickedTask in stateBasedTasks to track the selected task for further actions
    }
    suspend fun onShowAddNewTaskClick() {
        // Updates activeUiComponent to AddingNew to trigger task creation screen visibility
    }
    suspend fun onShowFullEditClick(taskInEdit: TaskEntity) {
        // Sets activeUiComponent to FullEditing and inEditTask in stateBasedTasks, initiating full task editing mode
    }
    suspend fun onShowQuickEditClick(task: TaskEntity) {
        // Sets activeUiComponent to QuickEditOverlay, enabling quick task modifications
    }
    fun onTaskDetailsClick(task: TaskEntity) {
        // Updates showingDetailsTask in stateBasedTasks and activeUiComponent to DetailsView to display task details
    }
    // Additional functions follow similar patterns to set specific UiState properties for other interactions.

    // Task completion handling
    val taskCompletions: StateFlow<List<TaskCompletionHistory>> = _taskCompletions.asStateFlow()
    fun loadTaskCompletions()
}
```

## MainActivity
```
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	// Inject lateinit Repository and reminderManager
	// get tasksViewModel (TasksViewModel by viewModels())
	override fun onCreate() {
		NotificationPermissionHandler.initialize(this)
		setContent {
            RoutineTurboATheme {
                MainScreen(
                    tasksViewModel,
                    NotificationPermissionHandler.getShowExactAlarmDialog()
                )
                NotificationPermissionHandler.HandlePermissionDialogs()
            }
        }
        // other functions like handleBackPress()
    }
}
```

## MainScreen
```
@Composable
fun MainScreen(tasksViewModel, showExactAlarmDialog) {
    // Initialize context, coroutineScope, drawerState, reminderManager

    // Collect state: tasksByDate, tasksCompleted, uiState, selectedDate, for example:
        val tasksByDate by tasksViewModel.tasksByDate.collectAsState()
        val uiState by tasksViewModel.uiState.collectAsState()

    // Create EventsHandler with tasksViewModel for state and data events

    ModalNavigationDrawer(
        drawerState,
        AppDrawer(tasksViewModel, reminderManager, showExactAlarmDialog, clickedTask, selectedDate, onStateChangeEvents.onShowCompletedTasksClick)
        ) {
        Scaffold(
            AppTopBar(drawerState, selectedDate, uiState, onStateChangeEvents, onDataOperationEvents),
            if (!isAddingOrFullEditing) AppBottomBar(onStateChangeEvents.onShowAddNewTaskClick)
        ) { >

            TasksLazyColumnAnimation(uiState.activeUiComponent.shouldShowLazyColumn() ?: true) {
                LazyColumn {
                    items(tasksByDate, key = { it.id }) { task ->
                        ParentTaskItem(task, uiState, onStateChangeEvents, onDataOperationEvents)
                    }
                }
            }

            NewTaskScreenAnimation(uiState.activeUiComponent is ActiveUiComponent.AddingNew) {
                NewTaskCreationScreen(
                    paddingValues, clickedTask, taskBelowClicked, selectedDate,
                    onStateChangeEvents, onDataOperationEvents.onMainTasksRequested,
                    onConfirm = { onDataOperationEvents.onNewTaskConfirmClick(clicked, below, data) }
                    }
                )
            }

            NewTaskScreenAnimation(uiState.activeUiComponent is ActiveUiComponent.DatePicker) {
                PickDateDialog(selectedDate, onStateChangeEvents.onDateChangeClick, onStateChangeEvents.onCancelClick)
            }

            if (uiState.activeUiComponent is ActiveUiComponent.FinishedTasks) {
                TaskCompletionDialog(tasksCompleted, onStateChangeEvents.onCancelClick)
            }

            if (uiState.taskCreationState is TaskCreationState.Success) {
                Box { SuccessIndicator(onReset = onStateChangeEvents.resetTaskCreationState) }
            }
        }
    }
}
```

## ParentTaskItem
```
@Composable
fun ParentTaskItem(
    task: TaskEntity,
    uiState: UiState,
    onStateChangeEvents: StateChangeEvents,
    onDataOperationEvents: DataOperationEvents,
) {
    // Using UiState.stateBasedTasks and uiState.ActiveUiComponent, define states like isThisTaskClicked, isThisTaskQuickEditing, isThisTaskFullEditing, and also visibilities like isThisTaskContextMenu, isThisTaskShowDetails, and so on.
    // Define colors, height, width, border, etc. condition based on state of task (like if clicked or not)

    Box() {
        Card() {
            Row() {
                Spacer()
                HourColumn()
                if (isThisTaskQuickEditing) {
                    InLineQuickEdit()
                }
                if (isThisTaskFullEditing) {
                    FullEditDialog()
                }
                else if (isThisTaskShowDetails) {
                    TaskDetailsDialog()
                }
                else {
                    Row() {
                        PrimaryTaskView()
                        Box() {
                            IconButton() {
                                Icon()
                            }
                            DropdownMenu() {
                                DropdownMenuItem() // View Details
                                DropdownMenuItem() // Edit
                                DropdownMenuItem() // Delete
                            }
                        }
                    }
                }
            }
        }
        Spacer() // Bottom underline
    }
```

## Primary Task View

```
@Composable
fun PrimaryTaskView(
    task: TaskEntity,
    isThisTaskClicked: Boolean,
    cardHeight: Dp,
    stateChangeEvents: StateChangeEvents,
    topPadding: Dp,
    forReferenceView: Boolean = false,
    bgColor: Color = Color.LightGray,
) {
    // Get things ready for display, like Text Color, formatted endtime, etc.
    Column() {  // Primary Task View
        Row() {  // Main row container for task name and right-most area
            Row(weight = 0.3f) {  // Left side, displaying task name and icon
                Text()  // Task name
                Box() {  // Icon showing first letter of task type
                    Text()  // Letter inside circular background
                }
            }
            Column() {  // Right-most area for edit button and task end time
                IconButton() {  // Edit button
                    Icon()  // Edit icon
                }
                Box() {  // Task end time display box
                    Text()  // End time text
                }
            }
        }
        if (cardHeight > minHeightForOptionalTimings) {  // Optional timings based on card height
            OptionalTaskTimings()  // Additional timing details if height is sufficient
        }
    }
}
```

Today, I need your help with 


