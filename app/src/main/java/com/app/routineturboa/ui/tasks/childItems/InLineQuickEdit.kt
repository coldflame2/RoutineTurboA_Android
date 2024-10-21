package com.app.routineturboa.ui.tasks.childItems

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.app.routineturboa.data.room.TaskEntity
import com.app.routineturboa.ui.models.TaskFormData
import com.app.routineturboa.ui.tasks.dialogs.FullEditDialog
import com.app.routineturboa.utils.Converters.timeToString
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun InLineQuickEdit(
    mainTasks: List<TaskEntity>?,
    task: TaskEntity,
    isFullEditing: Boolean,
    onShowFullEditClick: (Int) -> Unit,
    onUpdateTaskConfirmClick: (Int, TaskFormData) -> Unit,
    onCancelClick: () -> Unit,
) {
    val tag = "QuickEditScreen"
    val context = LocalContext.current

    // State variables to hold the edited values
    val startTime = task.startTime // this doesn't change
    var editedName by remember { mutableStateOf(task.name) }
    var durationString by remember { mutableStateOf(task.duration.toString()) }
    var endTime by remember { mutableStateOf(task.endTime) }
    var endTimeString by remember { mutableStateOf(timeToString(endTime)) }

    // Update endTime when durationString changes
    LaunchedEffect(durationString) {
        if (durationString.isNotEmpty()) {
            try {
                val durationMinutes = durationString.toLong()
                endTime = startTime?.plusMinutes(durationMinutes) ?: LocalTime.now()
                endTimeString = timeToString(endTime)
            } catch (e: NumberFormatException) {
                Toast.makeText(context, "Invalid duration format", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(modifier = Modifier.padding(5.dp)) {
        // region: name and duration input fields
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Task Name input
            Box(
                modifier = Modifier
                    .weight(2f)
                    .height(50.dp)
                    .background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        MaterialTheme.shapes.small
                    ),
            ) {
                BasicTextField(
                    value = editedName,
                    onValueChange = { newName -> editedName = newName },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    decorationBox = { innerTextField ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .fillMaxHeight()
                        ) {
                            if (editedName.isEmpty()) {
                                Text(
                                    text = "Task name...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }

            // Duration input
            Box(
                modifier = Modifier
                    .weight(1.3f)
                    .height(50.dp)
                    .background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        MaterialTheme.shapes.small
                    ),

            ) {
                BasicTextField(
                    value = durationString,
                    onValueChange = { newDuration -> durationString = newDuration },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    decorationBox = { innerTextField ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .fillMaxHeight()
                        ) {
                            if (durationString.isEmpty()) {
                                Text(
                                    text = "Duration...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }

        }

        // endregion

        Spacer(modifier = Modifier.height(28.dp))

        // region: Save and full-edit Buttons Row

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(35.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Full-Screen Edit Button
            Button(
                onClick = { onShowFullEditClick(task.id) },
                modifier = Modifier.fillMaxHeight(),
                shape = RoundedCornerShape(15.dp),
                contentPadding = PaddingValues(5.dp)
            ) {
                Text(
                    text = "Advanced Edit",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            // Save Button
            Button(
                onClick = {
                    try {
                        val durationMinutes = durationString.toInt()
                        val newEndTime = startTime?.plusMinutes(durationMinutes.toLong())

                        val updatedTaskFormData = TaskFormData(
                            name = editedName,
                            startTime = startTime,
                            endTime = newEndTime,
                            notes = task.notes,
                            taskType = task.type,
                            position = task.position,
                            duration = durationMinutes,
                            reminder = task.reminder,
                            mainTaskId = task.mainTaskId
                        )

                        onUpdateTaskConfirmClick(task.id, updatedTaskFormData)

                    } catch (e: Exception) {
                        Log.e(tag, "Error: $e")
                        Toast.makeText(
                            context,
                            "Error saving task values: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                },
                modifier = Modifier.fillMaxHeight(),
                shape = RoundedCornerShape(15.dp),
                contentPadding = PaddingValues(5.dp)
            ) {
                Text(
                    text = "Save",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        // endregion

        if (isFullEditing) {
            FullEditDialog(
                mainTasks = mainTasks ?: emptyList(),
                task = task,
                onConfirmEdit = { taskId, updatedTaskFormData ->
                    onUpdateTaskConfirmClick(taskId, updatedTaskFormData)
                },
                onCancel = onCancelClick
            )
        }
    }
}
