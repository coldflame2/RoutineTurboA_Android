package com.app.routineturboa.data.repository

data class TaskOperationResult(
    val success: Boolean,
    val newTaskId: Long? = null
)

