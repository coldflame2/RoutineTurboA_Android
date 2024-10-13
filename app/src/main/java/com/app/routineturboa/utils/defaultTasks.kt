package com.app.routineturboa.utils

import com.app.routineturboa.data.local.TaskEntity
import com.app.routineturboa.utils.TimeUtils.strToDateTime

fun defaultTasks(): List<TaskEntity> {
    return listOf(
        TaskEntity(
            position = 1,
            name = "Start of Day",
            notes = "",
            duration = 359,
            startTime = strToDateTime("00:01 AM"),
            endTime = strToDateTime("06:00 AM"),
            reminder = strToDateTime("06:00 AM"),
            type = "Default"
        ),

        TaskEntity(
            position = Int.MAX_VALUE,
            name = "End of Day",
            notes = "",
            duration = 1079,
            startTime = strToDateTime("06:00 AM"),
            endTime = strToDateTime("11:59 PM"),
            reminder = strToDateTime("06:00 AM"),
            type = "Default"
        )
    )
}