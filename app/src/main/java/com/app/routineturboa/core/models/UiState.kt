package com.app.routineturboa.core.models

import com.app.routineturboa.data.room.entities.TaskEntity
import com.app.routineturboa.ui.models.TaskFormData

data class UiState(
    val uiScreen: UiScreen = UiScreen.None,
    val taskContext: TaskContext = TaskContext(),

    val taskOperationState: TaskOperationState = TaskOperationState.Idle,

    val msalAuthState: MsalAuthState = MsalAuthState(),
    val onedriveSyncState: OnedriveSyncState = OnedriveSyncState.Idle,
)

data class TaskContext(
    val clickedTask: TaskEntity? = null,
    val longPressMenuTask: TaskEntity? = null,
    val taskBelowClickedTask: TaskEntity? = null,
    val inEditTask: TaskEntity? = null,
    val showingDetailsTask: TaskEntity? = null,
    val latestTasks: List<TaskEntity> = emptyList()
)

sealed class TaskOperationState {
    data object Idle : TaskOperationState()
    data object Loading : TaskOperationState()

    data class FillingDetails(
        val operationType: TaskOperationType, // Indicates if adding new or editing
        val formData: TaskFormData,
    ) : TaskOperationState()

    data class Success(
        val operationType: TaskOperationType,
        val taskId: Int,
        val message: String? = null
    ) : TaskOperationState()

    data class Error(
        val operationType: TaskOperationType,
        val taskId: Int,
        val message: String? = null
    ) : TaskOperationState()
}

enum class TaskOperationType {
    NEW_TASK,
    EDIT_TASK,
    DELETE_TASK
}

sealed class OnedriveSyncState {
    data object Idle : OnedriveSyncState()
    data object Loading : OnedriveSyncState()
    data object Success : OnedriveSyncState()
    data class Error(val message: String) : OnedriveSyncState()
}

data class MsalAuthState(
    val isSignedIn: Boolean = false,
    val username: String = "",
    val profileImageUrl: String? = null,
    val signInStatus: SignInStatus = SignInStatus.Idle // New property to track sign-in status
)

// Enum to represent sign-in statuses
enum class SignInStatus {
    Idle,       // No ongoing sign-in process
    SigningIn,  // User is currently signing in
    SignedIn,   // User has successfully signed in
    Error       // An error occurred during sign-in
}

sealed class UiScreen {
    open fun shouldShowLazyColumn(): Boolean? = true

    data object AddingNew : UiScreen() {
        override fun shouldShowLazyColumn() = false
    }
    data object FullEditing : UiScreen() {
        override fun shouldShowLazyColumn() = true
    }
    data object QuickEditOverlay : UiScreen() {
        override fun shouldShowLazyColumn() = true
    }
    data object DetailsView : UiScreen() {
        override fun shouldShowLazyColumn() = true
    }
    data object LongPressMenu: UiScreen() {
        override fun shouldShowLazyColumn() = true
    }
    data object FinishedTasksView : UiScreen()
    data object DatePicker : UiScreen()
    data object None : UiScreen()
}

