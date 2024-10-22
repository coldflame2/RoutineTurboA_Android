package com.app.routineturboa.utils

import com.app.routineturboa.data.room.TaskEntity
import com.app.routineturboa.data.dbutils.Converters.stringToTime
import com.app.routineturboa.data.dbutils.RecurrenceType
import java.time.LocalDate

fun getBasicTasksList(): List<TaskEntity> {
    return listOf(
        TaskEntity(
            position = 1,
            name = "Start of Day",
            notes = "",
            duration = 359,
            startTime = stringToTime("00:01 AM")!!,
            endTime = stringToTime("06:00 AM")!!,
            reminder = stringToTime("06:00 AM")!!,
            type = TaskTypes.BASICS,
            startDate = LocalDate.now(),
            isRecurring = true,
            recurrenceType = RecurrenceType.DAILY,
            recurrenceInterval = 1,
            recurrenceEndDate = LocalDate.now().plusDays(365)
        ),

        TaskEntity(
            position = Int.MAX_VALUE,
            name = "End of Day",
            notes = "",
            duration = 1079,
            startTime = stringToTime("06:00 AM"),
            endTime = stringToTime("11:59 PM"),
            reminder = stringToTime("06:00 AM"),
            type = TaskTypes.BASICS,
            startDate = LocalDate.now(),
            isRecurring = true,
            recurrenceType = RecurrenceType.DAILY,
            recurrenceInterval = 1,
            recurrenceEndDate = LocalDate.now().plusDays(365)
        )
    )
}