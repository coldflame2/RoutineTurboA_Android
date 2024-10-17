package com.app.routineturboa.ui.tasks.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.app.routineturboa.data.room.TaskCompletionEntity
import com.app.routineturboa.data.room.TaskCompletionHistory

@Composable
fun TaskCompletionDialog(
    taskCompletionHistories: List<TaskCompletionHistory>,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
                .padding(16.dp),
            color = MaterialTheme.colorScheme.background, // Set background color
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Dialog Title
                Text(text = "Task Completions", style = MaterialTheme.typography.headlineMedium)

                // Scrollable Task Completions List
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    if (taskCompletionHistories.isNotEmpty()) {
                        taskCompletionHistories.forEach { taskWithCompletion ->
                            TaskCompletionItem(taskWithCompletion.task.name, taskWithCompletion.completions)
                        }
                    } else {
                        Text(text = "No task completions available.", style = MaterialTheme.typography.labelMedium)
                    }
                }

                // OK Button
                Button(
                    onClick = { onDismiss() },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("OK")
                }
            }
        }
    }
}

@Composable
fun TaskCompletionItem(taskName: String, completions: List<TaskCompletionEntity>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Text(
            text = taskName,
            style = MaterialTheme.typography.labelLarge
        )

        completions.forEach { taskCompletion ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (taskCompletion.isCompleted) {
                    Text(
                        text = "Completed",
                        style = MaterialTheme.typography.labelSmall
                    )
                } else {
                    Text(
                        text = "Incomplete",
                        color = Color.Red,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Text(
                    text = taskCompletion.date.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}
