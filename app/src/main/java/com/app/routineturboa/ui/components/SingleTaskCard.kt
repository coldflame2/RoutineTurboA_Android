package com.app.routineturboa.ui.components

import android.content.Context
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
import com.app.routineturboa.data.model.TaskEntity
import com.app.routineturboa.reminders.ReminderManager
import com.app.routineturboa.utils.DottedLine
import com.app.routineturboa.utils.SineEasing
import com.app.routineturboa.viewmodel.TasksViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun SingleTaskCard(
    context: Context,
    tasksViewModel: TasksViewModel,
    reminderManager: ReminderManager,
    modifier: Modifier = Modifier,
    task: TaskEntity,
    onClick: () -> Unit,
    canDelete: Boolean,
    onDelete: (TaskEntity) -> Unit,
    isClicked: Boolean
) {
    // <editor-fold desc="variables">
    val tag = "SingleTaskCard"


    var expanded by remember { mutableStateOf(false) }
    var longPressOffset by remember { mutableStateOf(Offset.Zero) }
    val forceRecompose = remember { mutableStateOf(false) }

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
    val infiniteTransition = rememberInfiniteTransition(label = "BorderAnimation")

    val isEditing = remember { mutableStateOf(false) }
    val isFullEditing = remember { mutableStateOf(false) }
    val taskBeingEdited = remember { mutableStateOf<TaskEntity?>(null) }

    val taskHeight: Dp = when {
        isEditing.value && taskBeingEdited.value != null -> 220.dp // if in-edit mode
        task.type == "QuickTask" -> 50.dp
        task.type == "MainTask" -> 85.dp
        else -> 80.dp // Default height, in case there are other task types
    }

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

    val dottedLineColor = when {
        isClicked -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
    }

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

    // </editor-fold>

    // <editor-fold desc="Main Box container that contains everything except 'show notes' dialog">
    Box(
        // <editor-fold desc="modifier.pointerInput (clicking behavior)">
        modifier = modifier
            .fillMaxWidth()
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
    ) // </editor-fold>
    {
        // <editor-fold desc="Dotted Line at top">
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = 0.dp, y = 2.dp)
                .align(Alignment.TopStart)
        ) {
            DottedLine(
                color = dottedLineColor,
                thickness = if (isClicked) 2.dp else 0.5.dp
            )
        }
        // </editor-fold "dotted Line">

        // <editor-fold desc="Row - Main TaskCard and Hour-Column">
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
                    verticalArrangement = Arrangement.spacedBy((-12).dp),
                ) {
                    Text(
                        text = startTimeHourString,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp),
                    )

                    Text(
                        text = ".",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = startTimeMinuteString,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp),
                    )
                }

                // A/P and M Column
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy((-12).dp), // Adjust to control space between A/P and M
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
                    .height(taskHeight)
                    .fillMaxWidth()
                    .padding(
                        start = if (taskType == "QuickTask") 0.dp else 4.dp,
                        end = 10.dp, top = 5.dp, bottom = 5.dp
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
                // </editor-fold>
            ) {
                // Layout for Main content of the card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            if (isEditing.value) 2.dp else 15.dp,
                            if (isEditing.value) 2.dp else 4.dp,
                            8.dp,
                            0.dp,
                        )
                        .height(taskHeight)
                ) {
                    // If editing (in-place)
                    if (isEditing.value) {
                        QuickEditScreen(
                            task = task,
                            tasksViewModel = tasksViewModel,
                            reminderManager = reminderManager,
                            context = context,
                            isEditing = isEditing,
                            onCancel = { isEditing.value = false }
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
                                    tint = if (isClicked) MaterialTheme.colorScheme.secondary.copy(alpha = 1f)
                                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                )
                            }
                            // </editor-fold>

                            Spacer(modifier = Modifier.width(15.dp))

                            // Quick-Edit Button
                            IconButton(
                                // <editor-fold desc="Quick-Edit Icon "
                                onClick = {
                                    isEditing.value = true
                                    taskBeingEdited.value = task
                                },
                                modifier = Modifier
                                    .alpha(if (taskType == "QuickTask") 0f else 0.5f)
                                    .size(16.dp)

                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Task",
                                    tint = if (isClicked) MaterialTheme.colorScheme.surfaceTint.copy(alpha = 1f)
                                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
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
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(start = 4.dp)
                                )
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
                    isFullEditing.value = true
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