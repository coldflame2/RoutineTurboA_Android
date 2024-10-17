package com.app.routineturboa.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.app.routineturboa.data.DbConstants
import com.app.routineturboa.utils.Converters
import java.time.LocalDateTime

@Entity(tableName = DbConstants.TASKS_TABLE)
@TypeConverters(Converters::class)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val notes: String = "",
    val duration: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val reminder: LocalDateTime,
    val type: String,
    val position: Int,
    val mainTaskId: Int? = null
)
