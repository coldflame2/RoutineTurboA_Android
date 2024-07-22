package com.app.routineturboa.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.app.routineturboa.data.model.TaskEntity
import java.time.format.DateTimeFormatter

const val TAG = "TaskItem"

@Composable
fun TasksLazyColumn(
    tasks: List<TaskEntity>,
    onTaskSelected: (TaskEntity?) -> Unit,
    onTaskEdited: (TaskEntity?) -> Unit,
    onTaskDelete: (TaskEntity) -> Unit,
    isTaskFirst: (TaskEntity) -> Boolean,
    isTaskLast: (TaskEntity) -> Boolean
) {
    Log.d(TAG, "TasksLazyColumn called")
    val taskClicked = remember { mutableStateOf<TaskEntity?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(tasks, key = { it.id }) { task ->
            TaskCard(
                task = task,
                onClick = {
                    taskClicked.value = task
                    onTaskSelected(task)
                },
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
fun TaskCard(
    modifier: Modifier = Modifier,
    task: TaskEntity,
    onClick: () -> Unit,
    onEditClick: (TaskEntity) -> Unit,
    onDelete: (TaskEntity) -> Unit,
    canDelete: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    var longPressOffset by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { offset ->
                        longPressOffset = offset
                        expanded = true
                    },
                    onTap = {
                        Log.d(TAG, "TaskCard onTap called")
                        onClick()
                    }
                )
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                val formattedStartTime = task.startTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
                val formattedEndTime = task.endTime.format(DateTimeFormatter.ofPattern("hh:mm a"))

                Text(
                    text = formattedStartTime,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Box(
                    modifier = Modifier
                        .padding(vertical = 1.dp)
                        .fillMaxHeight()
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Spacer(modifier = Modifier.height(26.dp))
                }

                Text(
                    text = formattedEndTime,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(2f)
            ) {
                Text(
                    text = task.taskName,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    overflow = TextOverflow.Ellipsis
                )

                val formattedReminder = task.reminder.format(DateTimeFormatter.ofPattern("hh:mm a"))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp).padding(start=10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = "Reminder",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )

                    Text(
                        text = formattedReminder,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Row (verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp).padding(start=10.dp)
                ){
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Duration",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )


                    Text(
                        text = "${task.duration} minutes",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

            }

            IconButton(
                onClick = { onEditClick(task) },
                modifier = Modifier.weight(0.2f)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Task",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        offset = DpOffset(
            x = with(density) { longPressOffset.x.toDp() },
            y = with(density) { longPressOffset.y.toDp() - 150.dp }
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
