package com.app.routineturboa.data.repository

data class TaskOperationResult(
    val success: Boolean,
    val newTaskId: Int? = null,
    val message: String = "Task operation successful"
)

