package com.app.routineturbo_android

data class Task(
    val id: Int,
    val startTime: String,
    val endTime: String,
    val duration: Int,
    val taskName: String,
    val reminders: String,
    val type: String,
    val position: Int
)
