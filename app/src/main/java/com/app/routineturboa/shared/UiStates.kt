package com.app.routineturboa.shared

data class UiStates(
    // Variables with Boolean to either show or not show UI component
    val isQuickEditing: Boolean = false,
    val isFullEditing: Boolean = false,
    val isShowingDetails: Boolean = false,
    var isShowingCompletedTasks: Boolean = false,
    val isAddingNew: Boolean = false,
    val isShowingDatePicker: Boolean = false,
)