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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.app.routineturboa.data.model.TaskEntity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    initialStartTime: LocalDateTime,
    onSave: (TaskEntity) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")

    var taskName by remember { mutableStateOf("") }
    var duration by remember { mutableLongStateOf(0L) }
    var endTime by remember { mutableStateOf(initialStartTime) }

    LaunchedEffect(duration) {
        endTime = if (duration > 0) {
            initialStartTime.plusMinutes(duration)
        } else {
            Toast.makeText(context, "Invalid duration. End time unchanged.", Toast.LENGTH_SHORT).show()
            initialStartTime
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
                value = initialStartTime.format(dateTimeFormatter),
                onValueChange = { },
                label = { Text("Start Time") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                colors = TextFieldDefaults.colors(
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            )

            TextField(
                value = duration.toString(),
                onValueChange = {
                    duration = it.toLongOrNull() ?: 0L
                },
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
                value = endTime.format(dateTimeFormatter),
                onValueChange = { },
                label = { Text("End Time") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                colors = TextFieldDefaults.colors(
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
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
                        val newTask = TaskEntity(
                            id = 0, // ID will be auto-generated by the database
                            taskName = taskName,
                            startTime = initialStartTime,
                            endTime = endTime,
                            duration = duration.toInt(),
                            reminder = initialStartTime, // You can add a field for reminders if necessary
                            type = "", // You can add a field for type if necessary
                            position = 0 // Position will be set in MainScreen
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