package com.app.routineturboa.core.utils

import com.app.routineturboa.data.room.entities.TaskEntity
import com.app.routineturboa.core.dbutils.Converters.uiStringToTime
import com.app.routineturboa.core.dbutils.TaskTypes
import java.time.LocalDate

fun getBasicTasksList(
    selectedDate: LocalDate = LocalDate.now()
): List<TaskEntity> {
    return listOf(
        TaskEntity(
            position = 1,
            name = "Sleep",
            notes = "",
            duration = 359,
            startTime = uiStringToTime("12:01:01 AM"),
            endTime = uiStringToTime("06:00:01 AM"),
            reminder = uiStringToTime("06:00:01 AM"),
            type = TaskTypes.BASICS,
            startDate = selectedDate,
            isRecurring = false,
        ),

        TaskEntity(
            position = 2,
            name = "Wake up",
            notes = "",
            duration = 1,
            startTime = uiStringToTime("06:00:01 AM"),
            endTime = uiStringToTime("06:02:01 AM"),
            reminder = uiStringToTime("06:00:01 AM"),
            type = TaskTypes.QUICK,
            startDate = selectedDate,
            isRecurring = false,
        ),

        TaskEntity(
            position = 3,
            name = "Day's Work",
            notes = "",
            duration = 958,
            startTime = uiStringToTime("06:02:01 AM"),
            endTime = uiStringToTime("10:00:01 PM"),
            reminder = uiStringToTime("06:02:01 AM"),
            type = TaskTypes.MAIN,
            startDate = selectedDate,
            isRecurring = false,
        ),

        TaskEntity(
            position = 4,
            name = "Before Sleep rituals",
            notes = "",
            duration = 30,
            startTime = uiStringToTime("10:00:01 PM"),
            endTime = uiStringToTime("10:30:01 PM"),
            reminder = uiStringToTime("10:00:01 AM"),
            type = TaskTypes.MAIN,
            startDate = selectedDate,
            isRecurring = false,
        ),

        TaskEntity(
            position = 5,
            name = "Sleep",
            notes = "",
            duration = 89,
            startTime = uiStringToTime("10:30:01 PM"),
            endTime = uiStringToTime("11:59:01 PM"),
            reminder = uiStringToTime("10:30:01 AM"),
            type = TaskTypes.MAIN,
            startDate = selectedDate,
            isRecurring = false,
        ),


    )
}