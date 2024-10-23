package com.app.routineturboa.ui.tasks.dialogs

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.sharp.AddAlert
import androidx.compose.material.icons.sharp.Start
import androidx.compose.material.icons.sharp.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.app.routineturboa.R
import com.app.routineturboa.data.room.TaskEntity
import com.app.routineturboa.ui.models.TaskFormData
import com.app.routineturboa.ui.reusable.CustomTextField
import com.app.routineturboa.ui.reusable.dropdowns.SelectTaskTypeDropdown
import com.app.routineturboa.ui.reusable.dropdowns.ShowMainTasksDropdown
import com.app.routineturboa.data.dbutils.Converters.timeToString
import com.app.routineturboa.data.dbutils.Converters.stringToTime
import com.app.routineturboa.data.dbutils.RecurrenceType
import com.app.routineturboa.utils.TaskTypes
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullEditDialog(
    mainTasks: List<TaskEntity>,
    task: TaskEntity,
    onConfirmEdit: (TaskFormData) -> Unit,
    onCancel: () -> Unit
) {
    val tag = "FullEditDialog"
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Actual data of task being edited as State variables
    var id by remember { mutableIntStateOf(task.id) }
    var taskName by remember { mutableStateOf(task.name) }
    var notes by remember { mutableStateOf(task.notes) }
    var startTime by remember { mutableStateOf(task.startTime) }
    var endTime by remember { mutableStateOf(task.endTime) }
    var duration by remember { mutableIntStateOf(task.duration?: 1) }
    var reminder by remember { mutableStateOf(task.reminder) }
    var position by remember { mutableIntStateOf(task.position?: 1) }
    var taskType by remember { mutableStateOf(task.type) }
    // State for linked main task if this is a helper task
    var linkedMainTaskIdIfHelper by remember { mutableStateOf<Int?>(null) }

    var isRecurring by remember { mutableStateOf(false) }
    var recurrenceType by remember { mutableStateOf(RecurrenceType.DAILY) } // Default to 'DAILY'
    var recurrenceInterval by remember { mutableIntStateOf(1) }
    var recurrenceEndDate by remember { mutableStateOf<LocalDate?>(null) }

    // Convert state variables to  string for display
    var startTimeString by remember {mutableStateOf(timeToString(startTime))}
    var endTimeString by remember {mutableStateOf(timeToString(endTime))}
    var reminderString by remember {mutableStateOf(timeToString(reminder))}
    var durationString by remember {mutableStateOf(duration.toString())}
    val idFormatted by remember {mutableStateOf(id.toString())}
    var positionString by remember {mutableStateOf(position.toString())}

    var isReminderLinked by remember { mutableStateOf(true) }


        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f),
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Edit Task", style = MaterialTheme.typography.titleLarge)

                CustomTextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = "Task Name",
                    placeholder = "Enter task name",
                    leadingIcon = Icons.Default.AddTask,
                    modifier = Modifier.fillMaxWidth()
                )

                // Start Time and Reminder Side by Side with Link/Link-off button
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    CustomTextField(
                        value = startTimeString,
                        onValueChange = { startTimeString = it },
                        label = "Start Time",
                        placeholder = "Enter start time",
                        leadingIcon = Icons.Sharp.Start,
                    )

                    Icon (
                        imageVector = if (isReminderLinked) Icons.Default.Link
                        else Icons.Default.LinkOff,
                        contentDescription = "Link Reminder",
                        modifier = Modifier
                            .clickable {
                                isReminderLinked = !isReminderLinked
                                if (isReminderLinked) {
                                    reminderString = startTimeString
                                }
                            }
                            .size(25.dp)
                    )

                    CustomTextField(
                        value = reminderString,
                        onValueChange = { reminderString = it },
                        label = "Reminder",
                        placeholder = "Enter reminder time",
                        leadingIcon = Icons.Sharp.AddAlert,
                        enabled = !isReminderLinked,
                    )
                }

                // Duration and End Time Side by Side
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LaunchedEffect (durationString){
                        if (durationString.isNotEmpty()) {
                            try {
                                endTimeString = timeToString(startTime?.plusMinutes(durationString.toLong()))
                            } catch (e: NumberFormatException) {
                                Toast.makeText(context, "Invalid duration format", Toast.LENGTH_SHORT).show()
                            }
                        }

                    }

                    CustomTextField(
                        value = durationString,
                        onValueChange = { durationString = it },
                        label = "Duration",
                        placeholder = "Enter duration",
                        leadingIcon = Icons.Sharp.Timer,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                    )

                    CustomTextField(
                        value = endTimeString,
                        onValueChange = { endTimeString = it },
                        label = "End Time",
                        placeholder = "Enter End Time",
                        leadingIconResId = R.drawable.arrowrighttoleft,
                        modifier = Modifier.weight(1f),
                    )
                }

                CustomTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "Notes",
                    placeholder = "Add Notes",
                    leadingIcon = Icons.Default.Link,
                    modifier = Modifier
                        .fillMaxWidth(),
                    singleLine = false
                )

                SelectTaskTypeDropdown(
                    taskType?: "",
                    onTaskTypeSelected = { newType -> taskType = newType }
                )

                // Show the main task dropdown only if task type is "HelperTask"
                if (taskType == TaskTypes.HELPER) {
                    ShowMainTasksDropdown(
                        mainTasks = mainTasks,
                        selectedMainTaskId = linkedMainTaskIdIfHelper,
                        onTaskSelected = { taskId -> linkedMainTaskIdIfHelper = taskId }
                    )
                }

                CustomTextField(
                    value = idFormatted,
                    onValueChange = { },
                    label = "ID (Only for dev)",
                    placeholder = "Internal purposes",
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                )

                CustomTextField(
                    value = positionString,
                    onValueChange = {positionString = it},
                    label = "Position (Don't Change) (Only for dev)",
                    placeholder = "Internal purposes. Don't change.",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Cancel Button
                    Button(
                        modifier = Modifier
                            .padding(5.dp)
                            .size(120.dp, 50.dp),
                        shape = RoundedCornerShape(25.dp),

                        onClick = { onCancel() },
                        colors = ButtonDefaults.buttonColors(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        )

                    ) {
                        Text("Cancel")
                    }

                    // Save Button
                    FloatingActionButton(
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(5.dp)
                            .size(120.dp, 50.dp),
                        elevation = FloatingActionButtonDefaults.elevation(10.dp),
                        onClick = {
                            Log.d(tag, "Save Button clicked...")
                            coroutineScope.launch {
                                try {
                                    startTime = stringToTime(startTimeString)
                                    endTime = stringToTime(endTimeString)
                                    reminder = stringToTime(reminderString)
                                    duration = durationString.toInt()
                                    id = idFormatted.toInt()
                                    position = positionString.toInt()

                                } catch (e: Exception) {
                                    Log.e(tag, "Error: $e")
                                    Toast.makeText(context, "Error saving task: ${e.message}", Toast.LENGTH_LONG).show()
                                }

                                val updatedTaskFormData = TaskFormData(
                                    id = task.id,
                                    name = taskName,

                                    startTime = startTime,
                                    endTime = endTime,
                                    notes = notes,

                                    taskType = taskType,
                                    position = position,
                                    duration = duration,
                                    reminder = reminder,
                                    mainTaskId = linkedMainTaskIdIfHelper,

                                    startDate = task.startDate,
                                    isRecurring = task.isRecurring ?: false,
                                    recurrenceType = task.recurrenceType,
                                    recurrenceInterval = task.recurrenceInterval,
                                    recurrenceEndDate = task.recurrenceEndDate

                                )

                                onConfirmEdit(updatedTaskFormData)

                            } // End of Coroutine
                        }
                    ) {
                        Text("Save")
                    }
                }


            }
        }

}
