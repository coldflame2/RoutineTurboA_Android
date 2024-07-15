package com.app.routineturboa.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.app.routineturboa.data.model.TaskEntity
import java.time.format.DateTimeFormatter

@Composable
fun TaskList(
    tasks: List<TaskEntity>,
    onTaskSelected: (TaskEntity?) -> Unit,
    onTaskEdited: (TaskEntity?) -> Unit,
    onTaskDelete: (TaskEntity) -> Unit,
    isTaskFirst: (TaskEntity) -> Boolean,
    isTaskLast: (TaskEntity) -> Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .height(350.dp),
        contentPadding = PaddingValues(2.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(tasks, key = { it.id }) { task ->
            TaskItem(
                task = task,
                onClick = { onTaskSelected(task) },
                onEditClick = { onTaskEdited(it) },
                onDelete = { onTaskDelete(task) },
                canDelete = !(isTaskFirst(task) || isTaskLast(task))
            )
        }
        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun TaskItem(
    task: TaskEntity,
    onClick: () -> Unit,
    onEditClick: (TaskEntity) -> Unit,
    onDelete: (TaskEntity) -> Unit,
    canDelete: Boolean
) {
    TaskCard(
        modifier = Modifier
            .padding(0.dp, 0.dp, 0.dp, 0.dp)
            .fillMaxWidth(),
        task = task,
        onClick = onClick,
        onEditClick = onEditClick,
        onDelete = onDelete,
        canDelete = canDelete
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
    task: TaskEntity,
    onClick: () -> Unit,
    onEditClick: (TaskEntity) -> Unit,
    onDelete: (TaskEntity) -> Unit,
    canDelete: Boolean,
    colors: CardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var longPressOffset by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current

    Box(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { offset ->
                            longPressOffset = offset
                            expanded = true
                        },
                        onTap = {
                            onClick()
                        }
                    )
                },
            colors = colors
        ) {
            content()
        }

        // Adjust the offsets for context-menu
        val adjustedOffset = Offset(
            x = longPressOffset.x,
            y = longPressOffset.y - 150
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(
                x = with(density) { adjustedOffset.x.toDp() },
                y = with(density) { adjustedOffset.y.toDp() }
            )
        ) {
            DropdownMenuItem(
                text = { Text("Edit") },
                onClick = {
                    expanded = false
                    onEditClick(task)
                }
            )

            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    expanded = false
                    if (canDelete) {
                        onDelete(task)
                    }
                },
                enabled = canDelete
            )
            }
        }
}


@Composable
fun UpperRow(task: TaskEntity, onEditClick: (TaskEntity) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp, 0.dp, 1.dp, 0.dp),
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
fun TaskName(task: TaskEntity) {
    Text(
        text = task.taskName,
        maxLines = 2,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun LowerRow(task: TaskEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp, 0.dp, 5.dp, 0.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TaskTimings(task)
        TaskDuration(task)
    }
}

@Composable
fun TaskTimings(task: TaskEntity) {
    // Display startTime and endTime in UI friendly format
    val formattedStartTime = task.startTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
    val formattedEndTime = task.endTime.format(DateTimeFormatter.ofPattern("hh:mm a"))

    Text(
        text = "$formattedStartTime - $formattedEndTime",
        style = MaterialTheme.typography.labelSmall.copy(
            color = MaterialTheme.colorScheme.outline,
            fontStyle = FontStyle.Italic
        )
    )
}

@Composable
fun TaskDuration(task: TaskEntity) {
    Text(
        text = "${task.duration} minutes",
        style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.outline
        )
    )
}

