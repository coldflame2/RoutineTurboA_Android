package com.app.routineturboa.ui.models

import com.app.routineturboa.data.dbutils.RecurrenceType
import com.app.routineturboa.utils.TaskTypes
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.LocalTime

data class TaskFormData(
    val id: Int = 0,
    val name: String,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val notes: String?,
    val taskType: String? = TaskTypes.UNDEFINED,
    val position: Int?,
    val duration: Int?,
    val reminder: LocalTime?,
    val mainTaskId: Int?,

    // Recurrence-related fields
    val startDate: LocalDate? = null,
    val isRecurring: Boolean = false,
    val recurrenceType: RecurrenceType? = null, // e.g., "DAILY", "WEEKLY", "MONTHLY"
    val recurrenceInterval: Int? = null, // Number of units between occurrences
    val recurrenceEndDate: LocalDate? = null // Optional end date for the recurrence
)