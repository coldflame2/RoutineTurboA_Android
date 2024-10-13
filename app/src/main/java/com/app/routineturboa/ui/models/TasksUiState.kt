package com.app.routineturboa.ui.models

data class TasksUiState(
    val clickedTaskId: Int? = null,
    val longPressedTaskId: Int? = null,
    val taskBelowClickedTaskId: Int? = null,
    val isQuickEditing: Boolean = false,
    val isFullEditing: Boolean = false,
    val inEditTaskId: Int? = null,
    val isShowingDetails: Boolean = false,
    val showingDetailsTaskId: Int? = null,
    val isAddingNew: Boolean = false,
)
