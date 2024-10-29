package com.app.routineturboa.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.app.routineturboa.core.dbutils.DbConstants
import com.app.routineturboa.core.dbutils.RecurrenceType
import com.app.routineturboa.core.dbutils.Converters
import com.app.routineturboa.core.utils.TaskTypes
import java.time.LocalDate
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
    val recurrenceType: RecurrenceType? = null, // Use Enum here
    val recurrenceInterval: Int? = null, // Number of units between occurrences
    val recurrenceEndDate: LocalDate? = null // Optional end date
)