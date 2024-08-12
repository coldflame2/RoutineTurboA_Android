package com.app.routineturboa.ui.tasks.dialogs

import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.app.routineturboa.utils.TaskTypes
import com.app.routineturboa.utils.TimeUtils.dateTimeToString
import com.app.routineturboa.utils.TimeUtils.strToDateTime


@Composable
fun AddTaskDialog(
    mainTasks: List<TaskEntity>,
    clickedTask: TaskEntity? = null,
    taskBelowClickedTask: TaskEntity? = null,
    onAddClick: (TaskFormData) -> Unit,
    onCancel: () -> Unit
) {
    val tag = "AddTaskDialog"

    if (clickedTask == null) {
        Log.e(tag, "clickedTask is null")
        Toast.makeText(LocalContext.current, "Click a task first.", Toast.LENGTH_SHORT).show()
        return
    }

    Log.d(tag, "clickedTask: $clickedTask")

    // Context and Initial Task Data
    val context = LocalContext.current
    val clickedTaskEndTime = clickedTask.endTime
    val clickedTaskID = clickedTask.id
    val clickedTaskPosition = clickedTask.position

    // Data in State variables
    val id by remember { mutableIntStateOf(clickedTaskID + 1) }
    var startTime by remember { mutableStateOf(clickedTaskEndTime) }
    val defaultDuration = 1L
    var endTime by remember { mutableStateOf(startTime.plusMinutes(defaultDuration)) }
    var taskName by remember { mutableStateOf(" ") }
    var notes by remember { mutableStateOf( " ") }
    var duration by remember { mutableLongStateOf(defaultDuration) }
    var reminder by remember { mutableStateOf(startTime) }
    var taskType by remember { mutableStateOf("") }

    // Add 1 to position of new task
    var taskPosition by remember { mutableIntStateOf(clickedTaskPosition.plus(1)) }

    // Convert state variables to  string for display
    var startTimeFormatted by remember {mutableStateOf(dateTimeToString(startTime))}
    var endTimeFormatted by remember {mutableStateOf(dateTimeToString(endTime))}
    var reminderFormatted by remember {mutableStateOf(dateTimeToString(reminder))}
    var durationFormatted by remember {mutableStateOf(duration.toString())}
    val idFormatted by remember {mutableStateOf(id.toString())}
    var positionFormatted by remember {mutableStateOf(taskPosition.toString())}


    val durationTaskBelowBeforeAdding = taskBelowClickedTask?.duration

    var isReminderLinked by remember { mutableStateOf(true) }

    Log.d(tag, "clicked task name and position: ${clickedTask.name} ${clickedTask.position}")
    Log.d(tag, "clicked task end time: ${clickedTask.endTime}")
    Log.d(tag, "new task start time: $startTimeFormatted")

    // State to hold selected main task ID
    var linkedMainTaskIdIfHelper by remember { mutableStateOf<Int?>(null) }

    // Calculate endTime based on duration input
    LaunchedEffect(durationFormatted) {
        if (durationFormatted.isNotEmpty()) {
            try {
                val durationLong = durationFormatted.toLong()
                if (durationLong > 0 || durationLong < durationTaskBelowBeforeAdding!!) {
                    endTimeFormatted = dateTimeToString(startTime.plusMinutes(durationLong))
                } else {
                    Toast.makeText(context, "Invalid duration. End time unchanged.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(context, "Invalid duration format. End time unchanged.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Duration is empty. End time unchanged.", Toast.LENGTH_SHORT).show()
        }
    }

    // Calculate Duration based on End Time
    LaunchedEffect(endTimeFormatted) {
        if (endTimeFormatted.isNotEmpty()) {
            try {
                // Attempt to parse the end time string
                val endTimeParsed = strToDateTime(endTimeFormatted)

                if (endTimeParsed.isAfter(startTime)) {
                    // Calculate the duration in minutes
                    val durationInMinutes = java.time.Duration.between(startTime, endTimeParsed).toMinutes()

                    // Update the durationFormatted state variable
                    durationFormatted = durationInMinutes.toString()
                } else {
                    // End time is not valid or is not after start time
                    Toast.makeText(context, ".", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Handle any unexpected exceptions
                Toast.makeText(context, "Error processing end time.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Update reminder time if linked
    LaunchedEffect(startTimeFormatted) {
        if (isReminderLinked) {
            reminderFormatted = startTimeFormatted
        }
    }

    // Listen for changes in the task type dropdown
    LaunchedEffect(taskType) {
        if (taskType == "HelperTask") {
            // Reset selectedMainTaskId when task type is changed to HelperTask
            linkedMainTaskIdIfHelper = null
        }
    }

    Dialog(
        onDismissRequest = { onCancel() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
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
                Text("Add New Task", style = MaterialTheme.typography.titleLarge)

                CustomTextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = "Task Name",
                    placeholder = "Enter task name",
                    leadingIcon = Icons.Default.AddTask,
                    modifier = Modifier.fillMaxWidth()
                )

                // Start Time and Reminder Side by Side with Link/Link-off button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CustomTextField(
                        value = startTimeFormatted,
                        onValueChange = { startTimeFormatted = it },
                        label = "Start Time",
                        placeholder = "Enter start time",
                        leadingIcon = Icons.Sharp.Start,
                        modifier = Modifier.weight(1f),
                    )

                    Icon (
                        imageVector = if (isReminderLinked) Icons.Default.Link else Icons.Default.LinkOff,
                        contentDescription = "Link Reminder",
                        modifier = Modifier
                            .clickable {
                                isReminderLinked = !isReminderLinked
                                if (isReminderLinked) {
                                    reminderFormatted = startTimeFormatted
                                }
                            }
                            .align(Alignment.CenterVertically)
                            .size(30.dp)
                    )

                    CustomTextField(
                        value = reminderFormatted,
                        onValueChange = { reminderFormatted = it },
                        label = "Reminder",
                        placeholder = "Enter reminder time",
                        leadingIcon = Icons.Sharp.AddAlert,
                        enabled = !isReminderLinked,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Duration and End Time Side by Side
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CustomTextField(
                        value = durationFormatted,
                        onValueChange = { durationFormatted = it },
                        label = "Duration",
                        placeholder = "Enter duration",
                        leadingIcon = Icons.Sharp.Timer,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                    )

                    CustomTextField(
                        value = endTimeFormatted,
                        onValueChange = { endTimeFormatted = it },
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

                // Task type dropdown
                SelectTaskTypeDropdown(
                    selectedTaskType = taskType,
                    onTaskTypeSelected = { newType ->
                        taskType = newType
                    }
                )

                // Show the main task dropdown only if task type is "HelperTask"
                if (taskType == TaskTypes.HELPER) {
                    ShowMainTasksDropdown(
                        mainTasks = mainTasks,
                        selectedMainTaskId = linkedMainTaskIdIfHelper,
                        onTaskSelected = { taskId -> linkedMainTaskIdIfHelper = taskId }
                    )
                }

                TextField(
                    value = idFormatted,
                    onValueChange = { },
                    label = { Text("Task ID") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                )

                CustomTextField(
                    value = positionFormatted,
                    onValueChange = {positionFormatted = it},
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
                        modifier = Modifier.padding(5.dp),
                        shape = RoundedCornerShape(25.dp),
                        onClick = onCancel
                    ) { Text("Cancel") }

                    // Add Button
                    Button(
                        modifier = Modifier.padding(5.dp),
                        shape = RoundedCornerShape(25.dp),
                        onClick = {
                            if (taskName.isNotBlank() && duration > 0) {
                                startTime = strToDateTime(startTimeFormatted)
                                endTime = strToDateTime(endTimeFormatted)
                                reminder = strToDateTime(reminderFormatted)
                                duration = durationFormatted.toLong()
                                taskPosition = positionFormatted.toInt()

                                val newTaskFormData = TaskFormData(
                                    position = taskPosition,
                                    name = taskName,
                                    notes = notes,
                                    startTime = startTime,
                                    endTime = endTime,
                                    duration = duration.toInt(),
                                    reminder = reminder,
                                    mainTaskId = linkedMainTaskIdIfHelper, // Link to the selected main task,
                                    taskType = taskType
                                )

                                onAddClick(newTaskFormData)

                            } else {
                                Toast.makeText(context,
                                    "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) { Text("Add")}  // END Add Button
                }  // Buttons Row
            }
        }
    }
}