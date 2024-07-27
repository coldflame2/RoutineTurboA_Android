package com.app.routineturboa.ui.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.app.routineturboa.data.model.TaskEntity
import com.app.routineturboa.utils.TimeUtils.dateTimeToString
import com.app.routineturboa.utils.TimeUtils.strToDateTime
import com.app.routineturboa.viewmodel.TasksViewModel
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    tasksViewModel: TasksViewModel,
    clickedTask: TaskEntity?,
    onSave: (TaskEntity) -> Unit,
    onCancel: () -> Unit
) {
    val tag = "AddTaskScreen"
    Log.d(tag, "AddTaskScreen: clickedTask: $clickedTask")

    val context = LocalContext.current
    val clickedTaskEndTime = clickedTask?.endTime
    val clickedTaskID = clickedTask?.id
    val clickedTaskPosition = clickedTask?.position

    val taskBelowBeforeAdding = tasksViewModel.getTaskBelow(clickedTask!!)
    val durationTaskBelowBeforeAdding = taskBelowBeforeAdding?.duration

    // Data in State variables
    val id by remember { mutableIntStateOf(clickedTaskID?.plus(1) ?: 1) }
    var taskName by remember { mutableStateOf(" ") }
    var taskNotes by remember { mutableStateOf( " ") }
    var startTime by remember { mutableStateOf(clickedTaskEndTime ?: LocalDateTime.now()) }
    var duration by remember { mutableLongStateOf(1L) }
    var endTime by remember { mutableStateOf(
        clickedTaskEndTime?.plusMinutes(duration) ?: LocalDateTime.now().plusMinutes(1)) }
    var reminder by remember { mutableStateOf(startTime) }
    var taskType by remember { mutableStateOf("") }
    var taskPosition by remember { mutableIntStateOf(clickedTaskPosition?.plus(1)?: 2) }

    // Convert state variables to  string for display
    var startTimeFormatted by remember {mutableStateOf(dateTimeToString(startTime))}
    var endTimeFormatted by remember {mutableStateOf(dateTimeToString(endTime))}
    var reminderFormatted by remember {mutableStateOf(dateTimeToString(reminder))}
    var durationFormatted by remember {mutableStateOf(duration.toString())}
    val idFormatted by remember {mutableStateOf(id.toString())}
    val taskPositionFormatted by remember {mutableStateOf(taskPosition.toString())}

    // Calculate endTime based on duration input
    LaunchedEffect(durationFormatted) {
        if (durationFormatted.isNotEmpty()) {
            try {
                val durationLong = durationFormatted.toLong()
                if (durationLong > 0 || durationLong < durationTaskBelowBeforeAdding!!) {
                    endTimeFormatted = dateTimeToString(startTime.plusMinutes(durationLong))
                } else {
                    Toast.makeText(context, "Invalid duration. End time unchanged.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(context, "Invalid duration format. End time unchanged.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Duration is empty. End time unchanged.", Toast.LENGTH_SHORT).show()
        }
    }

    Dialog(onDismissRequest = { onCancel() }) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("EDIT TASK", style = MaterialTheme.typography.titleLarge)

                TextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = { Text("Task Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = taskNotes,
                    onValueChange = { taskNotes = it },
                    label = { Text("Task Notes") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = taskType,
                    onValueChange = { taskType = it },
                    label = { Text("Task Type") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = startTimeFormatted,
                    onValueChange = { startTimeFormatted = it },
                    label = { Text("Start Time") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = durationFormatted,
                    onValueChange = { durationFormatted = it },
                    label = { Text("Duration (minutes)") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = endTimeFormatted,
                    onValueChange = { endTimeFormatted = it },
                    label = { Text("End Time") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = reminderFormatted,
                    onValueChange = { reminderFormatted = it },
                    label = { Text("Reminder") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = idFormatted,
                    onValueChange = { },
                    label = { Text("Task ID") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                )

                TextField(
                    value = taskPositionFormatted,
                    onValueChange = { },
                    label = { Text("Task Position") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = { onCancel() }) {
                        Text("Cancel")
                    }
                    Button(onClick = {
                        if (taskName.isNotBlank() && duration > 0) {
                            startTime = strToDateTime(startTimeFormatted)
                            endTime = strToDateTime(endTimeFormatted)
                            reminder = strToDateTime(reminderFormatted)
                            duration = durationFormatted.toLong()
                            taskPosition = taskPositionFormatted.toInt()

                            val newTask = TaskEntity(
                                position = taskPosition,
                                taskName = taskName,
                                notes = taskNotes,
                                startTime = startTime,
                                endTime = endTime,
                                duration = duration.toInt(),
                                reminder = reminder,
                                type = taskType,
                            )
                            onSave(newTask)

                        } else {
                            Toast.makeText(context, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}