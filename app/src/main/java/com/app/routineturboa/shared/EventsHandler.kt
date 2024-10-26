package com.app.routineturboa.shared

import com.app.routineturboa.viewmodel.TasksViewModel

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
            onCancelClick = tasksViewModel::onCancelClick,
            onShowDatePickerClick = tasksViewModel::onDatePickerClick,
            resetTaskCreationState = tasksViewModel::resetTaskCreationState,
            onDateChangeClick = tasksViewModel::onDateChangeClick,
        )
    }

    // Function to provide EventsOnData implementation
    fun onDataOperationEvents(): DataOperationEvents {
        return DataOperationEvents(
            onNewTaskConfirmClick = tasksViewModel::onNewTaskConfirmClick,
            onUpdateTaskConfirmClick = tasksViewModel::onUpdateTaskConfirmClick,
            onDeleteTaskConfirmClick = tasksViewModel::onDeleteTaskConfirmClick
        )
    }
}