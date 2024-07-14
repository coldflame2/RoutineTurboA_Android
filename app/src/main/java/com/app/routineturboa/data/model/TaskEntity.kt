package com.app.routineturboa.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val taskName: String,
    val duration: Int,
    val startTime: String,
    val endTime: String,
    val reminder: String,
    val type: String,
    val position: Int
)
