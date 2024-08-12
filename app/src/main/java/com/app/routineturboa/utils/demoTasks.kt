package com.app.routineturboa.utils

import android.content.Context
import com.app.routineturboa.R
import com.app.routineturboa.data.local.TaskEntity
import com.app.routineturboa.utils.TimeUtils.strToDateTime

fun getDemoTasks(context: Context): List<TaskEntity> {
    return listOf(
        TaskEntity(
            position = 2,
            name = "Morning Ritual",
            notes = "Listen to the tapes, get ready for the day",
            duration = 5,
            startTime = strToDateTime("06:00 AM"),
            endTime = strToDateTime("06:05 AM"),
            reminder = strToDateTime("06:00 AM"),
            type = context.getString(R.string.task_type_main)
        ),
        TaskEntity(
            position = 3,
            name = "Freshen up",
            notes = "",
            duration = 20,
            startTime = strToDateTime("06:05 AM"),
            endTime = strToDateTime("06:25 AM"),
            reminder = strToDateTime("06:05 AM"),
            type = context.getString(R.string.task_type_basics)
        ),
        TaskEntity(
            position = 4,
            name = "Ready for running",
            notes = "",
            duration = 10,
            startTime = strToDateTime("06:25 AM"),
            endTime = strToDateTime("06:35 AM"),
            reminder = strToDateTime("06:25 AM"),
            type = "default"
        ),
        TaskEntity(
            position = 5,
            name = "Running",
            notes = "",
            duration = 40,
            startTime = strToDateTime("06:35 AM"),
            endTime = strToDateTime("07:15 AM"),
            reminder = strToDateTime("06:35 AM"),
            type = context.getString(R.string.task_type_main)
        ),
        TaskEntity(
            position = 6,
            name = "Freshen up",
            notes = "",
            duration = 15,
            startTime = strToDateTime("07:15 AM"),
            endTime = strToDateTime("07:30 AM"),
            reminder = strToDateTime("07:15 AM"),
            type = "default"
        ),
        TaskEntity(
            position = 7,
            name = "Check emails and stuff",
            notes = "",
            duration = 30,
            startTime = strToDateTime("07:30 AM"),
            endTime = strToDateTime("08:00 AM"),
            reminder = strToDateTime("07:30 AM"),
            type = "default"
        ),
        TaskEntity(
            position = 8,
            name = "Work",
            notes = "",
            duration = 90,
            startTime = strToDateTime("08:00 AM"),
            endTime = strToDateTime("09:30 AM"),
            reminder = strToDateTime("08:00 AM"),
            type = context.getString(R.string.task_type_main)
        ),
        TaskEntity(
            position = 9,
            name = "Breakfast",
            notes = "",
            duration = 30,
            startTime = strToDateTime("09:30 AM"),
            endTime = strToDateTime("10:00 AM"),
            reminder = strToDateTime("09:30 AM"),
            type = context.getString(R.string.task_type_basics)
        ),
        TaskEntity(
            position = 10,
            name = "Work",
            notes = "",
            duration = 90,
            startTime = strToDateTime("10:00 AM"),
            endTime = strToDateTime("11:30 AM"),
            reminder = strToDateTime("10:00 AM"),
            type = context.getString(R.string.task_type_main)
        ),
        TaskEntity(
            position = 11,
            name = "Break",
            notes = "",
            duration = 30,
            startTime = strToDateTime("11:30 AM"),
            endTime = strToDateTime("12:00 PM"),
            reminder = strToDateTime("11:30 AM"),
            type = "default"
        ),
        TaskEntity(
            position = 12,
            name = "Work",
            notes = "",
            duration = 90,
            startTime = strToDateTime("12:00 PM"),
            endTime = strToDateTime("01:30 PM"),
            reminder = strToDateTime("12:00 PM"),
            type = context.getString(R.string.task_type_main)
        ),
        TaskEntity(
            position = 13,
            name = "Break",
            notes = "",
            duration = 30,
            startTime = strToDateTime("01:30 PM"),
            endTime = strToDateTime("02:00 PM"),
            reminder = strToDateTime("01:30 PM"),
            type = "default"
        ),
        TaskEntity(
            position = 14,
            name = "Lunch and Rest",
            notes = "",
            duration = 60,
            startTime = strToDateTime("02:00 PM"),
            endTime = strToDateTime("03:00 PM"),
            reminder = strToDateTime("02:00 PM"),
            type = "default"
        ),
        TaskEntity(
            position = 15,
            name = "Creative Work",
            notes = "",
            duration = 60,
            startTime = strToDateTime("03:00 PM"),
            endTime = strToDateTime("04:00 PM"),
            reminder = strToDateTime("03:00 PM"),
            type = "default"
        ),
        TaskEntity(
            position = 16,
            name = "Work",
            notes = "",
            duration = 90,
            startTime = strToDateTime("04:00 PM"),
            endTime = strToDateTime("05:30 PM"),
            reminder = strToDateTime("04:00 PM"),
            type = context.getString(R.string.task_type_main)
        ),
        TaskEntity(
            position = 17,
            name = "Tea",
            notes = "",
            duration = 30,
            startTime = strToDateTime("05:30 PM"),
            endTime = strToDateTime("06:00 PM"),
            reminder = strToDateTime("05:30 PM"),
            type = "default"
        ),
        TaskEntity(
            position = 18,
            name = "Leisure",
            notes = "",
            duration = 60,
            startTime = strToDateTime("06:00 PM"),
            endTime = strToDateTime("07:00 PM"),
            reminder = strToDateTime("06:00 PM"),
            type = "default"
        ),
        TaskEntity(
            position = 19,
            name = "Work",
            notes = "",
            duration = 120,
            startTime = strToDateTime("07:00 PM"),
            endTime = strToDateTime("09:00 PM"),
            reminder = strToDateTime("07:00 PM"),
            type = context.getString(R.string.task_type_main)
        ),
        TaskEntity(
            position = 20,
            name = "Dinner",
            notes = "",
            duration = 30,
            startTime = strToDateTime("09:00 PM"),
            endTime = strToDateTime("09:30 PM"),
            reminder = strToDateTime("09:00 PM"),
            type = "default"
        ),
        TaskEntity(
            position = 21,
            name = "Night Rituals",
            notes = "",
            duration = 30,
            startTime = strToDateTime("09:30 PM"),
            endTime = strToDateTime("10:00 PM"),
            reminder = strToDateTime("09:30 PM"),
            type = "default"
        ),
        TaskEntity(
            position = 22,
            name = "Read and Sleep",
            notes = "",
            duration = 30,
            startTime = strToDateTime("10:00 PM"),
            endTime = strToDateTime("10:30 PM"),
            reminder = strToDateTime("10:00 PM"),
            type = "default"
        )
    )
}
