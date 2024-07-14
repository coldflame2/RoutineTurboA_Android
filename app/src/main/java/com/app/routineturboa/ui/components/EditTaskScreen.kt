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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.app.routineturboa.data.model.TaskEntity
import com.app.routineturboa.utils.TimeUtils
import java.text.ParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    task: TaskEntity,
    onSave: (TaskEntity) -> Unit,
    onCancel: () -> Unit
) {
    var taskName by remember { mutableStateOf(task.taskName) }
    var startTime by remember { mutableStateOf(task.startTime) }
    var endTime by remember { mutableStateOf(task.endTime) }
    var duration by remember { mutableStateOf(task.duration.toString()) }
    var reminder by remember { mutableStateOf(task.reminder) }

    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(1.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text(
                text = "Edit",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(10.dp)
            )

            TextField(
                value = taskName,
                onValueChange = { taskName = it },
                label = { Text("Task Name") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = startTime,
                onValueChange = { startTime = it },
                label = { Text("Start Time (hh:mm a)") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = endTime,
                onValueChange = { endTime = it },
                label = { Text("End Time (hh:mm a)") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Duration") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = reminder,
                onValueChange = { reminder = it },
                label = { Text("Reminder") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
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
                    if (TimeUtils.isValidTimeFormat(startTime) && TimeUtils.isValidTimeFormat(endTime)) {
                        try {
                            val updatedTask = task.copy(
                                taskName = taskName,
                                startTime = startTime,
                                endTime = endTime,
                                reminder = reminder,
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

