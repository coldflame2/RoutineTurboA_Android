package com.app.routineturboa.ui.task

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.RectangleShape
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

    val taskType = task.type

    val mainType = context.getString(R.string.task_type_main)
    val basicsType = context.getString(R.string.task_type_basics)
    val helperType = context.getString(R.string.task_type_helper)
    val quickType = context.getString(R.string.task_type_quick)

    val mainTasks = tasksViewModel.getTasksByType(mainType).collectAsState(initial = emptyList())

    var isDropDownExpanded by remember { mutableStateOf(false) }
    var longPressOffset by remember { mutableStateOf(Offset.Zero) }
    val isShowNotes = remember { mutableStateOf(false) }
    val currentTime = LocalDateTime.now()
    val isTaskNow = currentTime.isAfter(task.startTime) && currentTime.isBefore(task.endTime)
    val isShowTaskDetails = remember { mutableStateOf(false) }

    val cardBgColor = when {
        isAnotherTaskEditing -> MaterialTheme.colorScheme.surface.copy(alpha = 1f)
        taskType == mainType -> LocalCustomColorsPalette.current.mainTaskColor.copy(alpha = 0.5f)
        taskType == basicsType -> LocalCustomColorsPalette.current.basicsTaskColor.copy(alpha = 0.5f)
        taskType == helperType -> LocalCustomColorsPalette.current.helperTaskColor.copy(alpha = 0.5f)
        taskType == quickType -> LocalCustomColorsPalette.current.quickTaskColor.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.surface.copy(alpha = 1f)
    }

    val cardHeight: Dp = when {
        isThisTaskQuickEditing -> 120.dp

        taskType == mainType -> (1.dp * task.duration).coerceAtLeast(35.dp)
        taskType == quickType -> 35.dp
        taskType == helperType -> 35.dp
        taskType == basicsType -> 35.dp

        else -> 30.dp
    }

    val cardBorder = when {
        isTaskNow -> null
        isThisTaskQuickEditing -> null
        isThisTaskClicked -> null
        else -> null
    }

    val cardPadding = when {
        // Space outside the card
        isThisTaskQuickEditing -> PaddingValues(0.dp, 0.dp, 0.dp, 0.dp)

        taskType == mainType -> PaddingValues(0.dp, 0.dp, 15.dp, 10.dp)
        taskType == basicsType -> PaddingValues(0.dp, 0.dp, 15.dp, 10.dp)
        taskType == helperType -> PaddingValues(0.dp, 0.dp, 15.dp, 10.dp)
        taskType == quickType -> PaddingValues(0.dp, 0.dp, 15.dp, 10.dp)

        else -> PaddingValues(0.dp, 0.dp, 15.dp, 10.dp)
    }

    Box(
        modifier = modifier
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

            // between HourColumn and Main Task Card
            Spacer(modifier = Modifier.width(3.dp))

            Card(
                modifier = modifier
                    .padding(cardPadding)
                    .alpha(if (isAnotherTaskEditing) 0.5f else 1f)
                    .height(cardHeight),
                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                shape = RectangleShape,
                border = cardBorder
            ) {
                Column {
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
                            isShowTaskDetails = isShowTaskDetails,
                            isThisTaskClicked = isThisTaskClicked,
                            onStartQuickEdit = onStartQuickEdit,
                            cardColor = cardBgColor,
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

                            if (isShowTaskDetails.value) {
                                TaskDetailsDialog(task = task,
                                    onDismiss = { isShowTaskDetails.value = false })
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
                onViewClick = { isShowTaskDetails.value = true },
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