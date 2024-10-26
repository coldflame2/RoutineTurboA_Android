package com.app.routineturboa.shared.states

data class UiState(
    val uiTaskReferences: UiTaskReferences = UiTaskReferences(),
    val activeUiComponent: ActiveUiComponent = ActiveUiComponent.None,
    val taskCreationState: TaskCreationState = TaskCreationState.Idle,
)