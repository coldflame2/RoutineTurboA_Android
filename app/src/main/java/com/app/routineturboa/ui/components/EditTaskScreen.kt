package com.app.routineturboa.ui.components

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.app.routineturboa.data.model.TaskEntity
import com.app.routineturboa.reminders.ReminderManager
import com.app.routineturboa.utils.TimeUtils.dateTimeToString
import com.app.routineturboa.utils.TimeUtils.strToDateTime
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    reminderManager: ReminderManager,
    task: TaskEntity,
    onSave: suspend (TaskEntity) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    // Actual data in State variables from 'task'
    var id by remember { mutableIntStateOf(task.id) }
    var taskName by remember { mutableStateOf(task.taskName) }
    var notes by remember { mutableStateOf(task.notes) }
    var startTime by remember { mutableStateOf(task.startTime) }
    var endTime by remember { mutableStateOf(task.endTime) }
    var duration by remember { mutableIntStateOf(task.duration) }
    var reminder by remember { mutableStateOf(task.reminder) }
    var position by remember { mutableIntStateOf(task.position) }

    // Convert state variables to  string for display
    var startTimeFormatted by remember {mutableStateOf(dateTimeToString(startTime))}
    var endTimeFormatted by remember {mutableStateOf(dateTimeToString(endTime))}
    var reminderFormatted by remember {mutableStateOf(dateTimeToString(reminder))}
    var durationFormatted by remember {mutableStateOf(duration.toString())}
    var idFormatted by remember {mutableStateOf(id.toString())}
    var positionFormatted by remember {mutableStateOf(position.toString())}

    val coroutineScope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = {onCancel()},
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f),
            color = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {

                TextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = { Text("Task Name") },
                    placeholder = { Text("Enter task name") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(imageVector = Icons.Default.AddTask, contentDescription = "Add New Task") },
                    trailingIcon = { Icon(imageVector = Icons.Default.Task, contentDescription = "Add New Task") },
                )

                TextField(
                    value = notes,
                    minLines = 3,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth().verticalScroll(ScrollState(1))
                )

                Spacer(modifier = Modifier.padding(1.dp))

                TextField(
                    value = startTimeFormatted,
                    onValueChange = {startTimeFormatted = it},
                    label = { Text("Start Time") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.padding(1.dp))

                TextField(
                    value = endTimeFormatted,
                    onValueChange = {endTimeFormatted = it},
                    label = { Text("End Time") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.padding(1.dp))

                TextField(
                    value = durationFormatted,
                    onValueChange = {durationFormatted = it},
                    label = { Text("Duration (minutes)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.padding(1.dp))

                TextField(
                    value = reminderFormatted,
                    onValueChange = {reminderFormatted = it},
                    label = { Text("Reminder Time") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.padding(1.dp))

                TextField(
                    value = idFormatted,
                    onValueChange = {idFormatted = it},
                    label = { Text("Task ID") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )

                TextField(
                    value = positionFormatted,
                    onValueChange = {positionFormatted = it},
                    label = { Text("Task Position") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Cancel Button
                    Button(
                        modifier = Modifier.padding(10.dp),
                        shape = RoundedCornerShape(25.dp),
                        onClick = { onCancel() }) {
                        Text("Cancel")
                    }

                    // Save Button
                    Button(
                        modifier = Modifier.padding(10.dp),
                        shape = RoundedCornerShape(25.dp),
                        onClick = {
                            Log.d("EditTaskScreen", "Save Button clicked...")
                            coroutineScope.launch {
                                try {
                                    startTime = strToDateTime(startTimeFormatted)
                                    endTime = strToDateTime(endTimeFormatted)
                                    reminder = strToDateTime(reminderFormatted)
                                    duration = durationFormatted.toInt()
                                    id = idFormatted.toInt()
                                    position = positionFormatted.toInt()

                                } catch (e: Exception) {
                                    Log.e("EditTaskScreen", "Error: $e")
                                    Toast.makeText(context, "Error saving task: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                                val updatedTask = task.copy(
                                    taskName = taskName,
                                    position = position,
                                    notes = notes,
                                    startTime = startTime,
                                    endTime = endTime,
                                    reminder = reminder,
                                    duration = duration
                                )
                                onSave(updatedTask)
                                reminderManager.observeAndScheduleReminders(context)
                            }
                        }
                    ) {
                        Text("Save")
                    }
                }

            }
        }
    }
}
