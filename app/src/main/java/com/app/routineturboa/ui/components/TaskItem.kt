package com.app.routineturboa.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.app.routineturboa.data.model.Task
import com.app.routineturboa.utils.TimeUtils.convertTo12HourFormat

@Composable
fun TaskItem(task: Task) {
    // Card to hold the task details
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(5.dp)  // Like shadow
    ) {

        // vertical layout container inside the Card
        Column(
            modifier = Modifier
                .padding(25.dp)
        ) {

            // Row for task name and icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Task name
                Text(
                    text = task.taskName,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer {
                            this.alpha = 0.99f // Trigger anti-aliasing
                        },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                // Task icon
                Icon(
                    imageVector = Icons.Default.Task,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Spacer between task name and start time
            Spacer(modifier = Modifier.height(18.dp))

            // Row for start time and duration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // Start time
                Text(
                    text = "${convertTo12HourFormat(task.startTime.split(" ")[1])} - ${convertTo12HourFormat(task.endTime.split(" ")[1])}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.graphicsLayer {
                        this.alpha = 0.99f // Trigger anti-aliasing
                    }
                )

                // Duration
                Text(
                    text = "${task.duration} minutes",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.graphicsLayer {
                        this.alpha = 0.99f // Trigger anti-aliasing
                    }
                )
            }
        }

    }
}
