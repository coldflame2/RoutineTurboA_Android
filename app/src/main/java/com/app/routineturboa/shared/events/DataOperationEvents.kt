package com.app.routineturboa.shared.events

import com.app.routineturboa.data.repository.TaskCreationOutcome
import com.app.routineturboa.data.room.entities.TaskEntity
import com.app.routineturboa.ui.models.TaskFormData

data class DataOperationEvents(
    val onNewTaskConfirmClick: suspend(
        clicked: TaskEntity,
        belowClicked: TaskEntity,
        newTaskForm: TaskFormData,
    ) -> Result<TaskCreationOutcome>,

    val onUpdateTaskConfirmClick: suspend (
        updatedTaskFormData: TaskFormData
    ) -> Unit,

    val onDeleteTaskConfirmClick: suspend (task: TaskEntity) -> Unit
)