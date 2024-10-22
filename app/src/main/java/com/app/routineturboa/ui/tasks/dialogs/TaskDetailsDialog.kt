package com.app.routineturboa.ui.tasks.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.app.routineturboa.data.room.TaskEntity
import com.app.routineturboa.data.dbutils.Converters.timeToString

@Composable
fun TaskDetailsDialog(
    task: TaskEntity,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = true),
        confirmButton = {
            TextButton(
                onClick = { onDismiss() },
            ) {
                Text("Close", style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onPrimary))
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Task Info",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        },
        title = {
            Text(
                text = "${task.name} [${task.duration} Mins]",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(top = 10.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    // Time information
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            Text(
                                text = "Start",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = timeToString(task.startTime).toString(),
                                style = MaterialTheme.typography.titleSmall
                            )
                        }

                        Spacer(modifier = Modifier.width(25.dp))
                        Column {
                            Text(
                                text = "End",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = timeToString( task.endTime).toString(),
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Notes",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = task.notes ?: "None",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                    // Reminder
                    Column {
                        Text(
                            text = "Reminder",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = timeToString(task.reminder).toString(),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                    // Type
                    Column {
                        Text(
                            text = "Type",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = task.type?: "None",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Internal details
                    Column {
                        Text(
                            text = "Internal Details",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "ID: ${task.id}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Position: ${task.position}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column {
                        Text(
                            text = task.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        },
        modifier = Modifier.padding(0.dp)
    )
}