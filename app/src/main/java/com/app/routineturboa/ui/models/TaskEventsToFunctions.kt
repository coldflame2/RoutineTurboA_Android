package com.app.routineturboa.ui.models

data class TaskEventsToFunctions(
    val onAnyTaskClick: (Int) -> Unit,
    val onAnyTaskLongPress: (Int) -> Unit,

    val onQuickEditClick: (Int) -> Unit,
    val onFullEditClick: (Int) -> Unit,
    val onShowTaskDetails: (Int) -> Unit,

    val onAddNewClick: () -> Unit,
    val onNewTaskSaveClick: (TaskFormData) -> Unit,

    val onCancelClick: () -> Unit,
    val onConfirmEdit: (Int) -> Unit,
    val onDeleteClick: (Int) -> Unit,
)
