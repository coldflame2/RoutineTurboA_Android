package com.app.routineturboa.core.models

import com.app.routineturboa.data.room.entities.TaskEntity
import com.app.routineturboa.ui.models.TaskFormData
import com.app.routineturboa.viewmodel.TasksViewModel
import java.time.LocalDate

class EventsHandler (
    private val tasksViewModel: TasksViewModel
) {
    // Function to provide EventsOnStates implementation
    fun onStateChangeEvents(): StateChangeEvents {
        return StateChangeEvents(
            onTaskClick = tasksViewModel::onTaskClick,
            onTaskLongPress = tasksViewModel::onTaskLongPress,
            onShowAddNewTaskClick = tasksViewModel::onShowAddNewTaskClick,
            onShowQuickEditClick = tasksViewModel::onShowQuickEditClick,
            onShowFullEditClick = tasksViewModel::onShowFullEditClick,
            onShowTaskDetailsClick = tasksViewModel::onTaskDetailsClick,

            onShowCompletedTasksClick = tasksViewModel::onShowCompletedTasksClick,
            onDismissOrReset = tasksViewModel::onDismissOrReset,
            onShowDatePickerClick = tasksViewModel::onDatePickerClick,
            resetTaskCreationState = tasksViewModel::resetTaskCreationState,
            onDateChangeClick = tasksViewModel::onDateChangeClick,
        )
    }

    // Function to provide implementation for events related to data
    fun onDataOperationEvents(): DataOperationEvents {
        return DataOperationEvents(
            onNewTaskConfirmClick = tasksViewModel::onNewTaskConfirmClick,
            onUpdateTaskConfirmClick = tasksViewModel::onUpdateTaskConfirmClick,
            onDeleteTaskConfirmClick = tasksViewModel::onDeleteTaskConfirmClick,

            // data request events
            onMainTasksRequested = tasksViewModel::onMainTasksRequested,
        )
    }
}

data class DataOperationEvents(
    // region: newTask, UpdateTask, deleteTask, mainTasks requested
    val onNewTaskConfirmClick: suspend(
        clicked: TaskEntity,
        belowClicked: TaskEntity,
        newTaskForm: TaskFormData,
    ) -> Result<TaskCreationOutcome>,

    val onUpdateTaskConfirmClick: suspend (
        updatedTaskFormData: TaskFormData
    ) -> Unit,

    val onDeleteTaskConfirmClick: suspend (task: TaskEntity) -> Unit,

    val onMainTasksRequested: suspend () -> List<TaskEntity>,
    // endregion
)

data class StateChangeEvents(
    // region: Events to update UI state
    // Events to update State-based tasks and date
    val onTaskClick: (TaskEntity) -> Unit,
    val onTaskLongPress: (TaskEntity) -> Unit,
    val onDateChangeClick: (LocalDate) -> Unit,

    // Events to set UI state items as true for showing UI components
    val onShowAddNewTaskClick: suspend () -> Unit,
    val onShowQuickEditClick: suspend (TaskEntity) -> Unit,
    val onShowFullEditClick: suspend (TaskEntity) -> Unit,
    val onShowTaskDetailsClick: (TaskEntity) -> Unit,

    val onShowCompletedTasksClick: () -> Unit,
    val onShowDatePickerClick: () -> Unit,

    // Functions to hide UI components
    val resetTaskCreationState: () -> Unit,
    val onDismissOrReset: (TaskEntity?) -> Unit,
    // endregion
)