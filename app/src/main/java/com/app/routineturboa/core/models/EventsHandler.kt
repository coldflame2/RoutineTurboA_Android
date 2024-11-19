package com.app.routineturboa.core.models

import android.app.Activity
import android.content.Context
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

            onShowCompletedTasksClick = tasksViewModel::onShowFinishedTasksView,
            onDismissOrReset = tasksViewModel::onDismissOrReset,
            onShowDatePickerClick = tasksViewModel::onDatePickerClick,
            onDateChangeClick = tasksViewModel::onDateChangeClick,
        )
    }

    // Function to provide implementation for events related to data
    fun onDataOperationEvents(): DataOperationEvents {
        return DataOperationEvents(
            onNewTaskConfirmClick = tasksViewModel::onNewTaskConfirmClick,
            onUpdateTaskConfirmClick = tasksViewModel::onFullEditConfirm,
            onDeleteTaskConfirmClick = tasksViewModel::onDeleteTaskConfirmClick,

            onSignInClick = tasksViewModel::onSignInClick,
            onSyncClick = tasksViewModel::onSyncButtonClick,
            onSignOutClick = tasksViewModel::onSignOutClick,

            // data request events
            onMainTasksRequested = tasksViewModel::onMainTasksRequested,
        )
    }
}

data class DataOperationEvents(
    // region: newTask, UpdateTask, deleteTask, mainTasks requested
    val onNewTaskConfirmClick: suspend (newTaskForm: TaskFormData) -> TaskOperationState,
    val onUpdateTaskConfirmClick: suspend (updatedTaskFormData: TaskFormData) -> Unit,
    val onDeleteTaskConfirmClick: suspend (task: TaskEntity) -> Unit,
    val onSignInClick: (Activity) -> Unit,
    val onSyncClick: (Context) -> Unit,
    val onSignOutClick: (Activity) -> Unit,
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
    val onShowFullEditClick: suspend () -> Unit,
    val onShowTaskDetailsClick: (TaskEntity) -> Unit,

    val onShowCompletedTasksClick: () -> Unit,
    val onShowDatePickerClick: () -> Unit,

    // Functions to hide UI components
    val onDismissOrReset: (TaskEntity?) -> Unit,
    // endregion
)