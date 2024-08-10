package com.app.routineturboa.ui.task

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.routineturboa.data.local.TaskEntity
import com.app.routineturboa.reminders.ReminderManager
import com.app.routineturboa.ui.task.dialogs.FullEditDialog
import com.app.routineturboa.utils.TimeUtils.dateTimeToString
import com.app.routineturboa.utils.TimeUtils.strToDateTime
import com.app.routineturboa.viewmodel.TasksViewModel

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun QuickEdit(
    task: TaskEntity,
    onEndEditing: () -> Unit,
    tasksViewModel: TasksViewModel,
    reminderManager: ReminderManager,
    context: Context
) {
    val tag = "QuickEditScreen"
    val isFullEditing = remember { mutableStateOf(false) }

    val startTime by remember { mutableStateOf(task.startTime) }
    var taskName by remember { mutableStateOf(task.name) }

    var duration by remember { mutableIntStateOf(task.duration) }
    var durationString by remember { mutableStateOf(duration.toString()) }

    var endTime by remember { mutableStateOf(task.endTime) }
    var endTimeString by remember { mutableStateOf(dateTimeToString(endTime)) }


    LaunchedEffect(durationString) {
        if (durationString.isNotEmpty()) {
            try {
                endTimeString = dateTimeToString(startTime.plusMinutes(durationString.toLong()))

            } catch (e: NumberFormatException) {
                Toast.makeText(context, "Invalid duration format", Toast.LENGTH_SHORT).show()
            }
        }
    } // end of LaunchedEffect for Duration

    fun beginTaskUpdateOperations(updatedTask: TaskEntity) {
        Log.d(tag, "Starting task update operations...")
        tasksViewModel.updateTaskAndAdjustNext(updatedTask)
    }

    TextField(
    value = taskName,
    onValueChange = { taskName = it },
    label = { Text("Task Name") }
    )

    TextField(
    value = durationString,
    onValueChange = { durationString = it },
    label = { Text("Duration (minutes)") }
    )

    TextField(
    value = endTimeString,
    onValueChange = { endTimeString = it },
    label = { Text("End Time") }
    )

    Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Cancel Button
        TextButton(onClick = { onEndEditing() }) {
            Text("Cancel")
        }

        // Save Button
        ExtendedFloatingActionButton (
            // <editor-fold desc="Save button with onClick function">
            modifier = Modifier.padding(end = 20.dp),

            // On Save Button in Quick-Edit Clicked
            onClick = {
                Toast.makeText(context, "Saving....", Toast.LENGTH_SHORT).show()
                Log.d(tag, "Save Button in Quick-Edit Screen clicked...")
                try{
                    duration = durationString.toInt()
                    endTime = strToDateTime(endTimeString)

                } catch (e: Exception) {
                    Log.e(tag, "Error: $e")
                    Toast.makeText(context, "Error saving task values: ${e.message}", Toast.LENGTH_LONG).show()
                }

                Log.d(tag, "New Edited Task Details. Name: $taskName, Duration: $duration, End Time: $endTime")

                val taskWithUpdatedData = task.copy(
                    name = taskName,
                    duration = duration,
                    endTime = endTime
                )
                tasksViewModel.updateTaskAndAdjustNext(taskWithUpdatedData)
                onEndEditing()
            }
        // </editor-fold>
        )

        {
            Text(
                text = "Save",
            )
        }

        // Full Screen Editing
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Full-Screen Editing",
            modifier = Modifier.clickable{
                isFullEditing.value = true
            }
                .align(Alignment.CenterVertically)
        )

        if (isFullEditing.value) {
            FullEditDialog(
                task = task,
                onConfirmTaskEdit = { updatedTask ->
                    isFullEditing.value = false
                    tasksViewModel.updateTaskAndAdjustNext(updatedTask)
                    onEndEditing()
                },
                onCancel = {
                    isFullEditing.value = false
                }
            )
        }
    }
}