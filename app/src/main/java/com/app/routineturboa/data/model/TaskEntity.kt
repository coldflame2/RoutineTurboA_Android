package com.app.routineturboa.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.app.routineturboa.utils.Converters
import java.time.LocalDateTime

@Entity(tableName = "tasks_table")
@TypeConverters(Converters::class)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val notes: String,
    val duration: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val reminder: LocalDateTime,
    val type: String,
    val position: Int
)
