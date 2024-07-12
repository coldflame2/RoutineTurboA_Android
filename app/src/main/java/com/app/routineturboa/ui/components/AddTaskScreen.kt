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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.app.routineturboa.data.model.Task
import com.app.routineturboa.utils.TimeUtils
import java.text.ParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    initialStartTime: String, // Added parameter for initial start time
    onSave: (Task) -> Unit,
    onCancel: () -> Unit
) {
    var taskName by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") } // Added state for end time
    val context = LocalContext.current

    LaunchedEffect(duration) {
        try {
            if (duration.isNotBlank()) {
                endTime = TimeUtils.addDurationToTime(initialStartTime, duration.toInt())
            }
        } catch (e: NumberFormatException) {
            endTime = ""
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        Column(
            modifier = Modifier.padding(5.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = taskName,
                onValueChange = { taskName = it },
                label = { Text("Task Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface
                )
            )
            TextField(
                value = initialStartTime, // Show the start time
                onValueChange = {},
                label = { Text("Start Time") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false, // Make it non-editable
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // Updated for Compose 1.2.0 or later
                )
            )
            TextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Duration (minutes)") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface
                )
            )
            TextField(
                value = endTime, // Show the end time
                onValueChange = {},
                label = { Text("End Time") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false, // Make it non-editable
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // Updated for Compose 1.2.0 or later
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
                    try {
                        val newTask = Task(
                            id = 0, // ID will be auto-generated by the database
                            taskName = taskName,
                            startTime = initialStartTime,
                            endTime = endTime, // Set the end time
                            duration = duration.toInt(),
                            reminder = "", // You can add a field for reminders if necessary
                            type = "", // You can add a field for type if necessary
                            position = 0 // Position will be set in MainScreen
                        )
                        onSave(newTask)
                    } catch (e: ParseException) {
                        Toast.makeText(context, "Invalid time format", Toast.LENGTH_SHORT).show()
                    } catch (e: NumberFormatException) {
                        Toast.makeText(context, "Invalid duration", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Save")
                }
            }
        }
    }
}
