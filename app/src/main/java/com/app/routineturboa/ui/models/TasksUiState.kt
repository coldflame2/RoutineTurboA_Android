package com.app.routineturboa.ui.models

import java.time.LocalDate

data class TasksUiState(
    // variables with specific task ID
    val clickedTaskId: Int? = null,
    val longPressedTaskId: Int? = null,
    val taskBelowClickedTaskId: Int? = null,
    val inEditTaskId: Int? = null,
    val showingDetailsTaskId: Int? = null,
    val selectedDate: LocalDate = LocalDate.now(),

    // Variables with Boolean to either show or not show UI component
    val isQuickEditing: Boolean = false,
    val isFullEditing: Boolean = false,
    val isShowingDetails: Boolean = false,
    var isShowingCompletedTasks: Boolean = false,
    val isAddingNew: Boolean = false,
)
