# Documentation: State and Event Handling in RoutineTurboApp

This documentation provides a detailed explanation of how state and events are managed in the RoutineTurboApp, focusing on:

- Data classes and sealed classes in the **shared** module.
- Functions in `TasksViewModel`.
- Integration and usage within the UI components.

---

## Table of Contents

1. [Overview](#overview)
2. [Shared Module](#shared-module)
   - [ActiveOverlayComponent](#activeoverlaycomponent)
   - [DataOperationEvents](#dataoperationevents)
   - [StateChangeEvents](#statechangeevents)
   - [TaskCreationState](#taskcreationstate)
   - [UiState and UiTaskReferences](#uistate-and-uitaskreferences)
3. [TasksViewModel](#tasksviewmodel)
   - [StateFlow and State Management](#stateflow-and-state-management)
   - [Utility Methods](#utility-methods)
   - [Database Operations](#database-operations)
   - [UI State Handling](#ui-state-handling)
   - [Task Completion Handling](#task-completion-handling)
4. [UI Integration](#ui-integration)
   - [MainScreen](#mainscreen)
   - [EventsHandler](#eventshandler)
   - [ParentTaskItem](#parenttaskitem)
5. [Conclusion](#conclusion)

---

## Overview

The RoutineTurboApp is a task management application that handles complex UI states and events to provide a seamless user experience. The application architecture relies heavily on **state management** and **event handling** to manage tasks, display UI components, and respond to user interactions.

---

## Shared Module

The **shared** module contains data classes and sealed classes that define the various states and events used throughout the app.

### ActiveOverlayComponent

A sealed class representing the different overlay components that can be active in the UI. This helps manage which UI component should be displayed at any given time.

```kotlin
sealed class ActiveOverlayComponent {
    data object FullEditing : ActiveOverlayComponent()
    data object AddingNew : ActiveOverlayComponent()
    data object QuickEditing : ActiveOverlayComponent()
    data object ShowingDetails : ActiveOverlayComponent()
    data object ShowingCompletedTasks : ActiveOverlayComponent()
    data object ShowingDatePicker : ActiveOverlayComponent()
    data object None: ActiveOverlayComponent()
}
```

- **FullEditing**: Indicates that the full task editing overlay is active.
- **AddingNew**: Indicates that the new task creation overlay is active.
- **QuickEditing**: Indicates that the quick edit overlay is active.
- **ShowingDetails**: Indicates that the task details overlay is active.
- **ShowingCompletedTasks**: Indicates that the completed tasks dialog is active.
- **ShowingDatePicker**: Indicates that the date picker dialog is active.
- **None**: Indicates that no overlay component is active.

### DataOperationEvents

A data class encapsulating suspend functions that handle data operations like adding, updating, and deleting tasks.

```kotlin
data class DataOperationEvents(
    val onNewTaskConfirmClick: suspend(
        clicked: TaskEntity,
        belowClicked: TaskEntity,
        newTaskForm: TaskFormData,
    ) -> Result<TaskCreationOutcome>,

    val onUpdateTaskConfirmClick: suspend (
        updatedTaskFormData: TaskFormData
    ) -> Unit,

    val onDeleteTaskConfirmClick: suspend (task: TaskEntity) -> Unit
)
```

- **onNewTaskConfirmClick**: Handles the confirmation of a new task creation.
- **onUpdateTaskConfirmClick**: Handles the confirmation of updating an existing task.
- **onDeleteTaskConfirmClick**: Handles the confirmation of deleting a task.

### StateChangeEvents

A data class that encapsulates functions for handling state changes triggered by user interactions.

```kotlin
data class StateChangeEvents(
    val onTaskClick: (TaskEntity) -> Unit,
    val onTaskLongPress: (TaskEntity) -> Unit,
    val onDateChangeClick: (LocalDate) -> Unit,
    val onShowAddNewTaskClick: suspend () -> Unit,
    val onShowQuickEditClick: suspend (TaskEntity) -> Unit,
    val onShowFullEditClick: suspend (TaskEntity) -> Unit,
    val onShowTaskDetailsClick: (TaskEntity) -> Unit,
    val onShowCompletedTasksClick: () -> Unit,
    val onShowDatePickerClick: () -> Unit,
    val resetTaskCreationState: () -> Unit,
    val onCancelClick: () -> Unit,
)
```

- **onTaskClick**: Handles task click events.
- **onTaskLongPress**: Handles task long-press events.
- **onDateChangeClick**: Handles date change events.
- **onShowAddNewTaskClick**: Initiates the new task creation process.
- **onShowQuickEditClick**: Initiates the quick edit process for a task.
- **onShowFullEditClick**: Initiates the full edit process for a task.
- **onShowTaskDetailsClick**: Displays the details of a task.
- **onShowCompletedTasksClick**: Displays completed tasks.
- **onShowDatePickerClick**: Displays the date picker dialog.
- **resetTaskCreationState**: Resets the task creation state.
- **onCancelClick**: Handles cancel events.

### TaskCreationState

A sealed class representing the state of task creation or editing.

```kotlin
sealed class TaskCreationState {
    data object Idle : TaskCreationState()
    data object Loading : TaskCreationState()
    data class Success(val newTaskId: Int) : TaskCreationState()
    data class Error(val message: String) : TaskCreationState()
}
```

- **Idle**: No task creation or editing is in progress.
- **Loading**: Task creation or editing is in progress.
- **Success**: Task creation or editing succeeded.
- **Error**: Task creation or editing failed.

### UiState and UiTaskReferences

#### UiState

A data class that holds the overall UI state of the application.

```kotlin
data class UiState(
    val uiTaskReferences: UiTaskReferences = UiTaskReferences(),
    val activeOverlayComponent: ActiveOverlayComponent = ActiveOverlayComponent.None,
    val taskCreationState: TaskCreationState = TaskCreationState.Idle,
    val isBaseTasksLazyColumnVisible: Boolean = true
)
```

- **uiTaskReferences**: References to various tasks involved in the UI.
- **activeOverlayComponent**: The currently active overlay component.
- **taskCreationState**: The current state of task creation or editing.
- **isBaseTasksLazyColumnVisible**: Controls the visibility of the main tasks list.

#### UiTaskReferences

A data class that holds references to specific tasks for UI operations.

```kotlin
data class UiTaskReferences(
    val clickedTask: TaskEntity? = null,
    val longPressedTask: TaskEntity? = null,
    val taskBelowClickedTask: TaskEntity? = null,
    val inEditTask: TaskEntity? = null,
    val showingDetailsTask: TaskEntity? = null,
    val latestTask: TaskEntity? = null
)
```

- **clickedTask**: The task that was last clicked.
- **longPressedTask**: The task that was long-pressed.
- **taskBelowClickedTask**: The task immediately below the clicked task.
- **inEditTask**: The task currently being edited.
- **showingDetailsTask**: The task whose details are being displayed.
- **latestTask**: The most recently created or updated task.

---

## TasksViewModel

The `TasksViewModel` class is responsible for handling business logic, state management, and communication with the repository.

### StateFlow and State Management

#### UI State

- **_uiState**: A `MutableStateFlow` holding the `UiState`.
- **uiState**: An immutable `StateFlow` exposed to the UI.

```kotlin
private val _uiState = MutableStateFlow(UiState())
val uiState: StateFlow<UiState> = _uiState.asStateFlow()
```

#### Selected Date

- **_selectedDate**: A `MutableStateFlow` holding the currently selected date.
- **selectedDate**: An immutable `StateFlow` exposed to the UI.

```kotlin
private val _selectedDate = MutableStateFlow(LocalDate.now())
val selectedDate: StateFlow<LocalDate> get() = _selectedDate
```

#### Tasks Flows

- **allTasks**: A `StateFlow` containing all tasks from the repository.
- **tasksByDate**: A `StateFlow` containing tasks filtered by the selected date.

```kotlin
val allTasks: StateFlow<List<TaskEntity>> = repository.allTasks.stateIn(...)
val tasksByDate: StateFlow<List<TaskEntity>> = _selectedDate.flatMapLatest { date -> ... }.stateIn(...)
```

### Utility Methods

#### Task Creation Helpers

- **createTaskEntityFromForm**: Converts `TaskFormData` into a `TaskEntity`.

```kotlin
private fun createTaskEntityFromForm(taskFormData: TaskFormData, taskId: Int = taskFormData.id): TaskEntity { ... }
```

#### Sample Tasks

- **insertSampleTasks**: Inserts sample tasks into the database.
- **insertBasicTasks**: Inserts basic tasks into the database.

```kotlin
fun insertSampleTasks() { ... }
fun insertBasicTasks() { ... }
```

#### Task Retrieval

- **getFirstTask**: Retrieves the first task from the database.
- **getTaskAtPosition**: Retrieves a task at a specific position.
- **getTaskById**: Retrieves a task by its ID.

```kotlin
private suspend fun getFirstTask(): TaskEntity? { ... }
private suspend fun getTaskAtPosition(taskPosition: Int): TaskEntity? { ... }
private suspend fun getTaskById(taskId: Int): TaskEntity? { ... }
```

### Database Operations

#### Adding a New Task

- **onNewTaskConfirmClick**: Handles the confirmation of a new task creation.

```kotlin
suspend fun onNewTaskConfirmClick(
    clickedTask: TaskEntity,
    taskBelowClickedTask: TaskEntity,
    newTaskFormData: TaskFormData,
): Result<TaskCreationOutcome> { ... }
```

This function:

1. Creates a new `TaskEntity` from the form data.
2. Initiates the task creation process through the repository.
3. Updates the UI state based on the result.

#### Updating an Existing Task

- **onUpdateTaskConfirmClick**: Handles the confirmation of updating an existing task.

```kotlin
suspend fun onUpdateTaskConfirmClick(updatedTaskFormData: TaskFormData) { ... }
```

This function:

1. Creates an updated `TaskEntity` from the form data.
2. Initiates the update process through the repository.
3. Updates the UI state accordingly.

#### Deleting a Task

- **onDeleteTaskConfirmClick**: Handles the confirmation of deleting a task.

```kotlin
suspend fun onDeleteTaskConfirmClick(task: TaskEntity) { ... }
```

This function:

1. Deletes the task through the repository.
2. Updates the UI state to reflect the deletion.

### UI State Handling

#### Task Click Events

- **onTaskClick**: Handles task click events.

```kotlin
fun onTaskClick(taskClicked: TaskEntity) { ... }
```

This function updates the UI state based on whether a task is in edit mode and which task was clicked.

#### Show Add New Task UI

- **onShowAddNewTaskClick**: Initiates the process to add a new task.

```kotlin
suspend fun onShowAddNewTaskClick() { ... }
```

This function:

1. Determines the task below the currently clicked task.
2. Updates the UI state to show the new task creation overlay.

#### Show Full Edit UI

- **onShowFullEditClick**: Initiates the full edit process for a task.

```kotlin
suspend fun onShowFullEditClick(taskInEdit: TaskEntity) { ... }
```

This function updates the UI state to display the full edit overlay for the specified task.

#### Show Quick Edit UI

- **onShowQuickEditClick**: Initiates the quick edit process for a task.

```kotlin
suspend fun onShowQuickEditClick(task: TaskEntity) { ... }
```

This function updates the UI state to display the quick edit inline UI for the specified task.

#### Show Task Details

- **onTaskDetailsClick**: Displays the details of a task.

```kotlin
fun onTaskDetailsClick(task: TaskEntity) { ... }
```

#### Show Completed Tasks

- **onShowCompletedTasksClick**: Displays the completed tasks dialog.

```kotlin
fun onShowCompletedTasksClick() { ... }
```

#### Task Long Press

- **onTaskLongPress**: Handles long-press events on a task.

```kotlin
fun onTaskLongPress(task: TaskEntity) { ... }
```

#### Cancel Events

- **onCancelClick**: Handles cancel events, resetting the UI state.

```kotlin
fun onCancelClick() { ... }
```

#### Date Picker Events

- **onDatePickerClick**: Displays the date picker dialog.

```kotlin
fun onDatePickerClick() { ... }
```

- **onDateChangeClick**: Handles the date change event.

```kotlin
fun onDateChangeClick(date: LocalDate) { ... }
```

#### Reset UI State

- **resetTaskCreationState**: Resets the task creation state.

```kotlin
fun resetTaskCreationState() { ... }
```

- **setUiStateToDefault**: Resets the entire UI state to default.

```kotlin
fun setUiStateToDefault() { ... }
```

### Task Completion Handling

#### Task Completions Flow

- **_taskCompletions**: A `MutableStateFlow` holding the list of task completion histories.
- **taskCompletions**: An immutable `StateFlow` exposed to the UI.

```kotlin
private val _taskCompletions = MutableStateFlow<List<TaskCompletionHistory>>(emptyList())
val taskCompletions: StateFlow<List<TaskCompletionHistory>> = _taskCompletions.asStateFlow()
```

#### Loading Task Completions

- **loadTaskCompletions**: Loads task completions into the flow.

```kotlin
fun loadTaskCompletions() { ... }
```

---

## UI Integration

### MainScreen

The `MainScreen` composable is the root UI component that integrates the `TasksViewModel`, handles UI state, and responds to user events.

#### State Observations

- **tasksByDate**: Observes tasks filtered by the selected date.
- **tasksCompleted**: Observes the list of completed tasks.
- **mainTasks**: Filters tasks of type `TaskTypes.MAIN`.
- **uiState**: Observes the overall UI state.
- **selectedDate**: Observes the currently selected date.

#### Event Handlers

- **EventsHandler**: Creates instances of `StateChangeEvents` and `DataOperationEvents` to pass down to child composables.

```kotlin
val eventsHandler = remember { EventsHandler(tasksViewModel) }
val onStateChangeEvents = eventsHandler.onStateChangeEvents()
val onDataOperationEvents = eventsHandler.onDataOperationEvents()
```

#### UI Components

- **AppDrawer**: The navigation drawer component.
- **AppTopBar**: The top app bar component.
- **AppBottomBar**: The bottom app bar component.
- **TasksLazyColumnAnimation**: Animates the visibility of the tasks list.
- **NewTaskScreenAnimation**: Animates the visibility of overlays like the new task creation screen.
- **PickDateDialog**: The date picker dialog.
- **TaskCompletionDialog**: Displays completed tasks.
- **SuccessIndicator**: Shows a success message after task creation.

#### Handling Overlays and Visibility

The `MainScreen` checks the `uiState.activeOverlayComponent` to determine which overlays to display and whether to show or hide certain UI components like the bottom bar or the tasks list.

### EventsHandler

The `EventsHandler` class bridges the `TasksViewModel` functions with the UI events.

```kotlin
class EventsHandler (
    private val tasksViewModel: TasksViewModel
) {
    fun onStateChangeEvents(): StateChangeEvents { ... }
    fun onDataOperationEvents(): DataOperationEvents { ... }
}
```

This allows UI components to easily access and invoke ViewModel functions in response to user interactions.

### ParentTaskItem

The `ParentTaskItem` composable represents each task item in the list and handles its own state and events.

#### State Determination

- **isThisTaskClicked**: Checks if the current task is the one that was clicked.
- **isThisTaskQuickEditing**: Checks if the current task is in quick edit mode.
- **isThisTaskFullEditing**: Checks if the current task is in full edit mode.
- **isThisTaskShowDetails**: Checks if the current task's details are being displayed.
- **isAnotherTaskEditing**: Checks if another task is currently being edited.

#### UI Components

- **HourColumn**: Displays the time and duration of the task.
- **InLineQuickEdit**: The inline quick edit UI for tasks.
- **FullEditDialog**: The full-screen edit dialog for tasks.
- **TaskDetailsDialog**: Displays the details of a task.
- **PrimaryTaskView**: The default view for a task when not in any special state.

#### Event Handling

- Uses `onStateChangeEvents` and `onDataOperationEvents` to respond to user interactions like taps, long presses, and context menu actions.

---

## Conclusion

This documentation outlines how state and events are managed within the RoutineTurboApp. By utilizing `StateFlow`, data classes, and sealed classes, the application efficiently manages complex UI states and provides responsive event handling.

- **State Management**: Centralized through `TasksViewModel` using `MutableStateFlow` and `StateFlow`.
- **Event Handling**: Encapsulated in `StateChangeEvents` and `DataOperationEvents`, passed down to UI components via `EventsHandler`.
- **UI Integration**: UI components observe state flows and invoke event handlers to update the UI and interact with the ViewModel.

This architecture ensures a scalable and maintainable codebase, facilitating the addition of new features and UI components with minimal impact on existing functionality.

---

