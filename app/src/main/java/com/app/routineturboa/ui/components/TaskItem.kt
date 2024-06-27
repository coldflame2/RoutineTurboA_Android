package com.app.routineturboa.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.app.routineturboa.data.model.Task

@Composable
fun TaskItem(
    task: Task, onEditClick: (Task) -> Unit, onClick: () -> Unit
) {

    TaskCard(
        modifier = Modifier
            .padding(0.dp, 0.dp, 0.dp, 0.dp)
            .fillMaxWidth(),

        onClick = onClick
    ) {
        Column {
            UpperRow(task, onEditClick)  // TaskName and Edit Icon
            LowerRow(task)  // Task Duration and Timings
        }
    }
}

@Composable
fun TaskCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    colors: CardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = colors
    ) {
        content()
    }
}

@Composable
fun UpperRow(task: Task, onEditClick: (Task) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(5.dp, 0.dp, 1.dp, 0.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        TaskName(task)
        EditIcon { onEditClick(task) }
    }
}

@Composable
fun EditIcon(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(32.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit Task",
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(13.dp)
        )
    }
}

@Composable
fun TaskName(task: Task) {
    Text(
        text = task.taskName,
        maxLines = 2,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun LowerRow(task: Task) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(40.dp, 0.dp, 5.dp, 0.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TaskTimings(task)
        TaskDuration(task)
    }
}

@Composable
fun TaskTimings(task: Task) {
    Text(
        text = "${task.startTime} - ${task.endTime}",
        style = MaterialTheme.typography.labelSmall.copy(
            color = MaterialTheme.colorScheme.outline,
            fontStyle = FontStyle.Italic
        )
    )
}

@Composable
fun TaskDuration(task: Task) {
    Text(
        text = "${task.duration} minutes",
        style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.outline
        )
    )
}

