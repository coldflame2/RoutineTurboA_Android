package com.app.routineturboa.ui.task

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.LibraryBooks
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.routineturboa.data.local.TaskEntity
import com.app.routineturboa.reminders.ReminderManager
import com.app.routineturboa.ui.components.DottedLine
import com.app.routineturboa.ui.task.dialogs.TaskDetailsDialog
import com.app.routineturboa.utils.SineEasing
import com.app.routineturboa.viewmodel.TasksViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun TaskCard(
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
    // <editor-fold desc="variables">
    var expanded by remember { mutableStateOf(false) }
    var longPressOffset by remember { mutableStateOf(Offset.Zero) }

    val density = LocalDensity.current
    var showNotesDialog by remember { mutableStateOf(false) }
    val taskType = task.type

    val formattedStartTime = task.startTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
    val startTimeHourString = formattedStartTime.substring(0, 2)
    val startTimeMinuteString = formattedStartTime.substring(3, 5)
    val startTimeAinAm = formattedStartTime.substring(6, 7)
    val startTimeMinAm = formattedStartTime.substring(7, 8)

    val currentTime = LocalDateTime.now()
    val isTaskNow = currentTime.isAfter(task.startTime) && currentTime.isBefore(task.endTime)

    var isShowTaskDetails by remember { mutableStateOf(false) }

    val taskHeight: Dp = when {
        isThisTaskQuickEditing -> 80.dp // if in-edit mode
        task.type == "QuickTask" -> 45.dp
        task.type == "MainTask" -> 40.dp
        task.type == "HelperTask" -> 40.dp
        else -> 40.dp // Default height, in case there are other task types
    }

    val infiniteTransition = rememberInfiniteTransition(label = "BorderAnimation")
    val borderAlpha by infiniteTransition.animateFloat(
        // <editor-fold desc="borderAlpha for current Task">
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = SineEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "BorderAlpha"
        // </editor-fold>
    )

    val backgroundColor = if (isAnotherTaskEditing) {
        MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha=0.3f)
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha=1f)
    }

    val dottedLineColor = when {
        isThisTaskClicked -> MaterialTheme.colorScheme.primary.copy(alpha = 1f)
        else -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
    }

    val dottedLineThickness = when {
        isThisTaskClicked -> 2.dp
        else -> 1.dp
    }

    val cardBorder = when {
        isTaskNow -> BorderStroke(
            width = 3.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = borderAlpha)
        )

        isThisTaskQuickEditing -> null

        isThisTaskClicked -> BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
        )

        else -> null
    }

    // </editor-fold>

    // <editor-fold desc="Main Box container that contains everything except 'show notes' dialog">
    Box(
        // <editor-fold desc="Main Box Modifier for clicking behavior">
        modifier = modifier
            .alpha(if (isAnotherTaskEditing) 0.5f else 1f)
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { offset ->
                        longPressOffset = offset
                        expanded = true
                        onTaskClick()
                        Log.d("SingleTaskCard", "Long Press Offset: $longPressOffset")
                    },
                    onTap = {
                        onTaskClick()
                    }
                )
            }
        // </editor-fold>
    )
    {
        // <editor-fold desc="Dotted Line at top">
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = 0.dp, y = 2.dp)
                .align(Alignment.TopStart)
        ) {
            if (!isThisTaskQuickEditing) {
                DottedLine(
                    color = dottedLineColor,
                    thickness = dottedLineThickness,
                    dotLength = 3.dp,
                    dotSpacing = 1.dp
                )
            }
        }
        // </editor-fold "dotted Line">

        Row {
            // StartTime Hour:Minute Column
            Row (
            // <editor-fold desc="StartTime Hour:Minute Column">
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(taskHeight)
                    .width(40.dp)
                    .padding(
                        start = 10.dp,
                        end = 1.dp,
                        top = 5.dp,
                    )
            // </editor-fold "StartTime Hour:Minute Column">
            ) {
                // Hour and Minute Column
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy((-8).dp),
                ) {
                    Text(
                        text = startTimeHourString,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp),
                    )

                    Text(
                        text = startTimeMinuteString,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp),
                    )
                }

                // A/P and M Column
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy((-10).dp), // Adjust to control space between A/P and M
                ) {
                    Text(
                        text = startTimeAinAm, // A or P
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary),
                    )
                    Text(
                        text = startTimeMinAm, // M
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary),
                    )
                }
            }
            // End of Column (Hour Column on Left)

            Spacer(modifier = Modifier.width(5.dp))

            Card(
                // <editor-fold desc = "Card (for Main Task)"
                modifier = modifier
                    .alpha(if (isAnotherTaskEditing) 0.8f else 1f)
                    .height(taskHeight)
                    .fillMaxWidth()
                    .padding(
                        start = if (taskType == "QuickTask") 0.dp else 4.dp,
                        end = 10.dp, top = 1.dp, bottom = 0.dp
                    )
                    .graphicsLayer {
                        clip = true
                        shadowElevation = if (isThisTaskClicked) 0f else 0f // Increased shadow elevation
                        shape = RoundedCornerShape(0.dp)
                        spotShadowColor =
                            Color.Blue // Changed shadow color to black for more prominence
                        ambientShadowColor =
                            Color.Yellow // Ambient shadow color can be adjusted as well
                    },
                colors = CardDefaults.cardColors(
                    containerColor = backgroundColor
                ),
                shape = RoundedCornerShape(0.dp),
                border = cardBorder
                // </editor-fold>
            ) {
                // Layout for Main content of the card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            if (isThisTaskQuickEditing) 2.dp else 15.dp,
                            if (isThisTaskQuickEditing) 2.dp else 4.dp,
                            8.dp,
                            0.dp,
                        )
                        .height(taskHeight)
                ) {
                    // If editing (in-place)
                    if (isThisTaskQuickEditing) {
                        QuickEdit(
                            task = task,
                            onEndEditing = { onEndEditing() },
                            tasksViewModel = tasksViewModel,
                            reminderManager = reminderManager,
                        )
                    }

                    else {
                        // <editor-fold desc="Main Interface (not in edit mode)"

                        // Task Name, Show Notes and Edit Icon
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Task Name
                            Text(
                                // <editor-fold desc="Task Name "
                                text = task.name,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(1f)
                                // </editor-fold>
                            )

                            // Show notes
                            IconButton(
                                // <editor-fold desc="Show Notes"
                                onClick = { showNotesDialog = true },
                                modifier = Modifier
                                    .alpha(if (taskType == "QuickTask") 0f else 0.5f)
                                    .size(25.dp)
                                    .padding(end = 10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Sharp.LibraryBooks,
                                    contentDescription = "Show Notes",
                                    tint = if (isThisTaskClicked) MaterialTheme.colorScheme.secondary.copy(alpha = 1f)
                                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                )
                            }
                            // </editor-fold>

                            Spacer(modifier = Modifier.width(15.dp))

                            // Quick-Edit Button
                            IconButton(
                                // <editor-fold desc="Quick-Edit Icon "
                                onClick = {
                                    onStartQuickEdit()
                                },
                                modifier = Modifier
                                    .alpha(if (taskType == "QuickTask") 0.5f else 0.7f)
                                    .size(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Task",
                                    tint = if (isThisTaskClicked) MaterialTheme.colorScheme.surfaceTint.copy(alpha = 1f)
                                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                )
                            }
                            // </editor-fold>

                            // </editor-fold>
                        }

                        // start, end, duration all combined
                        if (taskType != "QuickTask") {
                            Spacer(modifier = Modifier.height(8.dp))

                            // Layout for start, end, duration all combined
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = "End Time",
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = buildAnnotatedString {
                                        append("")
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(task.startTime.format(DateTimeFormatter.ofPattern("hh:mm a")))
                                        }
                                        append(" to ")
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(task.endTime.format(DateTimeFormatter.ofPattern("hh:mm a")))
                                        }
                                        append(" | ")
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(task.duration.toString())
                                        }
                                        append(" mins ")
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.3f),
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }

                            // if to show Task Details
                            if (isShowTaskDetails) {
                                TaskDetailsDialog(task = task,
                                    onDismiss = { isShowTaskDetails = false })
                            }
                        }

                        // </editor-fold>
                    }
                }
            } // End of Main Task-Card Details
        }

        // </editor-fold>

        // <editor-fold desc="Dropdown Menu">
        if (expanded){
            TaskDropdownMenu(
                onEditClick = {
                    expanded = false
                    onStartFullEdit()
                },
                canDelete = canDelete,
                onDeleteClick = {
                    expanded = false
                    if (canDelete) {
                        onDelete(task)
                    }
                },
                onViewClick = { isShowTaskDetails = true },
                onDismissRequest = { expanded = false },
                offset = DpOffset(
                    x = with(density) { longPressOffset.x.toDp() + 10.dp },
                    y = with(density) { longPressOffset.y.toDp() - 112.dp }
                )
            )
        }
        // </editor-fold>

    } // End of main parent Box
    // </editor-fold>

    // <editor-fold desc="Dialog to show notes"
    if (showNotesDialog) {
        AlertDialog(
            onDismissRequest = { showNotesDialog = false },
            title = { Text(text = "Task Notes") },
            text = { Text(text = task.notes) },
            confirmButton = {
                Button(onClick = { showNotesDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
    // </editor-fold>
}