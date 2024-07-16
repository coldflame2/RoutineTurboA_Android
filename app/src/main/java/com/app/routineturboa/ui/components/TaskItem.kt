package com.app.routineturboa.ui.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
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
    val taskClicked = remember{ mutableStateOf<TaskEntity?>(null)}

    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .height(350.dp)
    ) {
        items(tasks, key = { it.id }) { task ->
            TaskCard(
                task = task,
                onClick = { taskClicked.value = task
                    onTaskSelected(task) },
                onEditClick = { onTaskEdited(it) },
                onDelete = { onTaskDelete(task) },
                canDelete = !(isTaskFirst(task) || isTaskLast(task))
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 1.dp),
                thickness = 2.dp,
                color = Color.LightGray,
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
    canDelete: Boolean,
    colors: CardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
) {
    var expanded by remember { mutableStateOf(false) }
    var longPressOffset by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current

    Card(
        modifier = modifier
            .fillMaxWidth()
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
        colors = colors
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {


                UpperRow(task, onEditClick, )

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


@Composable
fun UpperRow(task: TaskEntity, onEditClick: (TaskEntity) -> Unit) {

    var isExpanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "arrow rotation"
    )
    val formattedReminder = task.reminder.format(DateTimeFormatter.ofPattern("hh:mm a"))

    Column {
        Row(modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp, 0.dp, 1.dp, 0.dp)
            .height(38.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = task.taskName,
                maxLines = 2,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Default.Alarm,
                contentDescription = "Edit Reminder",
                tint = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .size(30.dp)
                    .padding(0.dp, 0.dp, 8.dp, 0.dp),
            )

            Text(
                text = formattedReminder,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.width(10.dp))

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier.rotate(rotationState)
                    .clickable { isExpanded = !isExpanded }
                    .size(80.dp)
            )

        }

        AnimatedVisibility(visible = isExpanded) {
            LowerRow(task, onEditClick)
        }

    }

}


@Composable
fun LowerRow(task: TaskEntity, onEditClick: (TaskEntity) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 0.dp, 0.dp, 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(30.dp))

        val formattedStartTime = task.startTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
        val formattedEndTime = task.endTime.format(DateTimeFormatter.ofPattern("hh:mm a"))

        Text(
            text = "$formattedStartTime - $formattedEndTime   [${task.duration} minutes]",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.outline)
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = { onEditClick(task) }
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Task",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(25.dp)
            )
        }

    }
}
