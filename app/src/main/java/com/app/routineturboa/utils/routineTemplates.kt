package com.app.routineturboa.utils

import com.app.routineturboa.data.model.TaskEntity
import com.app.routineturboa.utils.TimeUtils.strToDateTime


val firstTask = TaskEntity(
    position = 1,
    taskName = "Start of Day",
    notes = "",
    duration = 359,
    startTime = strToDateTime("00:01 AM"),
    endTime = strToDateTime("06:00 AM"),
    reminder = strToDateTime("06:00 AM"),
    type = "default"
)

val demoTaskOne = TaskEntity(
    position = 2,
    taskName = "Morning Exercise",
    notes = "30 minutes of exercise",
    duration = 30,
    startTime = strToDateTime("06:00 AM"),
    endTime = strToDateTime("06:30 AM"),
    reminder = strToDateTime("06:00 AM"),
    type = "default"
)

val demoTaskTwo = TaskEntity(
    position = 3,
    taskName = "Breakfast",
    notes = "Healthy breakfast",
    duration = 30,
    startTime = strToDateTime("06:30 AM"),
    endTime = strToDateTime("07:00 AM"),
    reminder = strToDateTime("06:30 AM"),
    type = "default"
)

val demoTaskThree = TaskEntity(
    position = 4,
    taskName = "Work Session 1",
    notes = "Focus on project A",
    duration = 120,
    startTime = strToDateTime("07:00 AM"),
    endTime = strToDateTime("09:00 AM"),
    reminder = strToDateTime("07:00 AM"),
    type = "default"
)

val demoTaskFour = TaskEntity(
    position = 5,
    taskName = "Break",
    notes = "Short break",
    duration = 15,
    startTime = strToDateTime("09:00 AM"),
    endTime = strToDateTime("09:15 AM"),
    reminder = strToDateTime("09:00 AM"),
    type = "default"
)

val demoTaskFive = TaskEntity(
    position = 6,
    taskName = "Work Session 2",
    notes = "Continue with project A",
    duration = 120,
    startTime = strToDateTime("09:15 AM"),
    endTime = strToDateTime("11:15 AM"),
    reminder = strToDateTime("09:15 AM"),
    type = "default"
)

val demoTaskSix = TaskEntity(
    position = 7,
    taskName = "Lunch Break",
    notes = "Healthy lunch",
    duration = 60,
    startTime = strToDateTime("11:15 AM"),
    endTime = strToDateTime("12:15 PM"),
    reminder = strToDateTime("11:15 AM"),
    type = "default"
)

val demoTaskSeven = TaskEntity(
    position = 8,
    taskName = "Work Session 3",
    notes = "Focus on project B",
    duration = 120,
    startTime = strToDateTime("12:15 PM"),
    endTime = strToDateTime("02:15 PM"),
    reminder = strToDateTime("12:15 PM"),
    type = "default"
)

val demoTaskEight = TaskEntity(
    position = 9,
    taskName = "Afternoon Break",
    notes = "Short break",
    duration = 15,
    startTime = strToDateTime("02:15 PM"),
    endTime = strToDateTime("02:30 PM"),
    reminder = strToDateTime("02:15 PM"),
    type = "default"
)

val lastTask = TaskEntity(
    position = Int.MAX_VALUE,
    taskName = "End of Day",
    notes = "",
    duration = 1079,
    startTime = strToDateTime("06:00 AM"),
    endTime = strToDateTime("11:59 PM"),
    reminder = strToDateTime("06:00 AM"),
    type = "default"
)
