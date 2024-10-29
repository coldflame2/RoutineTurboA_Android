package com.app.routineturboa.core.models

import com.app.routineturboa.data.room.entities.TaskEntity
import com.app.routineturboa.ui.models.TaskFormData

data class UiState(
    val activeUiComponent: ActiveUiComponent = ActiveUiComponent.None,
    val stateBasedTasks: StateBasedTasks = StateBasedTasks(),
    val taskCreationState: TaskCreationState = TaskCreationState.Idle,
)

data class StateBasedTasks(
    val clickedTask: TaskEntity? = null,
    val longPressMenuTask: TaskEntity? = null,
    val taskBelowClickedTask: TaskEntity? = null,
    val inEditTask: TaskEntity? = null,
    val showingDetailsTask: TaskEntity? = null,
    val latestTask: TaskEntity? = null,
)

sealed class TaskCreationState {
    data object Idle : TaskCreationState()
    data object Loading : TaskCreationState()

    data class FillingDetails(
        val formData: TaskFormData // Integrates all task details
    ) : TaskCreationState()

    data class Success(val newTaskId: Int) : TaskCreationState()
    data class Error(val message: String) : TaskCreationState()
}

sealed class ActiveUiComponent {
    open fun shouldShowLazyColumn(): Boolean? = true

    data object AddingNew : ActiveUiComponent() {
        override fun shouldShowLazyColumn() = false
    }
    data object FullEditing : ActiveUiComponent() {
        override fun shouldShowLazyColumn() = true
    }
    data object QuickEditOverlay : ActiveUiComponent() {
        override fun shouldShowLazyColumn() = true
    }
    data object DetailsView : ActiveUiComponent() {
        override fun shouldShowLazyColumn() = true
    }
    data object LongPressMenu: ActiveUiComponent() {
        override fun shouldShowLazyColumn() = true
    }
    data object FinishedTasks : ActiveUiComponent()
    data object DatePicker : ActiveUiComponent()
    data object None : ActiveUiComponent()
}

data class TaskCreationOutcome(
    val success: Boolean,
    val newTaskId: Int? = null,
    val message: String = "Task operation successful"
)