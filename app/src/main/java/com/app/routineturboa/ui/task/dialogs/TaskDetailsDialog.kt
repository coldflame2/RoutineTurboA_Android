package com.app.routineturboa.ui.task.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.routineturboa.data.local.TaskEntity
import com.app.routineturboa.utils.TimeUtils.dateTimeToString

@Composable
fun TaskDetailsDialog(
    task: TaskEntity,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Task Info",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = "Task Details", style = MaterialTheme.typography.titleLarge)
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "ID: ${task.id}", style = MaterialTheme.typography.bodyMedium)

                val startTimeString = dateTimeToString(task.startTime)
                Text(text = "Start Time: $startTimeString", style = MaterialTheme.typography.bodyMedium)

                val endTimeString = dateTimeToString(task.endTime)
                Text(text = "End Time: $endTimeString", style = MaterialTheme.typography.bodyMedium)

                Text(text = "Name: ${task.name}", style = MaterialTheme.typography.bodyMedium)

                if (task.notes.isNotBlank()) {
                    Text(text = "Notes: ${task.notes}", style = MaterialTheme.typography.bodyMedium)
                }

                Text(text = "Position: ${task.position}", style = MaterialTheme.typography.bodyMedium)

                Text(text = "Type: ${task.type}", style = MaterialTheme.typography.bodyMedium)

                val reminderString = dateTimeToString(task.reminder)
                Text(text = "Reminder: $reminderString", style = MaterialTheme.typography.bodyMedium)

                // Adding a divider for better visual separation
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                )
            }
        },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
                Text("Close")
            }
        }
    )
}
