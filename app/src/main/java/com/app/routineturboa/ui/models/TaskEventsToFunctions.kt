package com.app.routineturboa.ui.models

import java.time.LocalDate

data class TaskEventsToFunctions(
    val onAnyTaskClick: (Int) -> Unit,
    val onAnyTaskLongPress: (Int) -> Unit,

    val onShowAddNewClick: () -> Unit,
    val onShowQuickEditClick: (Int) -> Unit,
    val onShowFullEditClick: (Int) -> Unit,
    val onShowTaskDetails: (Int) -> Unit,
    val onShowCompletedTasks: () -> Unit,

    val onCancelClick: () -> Unit,

    val onNewTaskSaveClick: (TaskFormData) -> Unit,
    val onConfirmEdit: (Int, TaskFormData) -> Unit,
    val onDeleteClick: (Int) -> Unit,

    val onDateChange: (LocalDate) -> Unit,
)
