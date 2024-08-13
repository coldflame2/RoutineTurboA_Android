package com.app.routineturboa.ui.task

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.app.routineturboa.R
import com.app.routineturboa.data.local.TaskEntity
import com.app.routineturboa.reminders.ReminderManager
import com.app.routineturboa.ui.task.child_elements.ExtraTaskDetails
import com.app.routineturboa.ui.task.child_elements.HourColumn
import com.app.routineturboa.ui.task.child_elements.MainTaskDisplay
import com.app.routineturboa.ui.task.child_elements.QuickEdit
import com.app.routineturboa.ui.task.child_elements.TaskDropdownMenu
import com.app.routineturboa.ui.task.dialogs.TaskDetailsDialog
import com.app.routineturboa.ui.theme.LocalCustomColorsPalette
import com.app.routineturboa.viewmodel.TasksViewModel
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ParentTaskItem(
    tasksViewModel: TasksViewModel,
    reminderManager: ReminderManager,
    modifier: Modifier = Modifier,

    task: TaskEntity,
    onTaskClick: () -> Unit,
    isThisTaskClicked: Boolean,
    isThisTaskQuickEditing: Boolean,
    isThisTaskFullEditing: Boolean,
    isAnotherTaskEditing: Boolean,

    onStartQuickEdit: () -> Unit,
    onStartFullEdit: () -> Unit,
    onEndEditing: () -> Unit,

    canDelete: Boolean,
    onDelete: (TaskEntity) -> Unit,
) {
    val density = LocalDensity.current
    val context = LocalContext.current
    val mainTaskTypeString = context.getString(R.string.task_type_main)
    val mainTasks = tasksViewModel.getTasksByType(mainTaskTypeString).collectAsState(
        initial = emptyList())
    var isDropDownExpanded by remember { mutableStateOf(false) }
    var longPressOffset by remember { mutableStateOf(Offset.Zero) }
    val isShowNotes = remember { mutableStateOf(false) }
    val currentTime = LocalDateTime.now()
    val isTaskNow = currentTime.isAfter(task.startTime) && currentTime.isBefore(task.endTime)
    var isShowTaskDetails by remember { mutableStateOf(false) }
    val cardHeight: Dp = when {
        isThisTaskQuickEditing -> 120.dp // if in-edit mode
        task.type == context.getString(R.string.task_type_quick) -> 45.dp
        task.type == context.getString(R.string.task_type_main) -> (1.dp * task.duration).coerceAtLeast(45.dp)
        task.type == context.getString(R.string.task_type_helper) -> 35.dp
        task.type == context.getString(R.string.task_type_basics) -> 35.dp
        else -> 30.dp
    }
    val cardBgColor = when (task.type) {
        context.getString(R.string.task_type_main)
        -> LocalCustomColorsPalette.current.mainTaskColor.copy(alpha=0.3f)
        context.getString(R.string.task_type_basics)
        -> LocalCustomColorsPalette.current.helperTaskColor.copy(alpha=0.3f)
        context.getString(R.string.task_type_helper)
        -> LocalCustomColorsPalette.current.quickTaskColor.copy(alpha=0.3f)
        context.getString(R.string.task_type_quick)
        -> LocalCustomColorsPalette.current.quickTaskColor.copy(alpha=0.3f)

        else -> MaterialTheme.colorScheme.surface.copy(alpha=1f)
    }
    val cardBorder = when {
        isTaskNow -> null
        isThisTaskQuickEditing -> null
        isThisTaskClicked -> null
        else -> null
    }

    Box(
        modifier = modifier
            .alpha(if (isAnotherTaskEditing) 0.5f else 1f)
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { offset ->
                        longPressOffset = offset
                        isDropDownExpanded = true
                        onTaskClick()
                    },
                    onTap = {
                        onTaskClick()
                    }
                )
            }
    ) {
        Row {

            HourColumn(
                isThisTaskClicked = isThisTaskClicked,
                isThisTaskNow = isTaskNow,
                cardHeight = cardHeight,
                startTime = task.startTime
            )

            Spacer(modifier = Modifier.width(3.dp))

            Card(
                modifier = modifier
                    .alpha(if (isAnotherTaskEditing) 0.8f else 1f)
                    .height(cardHeight)
                    .fillMaxWidth()
                    .padding(1.dp, 5.dp, 1.dp, 0.dp),
                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                shape = RoundedCornerShape(0.dp),
                border = cardBorder
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            if (isThisTaskQuickEditing) 2.dp else 5.dp,
                            if (isThisTaskQuickEditing) 2.dp else 4.dp,
                            8.dp,
                            0.dp,
                        )
                        .height(cardHeight)
                ) {

                    if (isThisTaskQuickEditing) {
                        QuickEdit(
                            mainTasks = mainTasks,
                            task = task,
                            onEndEditing = { onEndEditing() },
                            tasksViewModel = tasksViewModel,
                            reminderManager = reminderManager,
                        )
                    } else {
                        MainTaskDisplay(
                            taskName = task.name,
                            taskDuration = task.duration,
                            taskType = task.type,
                            isShowNotes = isShowNotes,
                            isThisTaskClicked = isThisTaskClicked,
                            onStartQuickEdit = onStartQuickEdit
                        )

                        if (task.type != "QuickTask") {

                            Spacer(modifier = Modifier.height(8.dp))

                            /**
                             * This is shown below MainTaskDisplay and is based on the height of the card.
                             */
                            ExtraTaskDetails(
                                startTime = task.startTime,
                                endTime = task.endTime,
                                duration = task.duration,
                            )

                            if (isShowTaskDetails) {
                                TaskDetailsDialog(task = task,
                                    onDismiss = { isShowTaskDetails = false })
                            }
                        }
                    }
                }
            }
        }

        // This has to be inside the parent box for dropdown menu to position correctly
        if (isDropDownExpanded){
            TaskDropdownMenu(
                onEditClick = {
                    isDropDownExpanded = false
                    onStartFullEdit()
                },
                canDelete = canDelete,
                onDeleteClick = {
                    isDropDownExpanded = false
                    if (canDelete) {
                        onDelete(task)
                    }
                },
                onViewClick = { isShowTaskDetails = true },
                onDismissRequest = { isDropDownExpanded = false },
                offset = DpOffset(
                    x = with(density) { longPressOffset.x.toDp() + 10.dp },
                    y = with(density) { longPressOffset.y.toDp() - 112.dp }
                )
            )
        }
    }

    if (isShowNotes.value) {
        AlertDialog(
            onDismissRequest = { isShowNotes.value = false },
            title = { Text(text = "Task Notes") },
            text = { Text(text = task.notes) },
            confirmButton = {
                Button(onClick = { isShowNotes.value = false }) {
                    Text("Close")
                }
            }
        )
    }
}