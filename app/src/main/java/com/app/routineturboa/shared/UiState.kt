package com.app.routineturboa.shared

data class UiState(
    val uiTaskReferences: UiTaskReferences = UiTaskReferences(),
    val activeOverlayComponent: ActiveOverlayComponent = ActiveOverlayComponent.None,
    val taskCreationState: TaskCreationState = TaskCreationState.Idle,

    // New flag to control TasksLazyColumn visibility independently
    val isBaseTasksLazyColumnVisible: Boolean = true
)

