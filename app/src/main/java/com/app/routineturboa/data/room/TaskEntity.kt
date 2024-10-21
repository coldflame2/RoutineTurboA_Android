package com.app.routineturboa.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.app.routineturboa.data.DbConstants
import com.app.routineturboa.utils.Converters
import com.app.routineturboa.utils.TaskTypes
import kotlinx.coroutines.CoroutineStart
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity(tableName = DbConstants.TASKS_TABLE)
@TypeConverters(Converters::class)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "New",
    val notes: String? = "",
    val duration: Int? = 1,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val reminder: LocalTime?,
    val type: String? = TaskTypes.UNDEFINED,
    val position: Int? = 1,
    val mainTaskId: Int? = null,

    val startDate: LocalDate? = null,

    // Recurrence fields
    val isRecurring: Boolean? = false,
    val recurrenceType: String? = null, // e.g., "DAILY", "WEEKLY", "MONTHLY"
    val recurrenceInterval: Int? = null, // Number of units between occurrences
    val recurrenceEndDate: LocalDate? = null // Optional end date
)