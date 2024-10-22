package com.app.routineturboa.shared

import com.app.routineturboa.data.repository.TaskOperationResult
import com.app.routineturboa.data.room.TaskEntity
import com.app.routineturboa.ui.models.TaskFormData

data class DataOperationEvents(
    val onNewTaskConfirmClick: suspend(
        newTaskFormData: TaskFormData,
        clickedTask: TaskEntity,
        taskBelowClickedTask: TaskEntity
    ) -> Result<TaskOperationResult>,

    val onUpdateTaskConfirmClick: suspend (
        task: TaskEntity, updatedTaskFormData: TaskFormData
    ) -> Unit,

    val onDeleteTaskConfirmClick: suspend (task: TaskEntity) -> Unit
)