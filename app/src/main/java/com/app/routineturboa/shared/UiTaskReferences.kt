package com.app.routineturboa.shared

import com.app.routineturboa.data.room.entities.TaskEntity

data class UiTaskReferences(
    val clickedTask: TaskEntity? = null,
    val longPressedTask: TaskEntity? = null,
    val taskBelowClickedTask: TaskEntity? = null,
    val inEditTask: TaskEntity? = null,  // Either quick or full edit
    val showingDetailsTask: TaskEntity? = null,
    val latestTask: TaskEntity? = null
)
