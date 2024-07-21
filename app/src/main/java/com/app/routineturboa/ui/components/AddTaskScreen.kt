package com.app.routineturboa.ui.components

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
import androidx.compose.material3.TextFieldDefaults
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
import com.app.routineturboa.data.model.TaskEntity
import com.app.routineturboa.utils.TimeUtils.dateTimeToString
import com.app.routineturboa.utils.TimeUtils.strToDateTime
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    clickedTask: TaskEntity?,
    onSaveNewTask: (TaskEntity) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    val clickedTaskEndTime = clickedTask?.endTime
    val clickedTaskPosition = clickedTask?.position

    // Actual data in State variables
    var taskName by remember { mutableStateOf(" ") }
    var startTime by remember { mutableStateOf(clickedTaskEndTime ?: LocalDateTime.now()) }
    var duration by remember { mutableLongStateOf(1L) }
    var endTime by remember { mutableStateOf(
        clickedTaskEndTime?.plusMinutes(duration) ?: LocalDateTime.now().plusMinutes(1)) }
    var reminder by remember { mutableStateOf(startTime) }  // equal to startTime by default

    var position by remember { mutableIntStateOf(clickedTaskPosition?.plus(1) ?: 1) } // Add 1 to clickedTaskPosition if it's not null

    // Convert state variables to  string for display
    var startTimeFormatted by remember {mutableStateOf(dateTimeToString(startTime))}
    var endTimeFormatted by remember {mutableStateOf(dateTimeToString(endTime))}
    var reminderFormatted by remember {mutableStateOf(dateTimeToString(reminder))}
    var durationFormatted by remember {mutableStateOf(duration.toString())}
    var positionFormatted by remember {mutableStateOf(position.toString())}

    // Calculate endTime based on duration input
    LaunchedEffect(durationFormatted) {
        if (durationFormatted.isNotEmpty()) {
            try {
                val durationLong = durationFormatted.toLong()
                if (durationLong > 0) {
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
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface
                )
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
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface
                )
            )

            TextField(
                value = endTimeFormatted,
                onValueChange = { endTimeFormatted = it },
                label = { Text("End Time") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            )

            TextField(
                value = reminderFormatted,
                onValueChange = { reminderFormatted = it },
                label = { Text("Reminder") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            )

            TextField(
                value = positionFormatted,
                onValueChange = {positionFormatted.plus(1)},
                label = { Text("Position") },
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
                        position = positionFormatted.toInt()

                        val newTask = TaskEntity(
                            id = 0, // ID will be auto-generated by the database
                            taskName = taskName,
                            startTime = startTime,
                            endTime = endTime,
                            duration = duration.toInt(),
                            reminder = reminder,
                            type = "MainTask",
                            position = position
                        )
                        onSaveNewTask(newTask)
                        Toast.makeText(context, "Task Added.", Toast.LENGTH_SHORT).show()

                        // Update positions of tasks below


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