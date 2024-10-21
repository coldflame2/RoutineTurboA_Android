package com.app.routineturboa.shared

import com.app.routineturboa.data.room.TaskEntity

data class TasksBasedOnState(
    // tasks by their state
    val clickedTask: TaskEntity? = null,
    val longPressedTask: TaskEntity? = null,
    val taskBelowClickedTask: TaskEntity? = null,
    val inEditTask: TaskEntity? = null,  // either quick and full edit
    val showingDetailsTask: TaskEntity? = null,
)