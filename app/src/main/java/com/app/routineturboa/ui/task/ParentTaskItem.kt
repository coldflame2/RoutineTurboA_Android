package com.app.routineturboa.ui.task

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.app.routineturboa.data.local.TaskEntity
import com.app.routineturboa.reminders.ReminderManager
import com.app.routineturboa.ui.task.child_elements.HourColumn
import com.app.routineturboa.ui.task.dialogs.TaskDetailsDialog
import com.app.routineturboa.ui.task.child_elements.ExtraTaskDetails
import com.app.routineturboa.ui.task.child_elements.MainTaskDisplay
import com.app.routineturboa.ui.task.child_elements.QuickEdit
import com.app.routineturboa.ui.task.child_elements.TaskDropdownMenu
import com.app.routineturboa.utils.AnimatedAlphaUtils
import com.app.routineturboa.utils.SineEasing
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

    var isDropDownExpanded by remember { mutableStateOf(false) }
    var longPressOffset by remember { mutableStateOf(Offset.Zero) }

    val isShowNotes = remember { mutableStateOf(false) }

    val currentTime = LocalDateTime.now()
    val isTaskNow = currentTime.isAfter(task.startTime) && currentTime.isBefore(task.endTime)

    var isShowTaskDetails by remember { mutableStateOf(false) }

    val cardHeight: Dp = when {
        isThisTaskQuickEditing -> 80.dp // if in-edit mode
        task.type == "QuickTask" -> 45.dp
        task.type == "MainTask" -> (1.dp * task.duration).coerceAtLeast(45.dp)
        task.type == "HelperTask" -> 40.dp
        else -> 40.dp
    }

    val infiniteTransition = rememberInfiniteTransition(label = "BorderAnimation")
    val animatedAlpha = AnimatedAlphaUtils.animatedAlpha(
        transition = infiniteTransition,
        initialValue = 0.2f,
        targetValue = 0.3f,
        duration = 500,
    )

    val cardBgColor = when {
        isThisTaskClicked -> MaterialTheme.colorScheme.primary.copy(alpha=0.5f)
        isTaskNow -> MaterialTheme.colorScheme.secondary.copy(alpha=animatedAlpha)
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
                cardHeight = cardHeight,
                startTime = task.startTime
            )

            Spacer(modifier = Modifier.width(1.dp))

            Card(
                modifier = modifier
                    .alpha(if (isAnotherTaskEditing) 0.8f else 1f)
                    .height(cardHeight)
                    .fillMaxWidth()
                    .padding(
                        start = if (task.type == "QuickTask") 0.dp else 4.dp,
                        end = 5.dp, top = 1.dp, bottom = 0.dp
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = cardBgColor
                ),
                shape = RoundedCornerShape(0.dp),
                border = cardBorder
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            if (isThisTaskQuickEditing) 2.dp else 15.dp,
                            if (isThisTaskQuickEditing) 2.dp else 4.dp,
                            8.dp,
                            0.dp,
                        )
                        .height(cardHeight)
                ) {

                    if (isThisTaskQuickEditing) {
                        QuickEdit(
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