package com.app.routineturboa.shared.events

import com.app.routineturboa.data.room.entities.TaskEntity
import java.time.LocalDate

data class StateChangeEvents(
    // Events to update State-based tasks and date
    val onTaskClick: (TaskEntity) -> Unit,
    val onTaskLongPress: (TaskEntity) -> Unit,
    val onDateChangeClick: (LocalDate) -> Unit,

    // Events to set UI state items as true for showing UI components
    val onShowAddNewTaskClick: suspend () -> Unit,
    val onShowQuickEditClick: suspend (TaskEntity) -> Unit,
    val onShowFullEditClick: suspend (TaskEntity) -> Unit,
    val onShowTaskDetailsClick: (TaskEntity) -> Unit,
    val onShowCompletedTasksClick: () -> Unit,
    val onShowDatePickerClick: () -> Unit,

    // Functions to hide UI components
    val resetTaskCreationState: () -> Unit,
    val onCancelClick: () -> Unit,
)