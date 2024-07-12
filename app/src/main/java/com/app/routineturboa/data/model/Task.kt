package com.app.routineturboa.data.model

data class Task(
    val id: Int,
    var startTime: String,
    var endTime: String,
    val duration: Int,
    val taskName: String,
    val reminder: String,
    val type: String,
    var position: Int
)
