package com.app.routineturboa.ui.tasks.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.app.routineturboa.data.dbutils.Converters.timeToString
import com.app.routineturboa.data.room.entities.TaskEntity

@OptIn(ExperimentalMaterial3Api::class)
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
                text = "${task.name} - ${task.duration} M",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        },

        // Content of dialog
        text = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(top = 1.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    // Complete TaskEntity view
                    Text(
                        text = task.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Start and End Time and Type
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {

                        // Start Time
                        Column {
                            Text( // header
                                text = "Start",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            ) // actual data
                            Text(
                                text = timeToString(task.startTime).toString(),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        Spacer(modifier = Modifier.width(30.dp))

                        // End Time
                        Column { // header
                            Text(
                                text = "End",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            ) // actual data
                            Text(
                                text = timeToString( task.endTime).toString(),
                                style = MaterialTheme.typography.titleSmall
                            )
                        }

                        Spacer(modifier = Modifier.width(30.dp))

                        // Type
                        Column {
                            Text(
                                text = "Type",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = task.type?: "None",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }

                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Notes
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

                    Spacer(modifier = Modifier.height(10.dp))

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

                    Spacer(modifier = Modifier.height(10.dp))

                    // RecurringType
                    Column(
                        modifier = Modifier.padding(top = 2.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "Recurring",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "isRecurring: ${task.isRecurring}.toString()",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "recurrenceType: ${task.recurrenceType?.name.toString()}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Recurrence-Start: ${(task.startDate.toString())}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                    }
                }
            }

        },
        modifier = Modifier.padding(1.dp)
    )
}