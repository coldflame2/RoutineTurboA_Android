package com.app.routineturboa.ui.task.child_elements

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.app.routineturboa.data.local.TaskEntity
import com.app.routineturboa.reminders.ReminderManager
import com.app.routineturboa.ui.task.dialogs.FullEditDialog
import com.app.routineturboa.utils.TimeUtils.dateTimeToString
import com.app.routineturboa.utils.TimeUtils.strToDateTime
import com.app.routineturboa.viewmodel.TasksViewModel

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun QuickEdit(
    task: TaskEntity,
    onEndEditing: () -> Unit,
    tasksViewModel: TasksViewModel,
    reminderManager: ReminderManager,
) {
    val tag = "QuickEditScreen"
    val context = LocalContext.current

    val isFullEditing = remember { mutableStateOf(false) }

    val startTime by remember { mutableStateOf(task.startTime) }
    var taskName by remember { mutableStateOf(task.name) }
    var durationString by remember { mutableStateOf(task.duration.toString()) }
    var endTime by remember { mutableStateOf(task.endTime) }
    var endTimeString by remember { mutableStateOf(dateTimeToString(endTime)) }

    LaunchedEffect(durationString) {
        if (durationString.isNotEmpty()) {
            try {
                endTimeString =
                    dateTimeToString(task.startTime.plusMinutes(durationString.toLong()))
            } catch (e: NumberFormatException) {
                Toast.makeText(context, "Invalid duration format", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(modifier = Modifier.padding(5.dp)) {
        // Task Name and Duration Row
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Box (
                modifier = Modifier
                    .weight(2f)
                    .height(45.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f),
                        MaterialTheme.shapes.small
                    ),
            ) {
                BasicTextField(
                    value = taskName,
                    onValueChange = { newTaskName -> taskName = newTaskName },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    decorationBox = { innerTextField ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(start=10.dp)
                                .fillMaxHeight() // This makes sure the content is centered vertically
                        ) {
                            if (taskName.isEmpty()) {
                                Text(
                                    "task name..."
                                )
                            }
                            innerTextField()
                        }

                    }
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(45.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f),
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
                                .padding(start=10.dp)
                                .fillMaxHeight() // This makes sure the content is centered vertically
                        ) {
                            if (durationString.isEmpty()) {
                                Text("Duration...")
                            }
                            innerTextField()
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Save and Cancel Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth()
                .height(43.dp), // Set the height for the entire row

            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Edit Button
            Button(
                onClick = { isFullEditing.value = true },
                modifier = Modifier.fillMaxHeight(),
                shape = RoundedCornerShape(15.dp),
                contentPadding = PaddingValues(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.inversePrimary.copy(alpha =0.2f),
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Full-screen Editing",
                    style = MaterialTheme.typography.labelLarge)

            }

            // Save Button
            Button(
                onClick = {
                    try {
                        endTimeString = dateTimeToString(startTime.plusMinutes(durationString.toLong()))
                        endTime = strToDateTime(endTimeString)

                        val taskWithUpdatedData =
                            task.copy(name = taskName, duration = durationString.toInt(), endTime = endTime)
                        tasksViewModel.updateTaskAndAdjustNext(taskWithUpdatedData)
                        Toast.makeText(
                            context,
                            "Task '${task.name}' updated...",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Log.e(tag, "Error: $e")
                        Toast.makeText(
                            context,
                            "Error saving task values: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    onEndEditing()
                },
                modifier = Modifier.fillMaxHeight(),
                shape = RoundedCornerShape(15.dp),
                contentPadding = PaddingValues(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.2f),
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Save",
                    style = MaterialTheme.typography.labelLarge)
            }
        }

        if (isFullEditing.value) {
            FullEditDialog(
                task = task,
                onConfirmTaskEdit = { updatedTask ->
                    isFullEditing.value = false
                    tasksViewModel.updateTaskAndAdjustNext(updatedTask)
                    onEndEditing()
                },
                onCancel = {
                    isFullEditing.value = false
                }
            )
        }
    }
}

