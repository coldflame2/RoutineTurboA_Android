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
fun EditTaskScreen(
    task: Task,
    onSave: (Task) -> Unit,
    onCancel: () -> Unit
) {
    var taskName by remember { mutableStateOf(task.taskName) }
    var startTime by remember { mutableStateOf(task.startTime) }
    var endTime by remember { mutableStateOf(task.endTime) }
    var duration by remember { mutableStateOf(task.duration.toString()) }

    val context = LocalContext.current

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
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface
                )
            )
            TextField(
                value = startTime,
                onValueChange = { startTime = it },
                label = { Text("Start Time (hh:mm a)") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface
                )
            )
            TextField(
                value = endTime,
                onValueChange = { endTime = it },
                label = { Text("End Time (hh:mm a)") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface
                )
            )
            TextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Duration") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface
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
                    if (TimeUtils.isValidTimeFormat(startTime) && TimeUtils.isValidTimeFormat(endTime)) {
                        try {
                            val updatedTask = task.copy(
                                taskName = taskName,
                                startTime = startTime,
                                endTime = endTime,
                                duration = duration.toInt()
                            )
                            onSave(updatedTask)
                        } catch (e: ParseException) {
                            Toast.makeText(context, "Invalid time format", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Please enter valid times in hh:mm a format", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Save")
                }
            }
        }
    }
}

