package com.app.routineturboa.ui.components

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.app.routineturboa.data.model.TaskEntity
import com.app.routineturboa.reminders.ReminderManager
import com.app.routineturboa.utils.DottedLine
import com.app.routineturboa.utils.TimeUtils.dateTimeToString
import com.app.routineturboa.utils.TimeUtils.strToDateTime
import com.app.routineturboa.viewmodel.TasksViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.PI
import kotlin.math.sin

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun SingleTaskCard(
    context: Context,
    tasksViewModel: TasksViewModel,
    reminderManager: ReminderManager,
    modifier: Modifier = Modifier,
    task: TaskEntity,
    onClick: () -> Unit,
    onEditClick: (TaskEntity) -> Unit,
    canDelete: Boolean,
    onDelete: (TaskEntity) -> Unit,
    isClicked: Boolean,
    taskBeingEdited: TaskEntity?,
    onTaskUpdate: (TaskEntity) -> Unit,
    onCancel: () -> Unit
) {
    val tag = "SingleTaskCard"
    var expanded by remember { mutableStateOf(false) }
    var longPressOffset by remember { mutableStateOf(Offset.Zero) }
    val coroutineScope = rememberCoroutineScope()

    val density = LocalDensity.current
    var showNotesDialog by remember { mutableStateOf(false) }
    val taskType = task.type
    val formattedStartTime = task.startTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
    val currentTime = LocalDateTime.now()
    val isTaskNow = currentTime.isAfter(task.startTime) && currentTime.isBefore(task.endTime)
    val infiniteTransition = rememberInfiniteTransition(label = "BorderAnimation")

    var isFullEditing by remember { mutableStateOf(false) }

    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = SineEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "BorderAlpha"
    )

    val border = when {
        isTaskNow -> BorderStroke(
            width = 3.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = borderAlpha)
        )
        isClicked -> BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
        )
        else -> null
    }


    // Main Box container that contains everything
    Box(
        modifier = modifier
            .fillMaxWidth()
            .alpha(1f)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { offset ->
                        longPressOffset = offset
                        expanded = true
                        onClick()
                        Log.d("SingleTaskCard", "Long Press Offset: $longPressOffset")

                    },
                    onTap = {
                        onClick()
                    }
                )
            }
    ) {
        // Dotted Line at top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = 0.dp, y = 2.dp)
                .align(Alignment.TopStart)
        ) {
            DottedLine(
                color = if (isClicked)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                else
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                thickness = if (isClicked) 2.dp else 0.5.dp
            )
        }

        // Main TaskCard and Start-End times
        Row {
            // Start-End times
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .height(if (taskType == "QuickTask") 50.dp else 110.dp)
                    .width(75.dp)
                    .padding(
                        start = 10.dp,
                        end = 1.dp,
                        top = 5.dp,
                    )
            ) {
                Text (
                    text = formattedStartTime,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 13.sp,
                    ),
                )
                Text (
                    text = ""
                )
            }

            // Main Task Card With Details
            val taskHeight: Dp = when {
                taskBeingEdited != null && taskBeingEdited.id == task.id -> 220.dp
                task.type == "QuickTask" -> 50.dp
                task.type == "MainTask" -> 110.dp
                else -> 110.dp // Default height, in case there are other task types
            }

            Card(
                modifier = modifier
                    .height(taskHeight)
                    .fillMaxWidth()
                    .padding(
                        start = if (taskType == "QuickTask") 0.dp else 4.dp,
                        end = 15.dp, top = 5.dp, bottom = 5.dp
                    )
                    .graphicsLayer {
                        clip = true
                        shadowElevation = if (isClicked) 10f else 1f // Increased shadow elevation
                        shape = RoundedCornerShape(15.dp)
                        spotShadowColor =
                            Color.Blue // Changed shadow color to black for more prominence
                        ambientShadowColor =
                            Color.Yellow // Ambient shadow color can be adjusted as well
                    },
                colors = CardDefaults.cardColors(),
                shape = RoundedCornerShape(15.dp),
                border = border
            ) {

                // Main content of the card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            if (taskBeingEdited?.id == task.id) 2.dp else 15.dp,
                            if (taskBeingEdited?.id == task.id) 2.dp else 10.dp,
                            0.dp, 0.dp
                        )
                ) {
                    // In-place editing of Task Name and Duration
                    if (taskBeingEdited?.id == task.id) {
                        val startTime by remember { mutableStateOf(task.startTime) }
                        var editedTaskName by remember { mutableStateOf(task.taskName) }
                        var editedDurationString by remember { mutableStateOf(task.duration.toString()) }
                        var endTimeString by remember { mutableStateOf(dateTimeToString(task.endTime)) }

                        val taskBelow by remember { mutableStateOf(tasksViewModel.getTaskBelow(task)) }

                        LaunchedEffect(editedDurationString) {
                            if (editedDurationString.isNotEmpty()) {
                                try {
//                                    Log.d("DurationCheck", "Entered duration: $editedDurationString minutes")
//                                    Log.d("DurationCheck", "Task below duration: ${taskBelow?.duration} minutes")

                                    if (taskBelow?.duration != null) {
                                        val maxValidDuration = editedDurationString.toInt() + taskBelow?.duration!! - 1

                                        if (editedDurationString.toInt() >= maxValidDuration) {
//                                            val message = "Invalid duration. Must be less than $maxValidDuration minutes."
                                            Toast.makeText(
                                                context, "must be less than $maxValidDuration minutes.",
                                                Toast.LENGTH_SHORT).show()
//                                            Log.d("DurationCheck", message)
                                        }

                                        if (editedDurationString.toInt() ==0 ){
//                                            val message = "Duration cannot be 0. Please enter a valid number."
                                            Toast.makeText(context, "Duration cannot be 0", Toast.LENGTH_SHORT).show()
//                                            Log.d("DurationCheck", message)
                                        } else {
                                            endTimeString = dateTimeToString(startTime.plusMinutes(editedDurationString.toLong()))
//                                            Log.d("DurationCheck", "End time updated to: $endTimeString")
                                        }
                                    } else {
                                        endTimeString = dateTimeToString(startTime.plusMinutes(editedDurationString.toLong()))
//                                        Log.d("DurationCheck", "No task below. End time updated to: $endTimeString")
                                    }
                                } catch (e: NumberFormatException) {
//                                    val message = "Invalid duration format. Please enter a valid number."
                                    Toast.makeText(context, "Invalid duration format", Toast.LENGTH_SHORT).show()
//                                    Log.d("DurationCheck", message)
                                }
                            }
                        }


                        TextField(
                            value = editedTaskName,
                            onValueChange = { editedTaskName = it },
                            label = { Text("Task Name") }
                        )

                        TextField(
                            value = editedDurationString,
                            onValueChange = { editedDurationString = it },
                            label = { Text("Duration (minutes)") }
                        )

                        TextField(
                            value = endTimeString,
                            onValueChange = { endTimeString = it },
                            label = { Text("End Time") }
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(onClick = { onCancel() }) {
                                Text("Cancel")
                            }

                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Task",
                                modifier = Modifier.clickable{
                                    isFullEditing = true
                                }
                                    .align(Alignment.CenterVertically)
                            )

                            if (isFullEditing) {
                                EditTaskScreen(
                                    reminderManager = reminderManager,
                                    task = taskBeingEdited,
                                    onSave = { updatedTask ->
                                        onCancel()
                                        coroutineScope.launch {
                                            tasksViewModel.updateTask(updatedTask)
                                            reminderManager.observeAndScheduleReminders(context)
                                        }
                                        tasksViewModel.refreshTasks()
                                    },
                                    onCancel = { isFullEditing = false }
                                )
                            }

                            Button(onClick = {
                                    val updatedTask = task.copy(
                                        taskName = editedTaskName,
                                        duration = editedDurationString.toIntOrNull() ?: task.duration,
                                        endTime = strToDateTime(endTimeString)
                                    )
                                    onTaskUpdate(updatedTask)
                                }
                            ) {
                                Text(
                                    text = "Save",
                                    modifier = Modifier.padding(start = 5.dp)
                                )
                            }
                        }

                    } else {
                        // Task Name, Duration, Reminder
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = task.taskName,
                                style = MaterialTheme.typography.titleLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(1f)
                            )

                            // Show notes
                            IconButton(
                                onClick = { showNotesDialog = true },
                                modifier = Modifier
                                    .alpha(if (taskType == "QuickTask") 0f else 0.9f)
                                    .size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Sharp.LibraryBooks,
                                    contentDescription = "Show Notes",
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))

                            IconButton(
                                onClick = { onEditClick(task) },
                                modifier = Modifier
                                    .alpha(if (taskType == "QuickTask") 0f else 0.9f)
                                    .size(18.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Task"
                                )
                            }
                        }

                        // Duration and Reminder
                        if (taskType != "QuickTask") {
                            Spacer(modifier = Modifier.height(8.dp))

                            // Duration
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Timer,
                                    contentDescription = "Duration",
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )

                                Text(
                                    text = buildAnnotatedString {
                                        append("Duration: ")
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(task.duration.toString())
                                        }
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Reminder
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Alarm,
                                    contentDescription = "Reminder",
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = buildAnnotatedString {
                                        append("Remind at: ")
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(task.reminder.format(DateTimeFormatter.ofPattern("hh:mm a")))
                                        }
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }


                }
            }
        }

        if (expanded){
            TaskDropdownMenu(
                onEditClick = {
                    expanded = false
                    onEditClick(task)
                },
                canDelete = canDelete,
                onDeleteClick = {
                    expanded = false
                    if (canDelete) {
                        onDelete(task)
                    }
                },
                onDismissRequest = { expanded = false },
                offset = DpOffset(
                    x = with(density) { longPressOffset.x.toDp() + 10.dp },
                    y = with(density) { longPressOffset.y.toDp() - 112.dp }
                )
            )
        }
    } // End of main parent Box

    // Dialog to show notes
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
}

object SineEasing : Easing {
    override fun transform(fraction: Float): Float {
        return sin(fraction * PI.toFloat() / 2)
    }
}