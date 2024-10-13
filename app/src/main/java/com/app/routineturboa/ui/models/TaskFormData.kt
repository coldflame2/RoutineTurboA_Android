package com.app.routineturboa.ui.models

import java.time.LocalDateTime

data class TaskFormData(
    val name: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val notes: String,
    val taskType: String,
    val position: Int,
    val duration: Int,
    val reminder: LocalDateTime,
    val mainTaskId: Int?,
)