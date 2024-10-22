package com.app.routineturboa.utils

import com.app.routineturboa.data.room.TaskEntity
import com.app.routineturboa.data.dbutils.Converters.uiStringToTime
import com.app.routineturboa.data.dbutils.RecurrenceType
import java.time.Duration
import java.time.LocalDate

fun getSampleTasksList(
    selectedDate: LocalDate = LocalDate.now()
): List<TaskEntity> {
    return listOf(

        TaskEntity(
            position = 1,
            name = "Sleep-Wake up",
            notes = "",
            duration = 359,
            startTime = uiStringToTime("12:01:01 AM"),
            endTime = uiStringToTime("06:00:01 AM"),
            reminder = uiStringToTime("06:00:01 AM"),
            type = TaskTypes.BASICS,
            startDate = selectedDate,
            isRecurring = true,
            recurrenceType = RecurrenceType.DAILY,
            recurrenceInterval = 1,
            recurrenceEndDate = selectedDate.plusDays(365)
        ),
        TaskEntity(
            position = 2,
            name = "Morning Ritual",
            notes = "Listen to the tapes, get ready for the day",
            duration = 5,
            startTime = uiStringToTime("06:00:01 AM"),
            endTime = uiStringToTime("06:05:01 AM"),
            reminder = uiStringToTime("06:00:01 AM"),
            type = TaskTypes.MAIN,
            startDate = selectedDate,
            isRecurring = true,
            recurrenceType = RecurrenceType.DAILY,
            recurrenceInterval = 1,
            recurrenceEndDate = selectedDate.plusDays(365)
        ),
        TaskEntity(
            position = 3,
            name = "Freshen up",
            notes = "",
            duration = 20,
            startTime = uiStringToTime("06:05:01 AM"),
            endTime = uiStringToTime("06:25:01 AM"),
            reminder = uiStringToTime("06:05:01 AM"),
            type = TaskTypes.BASICS,
            startDate = selectedDate,
            isRecurring = true,
            recurrenceType = RecurrenceType.DAILY,
            recurrenceInterval = 1,
            recurrenceEndDate = selectedDate.plusDays(365)
        ),
        TaskEntity(
            position = 4,
            name = "Ready for running",
            notes = "",
            duration = 10,
            startTime = uiStringToTime("06:25:01 AM"),
            endTime = uiStringToTime("06:35:01 AM"),
            reminder = uiStringToTime("06:25:01 AM"),
            type = TaskTypes.BASICS,
            startDate = selectedDate,
            isRecurring = true,
            recurrenceType = RecurrenceType.DAILY,
            recurrenceInterval = 1,
            recurrenceEndDate = selectedDate.plusDays(365)
        ),
        TaskEntity(
            position = 5,
            name = "Running",
            notes = "",
            duration = 40,
            startTime = uiStringToTime("06:35:01 AM"),
            endTime = uiStringToTime("07:15:01 AM"),
            reminder = uiStringToTime("06:35:01 AM"),
            type = TaskTypes.MAIN,
            startDate = selectedDate,
            isRecurring = true,
            recurrenceType = RecurrenceType.DAILY,
            recurrenceInterval = 1,
            recurrenceEndDate = selectedDate.plusDays(365)
        ),
        TaskEntity(
            position = 6,
            name = "Freshen up",
            notes = "",
            duration = 15,
            startTime = uiStringToTime("07:15:01 AM"),
            endTime = uiStringToTime("07:30:01 AM"),
            reminder = uiStringToTime("07:15:01 AM"),
            type = TaskTypes.BASICS,
            startDate = selectedDate,
            isRecurring = true,
            recurrenceType = RecurrenceType.DAILY,
            recurrenceInterval = 1,
            recurrenceEndDate = selectedDate.plusDays(365)
        ),
        TaskEntity(
            position = 7,
            name = "Check emails and stuff",
            notes = "",
            duration = 30,
            startTime = uiStringToTime("07:30:01 AM"),
            endTime = uiStringToTime("08:00:01 AM"),
            reminder = uiStringToTime("07:30:01 AM"),
            type = TaskTypes.BASICS,
            startDate = selectedDate,
            isRecurring = true,
            recurrenceType = RecurrenceType.DAILY,
            recurrenceInterval = 1,
            recurrenceEndDate = selectedDate.plusDays(365)
        ),
        TaskEntity(
            position = 8,
            name = "Work",
            notes = "",
            duration = 90,
            startTime = uiStringToTime("08:00:01 AM"),
            endTime = uiStringToTime("09:30:01 AM"),
            reminder = uiStringToTime("08:00:01 AM"),
            type = TaskTypes.MAIN,
            startDate = selectedDate,
            isRecurring = true,
            recurrenceType = RecurrenceType.DAILY,
            recurrenceInterval = 1,
            recurrenceEndDate = selectedDate.plusDays(365)
        ),
        TaskEntity(
            position = 9,
            name = "Breakfast",
            notes = "",
            duration = 30,
            startTime = uiStringToTime("09:30:01 AM"),
            endTime = uiStringToTime("10:00:01 AM"),
            reminder = uiStringToTime("09:30:01 AM"),
            type = TaskTypes.BASICS,
            startDate = selectedDate,
            isRecurring = true,
            recurrenceType = RecurrenceType.DAILY,
            recurrenceInterval = 1,
            recurrenceEndDate = selectedDate.plusDays(365)
        ),
        TaskEntity(
            position = 10,
            name = "Work",
            notes = "",
            duration = 90,
            startTime = uiStringToTime("10:00:01 AM"),
            endTime = uiStringToTime("11:30:01 AM"),
            reminder = uiStringToTime("10:00:01 AM"),
            type = TaskTypes.MAIN,
            startDate = selectedDate,
            isRecurring = true,
            recurrenceType = RecurrenceType.DAILY,
            recurrenceInterval = 1,
            recurrenceEndDate = selectedDate.plusDays(365)
        ),
        TaskEntity(
            position = 11,
            name = "Break",
            notes = "",
            duration = 30,
            startTime = uiStringToTime("11:30:01 AM"),
            endTime = uiStringToTime("12:00 PM"),
            reminder = uiStringToTime("11:30:01 AM"),
            type = TaskTypes.BASICS,
            startDate = selectedDate,
            isRecurring = true,
            recurrenceType = RecurrenceType.DAILY,
            recurrenceInterval = 1,
            recurrenceEndDate = selectedDate.plusDays(365)
        ),
        TaskEntity(
            position = 12,
            name = "Work",
            notes = "",
            duration = 90,
            startTime = uiStringToTime("12:00 PM"),
            endTime = uiStringToTime("01:30 PM"),
            reminder = uiStringToTime("12:00 PM"),
            type = TaskTypes.MAIN,
            startDate = selectedDate,
            isRecurring = true,
            recurrenceType = RecurrenceType.DAILY,
            recurrenceInterval = 1,
            recurrenceEndDate = selectedDate.plusDays(365)
        ),
        TaskEntity(
            position = 13,
            name = "Break",
            notes = "",
            duration = 30,
            startTime = uiStringToTime("01:30 PM"),
            endTime = uiStringToTime("02:00 PM"),
            reminder = uiStringToTime("01:30 PM"),
            type = TaskTypes.BASICS,
            startDate = selectedDate,
            isRecurring = true,
            recurrenceType = RecurrenceType.DAILY,
            recurrenceInterval = 1,
            recurrenceEndDate = selectedDate.plusDays(365)
        ),
        TaskEntity(
            position = 14,
            name = "Lunch and Rest",
            notes = "",
            duration = 60,
            startTime = uiStringToTime("02:00 PM"),
            endTime = uiStringToTime("03:00 PM"),
            reminder = uiStringToTime("02:00 PM"),
            type = TaskTypes.BASICS,
            startDate = selectedDate,
            isRecurring = true,
            recurrenceType = RecurrenceType.DAILY,
            recurrenceInterval = 1,
            recurrenceEndDate = selectedDate.plusDays(365)
        ),
        TaskEntity(
            position = 15,
            name = "Creative Work",
            notes = "",
            duration = 60,
            startTime = uiStringToTime("03:00 PM"),
            endTime = uiStringToTime("04:00 PM"),
            reminder = uiStringToTime("03:00 PM"),
            type = TaskTypes.MAIN,
            startDate = selectedDate,
            isRecurring = true,
            recurrenceType = RecurrenceType.DAILY,
            recurrenceInterval = 1,
            recurrenceEndDate = selectedDate.plusDays(365)
        ),
        TaskEntity(
            position = 16,
            name = "Work",
            notes = "",
            duration = 90,
            startTime = uiStringToTime("04:00 PM"),
            endTime = uiStringToTime("05:30 PM"),
            reminder = uiStringToTime("04:00 PM"),
            type = TaskTypes.MAIN,
            startDate = selectedDate,
            isRecurring = true,
            recurrenceType = RecurrenceType.DAILY,
            recurrenceInterval = 1,
            recurrenceEndDate = selectedDate.plusDays(365)
        ),
        TaskEntity(
            position = 17,
            name = "Tea",
            notes = "",
            duration = 30,
            startTime = uiStringToTime("05:30 PM"),
            endTime = uiStringToTime("05:50 PM"),
            reminder = uiStringToTime("05:30 PM"),
            type = TaskTypes.BASICS,
            startDate = selectedDate,
            isRecurring = true,
            recurrenceType = RecurrenceType.DAILY,
            recurrenceInterval = 1,
            recurrenceEndDate = selectedDate.plusDays(365)
        ),
        TaskEntity(
            position = 18,
            name = "Leisure",
            notes = "",
            duration = 60,
            startTime = uiStringToTime("05:50 PM"),
            endTime = uiStringToTime("07:00 PM"),
            reminder = uiStringToTime("05:50 PM"),
            type = TaskTypes.BASICS,
            startDate = selectedDate,
            isRecurring = true,
            recurrenceType = RecurrenceType.DAILY,
            recurrenceInterval = 1,
            recurrenceEndDate = selectedDate.plusDays(365)
        ),
        TaskEntity(
            position = 19,
            name = "Work",
            notes = "",
            duration = 120,
            startTime = uiStringToTime("07:00 PM"),
            endTime = uiStringToTime("09:00 PM"),
            reminder = uiStringToTime("07:00 PM"),
            type = TaskTypes.MAIN,
            startDate = selectedDate,
            isRecurring = true,
            recurrenceType = RecurrenceType.DAILY,
            recurrenceInterval = 1,
            recurrenceEndDate = selectedDate.plusDays(365)
        ),
        TaskEntity(
            position = 20,
            name = "Dinner",
            notes = "",
            duration = 30,
            startTime = uiStringToTime("09:00 PM"),
            endTime = uiStringToTime("09:30 PM"),
            reminder = uiStringToTime("09:00 PM"),
            type = TaskTypes.BASICS,
            startDate = selectedDate,
            isRecurring = true,
            recurrenceType = RecurrenceType.DAILY,
            recurrenceInterval = 1,
            recurrenceEndDate = selectedDate.plusDays(365)
        ),
        TaskEntity(
            position = 21,
            name = "Night Rituals",
            notes = "",
            duration = 30,
            startTime = uiStringToTime("09:30 PM"),
            endTime = uiStringToTime("10:00 PM"),
            reminder = uiStringToTime("09:30 PM"),
            type = TaskTypes.BASICS,
            startDate = selectedDate,
            isRecurring = true,
            recurrenceType = RecurrenceType.DAILY,
            recurrenceInterval = 1,
            recurrenceEndDate = selectedDate.plusDays(365)
        ),
        TaskEntity(
            position = 22,
            name = "Read and Sleep",
            notes = "",
            duration = 30,
            startTime = uiStringToTime("10:00 PM"),
            endTime = uiStringToTime("10:30 PM"),
            reminder = uiStringToTime("10:00 PM"),
            type = TaskTypes.BASICS,
            startDate = selectedDate,
            isRecurring = true,
            recurrenceType = RecurrenceType.DAILY,
            recurrenceInterval = 1,
            recurrenceEndDate = selectedDate.plusDays(365)
        ),
        TaskEntity(
            position = 23,
            name = "Special for Today",
            notes = "",
            duration = 15,
            startTime = uiStringToTime("10:30:01 PM"),
            endTime = uiStringToTime("10:45 PM"),
            reminder = uiStringToTime("10:30 PM"),
            type = TaskTypes.BASICS,
            startDate = selectedDate,
            isRecurring = false
        ),
        TaskEntity(
            position = 24,
            name = "End of day",
            notes = "",
            duration = Duration.between(
                uiStringToTime("10:45 PM"), uiStringToTime("11:59 PM")
            )
            .toMinutes()
            .toInt(),
            startTime = uiStringToTime("10:45 PM"),
            endTime = uiStringToTime("11:59 PM"),
            reminder = uiStringToTime("10:45 PM"),
            type = TaskTypes.BASICS,
            startDate = selectedDate,
            isRecurring = true,
            recurrenceType = RecurrenceType.DAILY,
            recurrenceInterval = 1,
            recurrenceEndDate = selectedDate.plusDays(365)
        )

    )
}
