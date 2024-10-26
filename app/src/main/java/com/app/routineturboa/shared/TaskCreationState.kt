package com.app.routineturboa.shared

sealed class TaskCreationState {
    data object Idle : TaskCreationState()
    data object Loading : TaskCreationState()
    data class Success(val newTaskId: Int) : TaskCreationState()
    data class Error(val message: String) : TaskCreationState()
}
