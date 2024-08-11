package com.app.routineturboa.ui.task.dialogs

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.sharp.AddAlert
import androidx.compose.material.icons.sharp.Start
import androidx.compose.material.icons.sharp.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.app.routineturboa.R
import com.app.routineturboa.data.local.TaskEntity
import com.app.routineturboa.ui.components.CustomTextField
import com.app.routineturboa.ui.components.SelectTaskTypeDropdown
import com.app.routineturboa.ui.theme.LocalCustomColorsPalette
import com.app.routineturboa.utils.TimeUtils.dateTimeToString
import com.app.routineturboa.utils.TimeUtils.strToDateTime
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullEditDialog(
    task: TaskEntity,
    onConfirmTaskEdit: suspend (TaskEntity) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    // Actual data in State variables from 'task'
    var id by remember { mutableIntStateOf(task.id) }
    var taskName by remember { mutableStateOf(task.name) }
    var notes by remember { mutableStateOf(task.notes) }
    var startTime by remember { mutableStateOf(task.startTime) }
    var endTime by remember { mutableStateOf(task.endTime) }
    var duration by remember { mutableIntStateOf(task.duration) }
    var reminder by remember { mutableStateOf(task.reminder) }
    var position by remember { mutableIntStateOf(task.position) }
    var taskType by remember { mutableStateOf(task.type) }

    // Convert state variables to  string for display
    var startTimeString by remember {mutableStateOf(dateTimeToString(startTime))}
    var endTimeString by remember {mutableStateOf(dateTimeToString(endTime))}
    var reminderString by remember {mutableStateOf(dateTimeToString(reminder))}
    var durationString by remember {mutableStateOf(duration.toString())}
    val idFormatted by remember {mutableStateOf(id.toString())}
    var positionString by remember {mutableStateOf(position.toString())}

    val coroutineScope = rememberCoroutineScope()
    var isReminderLinked by remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = {onCancel()},
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f),
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Edit Task", style = MaterialTheme.typography.titleLarge)

                CustomTextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = "Task Name",
                    placeholder = "Enter task name",
                    leadingIcon = Icons.Default.AddTask,
                    modifier = Modifier.fillMaxWidth()
                )

                // Start Time and Reminder Side by Side with Link/Link-off button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CustomTextField(
                        value = startTimeString,
                        onValueChange = { startTimeString = it },
                        label = "Start Time",
                        placeholder = "Enter start time",
                        leadingIcon = Icons.Sharp.Start,
                        modifier = Modifier.weight(1f),
                    )

                    Icon (
                        imageVector = if (isReminderLinked) Icons.Default.Link
                        else Icons.Default.LinkOff,
                        contentDescription = "Link Reminder",
                        modifier = Modifier
                            .clickable {
                                isReminderLinked = !isReminderLinked
                                if (isReminderLinked) {
                                    reminderString = startTimeString
                                }
                            }
                            .align(Alignment.CenterVertically)
                            .size(25.dp)
                    )

                    CustomTextField(
                        value = reminderString,
                        onValueChange = { reminderString = it },
                        label = "Reminder",
                        placeholder = "Enter reminder time",
                        leadingIcon = Icons.Sharp.AddAlert,
                        enabled = !isReminderLinked,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Duration and End Time Side by Side
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LaunchedEffect (durationString){
                        if (durationString.isNotEmpty()) {
                            try {
                                endTimeString = dateTimeToString(startTime.plusMinutes(durationString.toLong()))
                            } catch (e: NumberFormatException) {
                                Toast.makeText(context, "Invalid duration format", Toast.LENGTH_SHORT).show()
                            }
                        }

                    }

                    CustomTextField(
                        value = durationString,
                        onValueChange = { durationString = it },
                        label = "Duration",
                        placeholder = "Enter duration",
                        leadingIcon = Icons.Sharp.Timer,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                    )

                    CustomTextField(
                        value = endTimeString,
                        onValueChange = { endTimeString = it },
                        label = "End Time",
                        placeholder = "Enter End Time",
                        leadingIconResId = R.drawable.arrowrighttoleft,
                        modifier = Modifier.weight(1f),
                    )
                }

                CustomTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "Notes",
                    placeholder = "Add Notes",
                    leadingIcon = Icons.Default.Link,
                    modifier = Modifier
                        .fillMaxWidth(),
                    singleLine = false
                )

                SelectTaskTypeDropdown(taskType, onTaskTypeSelected = { newType -> taskType = newType })

                CustomTextField(
                    value = idFormatted,
                    onValueChange = { },
                    label = "ID (Only for dev)",
                    placeholder = "Internal purposes",
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                )

                CustomTextField(
                    value = positionString,
                    onValueChange = {positionString = it},
                    label = "Position (Don't Change) (Only for dev)",
                    placeholder = "Internal purposes. Don't change.",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Cancel Button
                    Button(
                        modifier = Modifier
                            .padding(5.dp)
                            .size(120.dp, 50.dp),
                        shape = RoundedCornerShape(25.dp),

                        onClick = { onCancel() },
                        colors = ButtonDefaults.buttonColors(
                            LocalCustomColorsPalette.current.gray200,
                        )

                    ) {
                        Text("Cancel")
                    }

                    // Save Button
                    FloatingActionButton(
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(5.dp)
                            .size(120.dp, 50.dp),
                        elevation = FloatingActionButtonDefaults.elevation(10.dp),
                        onClick = {
                            Log.d("EditTaskScreen", "Save Button clicked...")
                            coroutineScope.launch {
                                try {
                                    startTime = strToDateTime(startTimeString)
                                    endTime = strToDateTime(endTimeString)
                                    reminder = strToDateTime(reminderString)
                                    duration = durationString.toInt()
                                    id = idFormatted.toInt()
                                    position = positionString.toInt()

                                } catch (e: Exception) {
                                    Log.e("EditTaskScreen", "Error: $e")
                                    Toast.makeText(context, "Error saving task: ${e.message}", Toast.LENGTH_LONG).show()
                                }

                                val updatedTask = task.copy(
                                    name = taskName,
                                    position = position,
                                    notes = notes,
                                    startTime = startTime,
                                    endTime = endTime,
                                    reminder = reminder,
                                    duration = duration,
                                    type = taskType
                                )

                                onConfirmTaskEdit(updatedTask)

                            } // End of Coroutine
                        }
                    ) {
                        Text("Save")
                    }
                }

            }
        }
    }
}
