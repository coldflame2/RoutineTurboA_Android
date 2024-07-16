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
import com.app.routineturboa.data.model.TaskEntity
import com.app.routineturboa.reminders.ReminderManager
import com.app.routineturboa.utils.TimeUtils.dateTimeToString
import com.app.routineturboa.utils.TimeUtils.strToDateTime
import kotlinx.coroutines.launch

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
    var taskName by remember { mutableStateOf(task.taskName) }
    var startTime by remember { mutableStateOf(task.startTime) }
    var endTime by remember { mutableStateOf(task.endTime) }
    var duration by remember { mutableIntStateOf(task.duration) }
    var reminder by remember { mutableStateOf(task.reminder) }

    // Convert state variables to  string for display
    var startTimeFormatted by remember {mutableStateOf(dateTimeToString(startTime))}
    var endTimeFormatted by remember {mutableStateOf(dateTimeToString(endTime))}
    var reminderFormatted by remember {mutableStateOf(dateTimeToString(reminder))}
    var durationFormatted by remember {mutableStateOf(duration.toString())}

    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {


        TextField(
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("Task Name") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = startTimeFormatted,
            onValueChange = {startTimeFormatted = it},
            label = { Text("Start Time") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = endTimeFormatted,
            onValueChange = {endTimeFormatted = it},
            label = { Text("End Time") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = durationFormatted,
            onValueChange = {durationFormatted = it},
            label = { Text("Duration (minutes)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = reminderFormatted,
            onValueChange = {reminderFormatted = it},
            label = { Text("Reminder Time") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = { onCancel() }) {
                Text("Cancel")
            }

            Button(onClick = {
                coroutineScope.launch {
                    try {
                        startTime = strToDateTime(startTimeFormatted)
                        endTime = strToDateTime(endTimeFormatted)
                        reminder = strToDateTime(reminderFormatted)
                        duration = durationFormatted.toInt()

                    } catch (e: Exception) {
                        Log.e("EditTaskScreen", "Error: $e")
                    }

                    val updatedTask = task.copy(
                        taskName = task.taskName,
                        startTime = startTime,
                        endTime = endTime,
                        reminder = reminder,
                        duration = duration
                    )
                    onSave(updatedTask)
                    reminderManager.observeAndScheduleReminders(context)
                    Toast.makeText(context, "Task Edited.", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Save")
            }
        }

    }
}
