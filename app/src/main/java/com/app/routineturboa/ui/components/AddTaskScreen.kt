package com.app.routineturboa.ui.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.app.routineturboa.data.model.TaskEntity
import com.app.routineturboa.utils.CustomTextField
import com.app.routineturboa.utils.TimeUtils.dateTimeToString
import com.app.routineturboa.utils.TimeUtils.possibleFormats
import com.app.routineturboa.utils.TimeUtils.strToDateTime
import com.app.routineturboa.viewmodel.TasksViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeParseException


@Composable
fun AddTaskScreen(
    tasksViewModel: TasksViewModel,
    clickedTask: TaskEntity?,
    onAddClick: (TaskEntity) -> Unit,
    onCancel: () -> Unit
) {
    val tag = "AddTaskScreen"
    Log.d(tag, "AddTaskScreen: clickedTask: $clickedTask")

    val context = LocalContext.current
    val clickedTaskEndTime = clickedTask?.endTime
    val clickedTaskID = clickedTask?.id
    val clickedTaskPosition = clickedTask?.position

    val taskBelowBeforeAdding = tasksViewModel.getTaskBelow(clickedTask!!)
    val durationTaskBelowBeforeAdding = taskBelowBeforeAdding?.duration

    // Data in State variables
    val id by remember { mutableIntStateOf(clickedTaskID?.plus(1) ?: 1) }
    var taskName by remember { mutableStateOf(" ") }
    var taskNotes by remember { mutableStateOf( " ") }
    var startTime by remember { mutableStateOf(clickedTaskEndTime ?: LocalDateTime.now()) }
    var duration by remember { mutableLongStateOf(1L) }
    var endTime by remember { mutableStateOf(
        clickedTaskEndTime?.plusMinutes(duration) ?: LocalDateTime.now().plusMinutes(1)) }
    var reminder by remember { mutableStateOf(startTime) }
    var taskType by remember { mutableStateOf("") }
    var taskPosition by remember { mutableIntStateOf(clickedTaskPosition?.plus(1)?: 2) }

    var isReminderLinked by remember { mutableStateOf(true) }
    var isDurationLinked by remember { mutableStateOf(true) }

    // Convert state variables to  string for display
    var startTimeFormatted by remember {mutableStateOf(dateTimeToString(startTime))}
    var endTimeFormatted by remember {mutableStateOf(dateTimeToString(endTime))}
    var reminderFormatted by remember {mutableStateOf(dateTimeToString(reminder))}
    var durationFormatted by remember {mutableStateOf(duration.toString())}
    val idFormatted by remember {mutableStateOf(id.toString())}
    val taskPositionFormatted by remember {mutableStateOf(taskPosition.toString())}

    Log.d(tag, "clicked task name and position: ${clickedTask.taskName} ${clickedTask.position}")
    Log.d(tag, "clicked task end time: ${clickedTask.endTime}")
    Log.d(tag, "new task start time: $startTimeFormatted")

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

    fun endTimeStringToLDT(timeString: String): LocalDateTime? {
        val trimmedTimeString = timeString.trim().uppercase()
        for (formatter in possibleFormats) {
            try {
                val localTime = LocalTime.parse(trimmedTimeString, formatter)
                return LocalDateTime.of(LocalDate.now(), localTime)
            } catch (e: DateTimeParseException) {
                Log.e("TimeUtils", "Error parsing time string: $timeString")
            }
        }

        return null
    }

    // Calculate Duration based on End Time
    LaunchedEffect(endTimeFormatted) {
        if (endTimeFormatted.isNotEmpty()) {
            try {
                // Attempt to parse the end time string
                val endTimeParsed = endTimeStringToLDT(endTimeFormatted)

                if (endTimeParsed != null && endTimeParsed.isAfter(startTime)) {
                    // Calculate the duration in minutes
                    val durationInMinutes = java.time.Duration.between(startTime, endTimeParsed).toMinutes()

                    // Update the durationFormatted state variable
                    durationFormatted = durationInMinutes.toString()
                } else {
                    // End time is not valid or is not after start time, do nothing or reset durationFormatted
                    Toast.makeText(context, "End time must be after start time and valid.", Toast.LENGTH_SHORT).show()
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
                    TextField(
                        value = startTimeFormatted,
                        onValueChange = { startTimeFormatted = it },
                        label = { Text("Start Time") },
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

                    TextField(
                        value = reminderFormatted,
                        onValueChange = { reminderFormatted = it },
                        label = { Text("Reminder") },
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
                    TextField(
                        value = durationFormatted,
                        onValueChange = { durationFormatted = it },
                        label = { Text("Duration (minutes)") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                    )

                    TextField(
                        value = endTimeFormatted,
                        onValueChange = { endTimeFormatted = it },
                        label = { Text("End Time") },
                        modifier = Modifier.weight(1f),
                    )
                }

                TextField(
                    value = taskNotes,
                    onValueChange = { taskNotes = it },
                    label = { Text("Task Notes") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(ScrollState(1)),
                    placeholder = {Text ("Add task notes")},
                )

                TaskTypeDropdown()

                TextField(
                    value = idFormatted,
                    onValueChange = { },
                    label = { Text("Task ID") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                )

                TextField(
                    value = taskPositionFormatted,
                    onValueChange = { },
                    label = { Text("Task Position") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Cancel Button
                    Button(
                        modifier = Modifier.padding(5.dp),
                        shape = RoundedCornerShape(25.dp),
                        onClick = { onCancel() }) {
                        Text("Cancel")
                    }

                    // Save Button
                    Button(
                        modifier = Modifier.padding(5.dp),
                        shape = RoundedCornerShape(25.dp),
                        onClick = {
                        if (taskName.isNotBlank() && duration > 0) {
                            startTime = strToDateTime(startTimeFormatted)
                            endTime = strToDateTime(endTimeFormatted)
                            reminder = strToDateTime(reminderFormatted)
                            duration = durationFormatted.toLong()
                            taskPosition = taskPositionFormatted.toInt()

                            val newTask = TaskEntity(
                                position = taskPosition,
                                taskName = taskName,
                                notes = taskNotes,
                                startTime = startTime,
                                endTime = endTime,
                                duration = duration.toInt(),
                                reminder = reminder,
                                type = taskType,
                            )
                            onAddClick(newTask)

                        } else {
                            Toast.makeText(context, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskTypeDropdown() {
    val taskTypes = listOf("MainTask", "QuickTask")
    val expanded = remember { mutableStateOf(false) }
    val selectedTaskType = remember { mutableStateOf(taskTypes[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = it },
        modifier = Modifier.fillMaxWidth(),

    ) {
        TextField(
            readOnly = true,
            value = selectedTaskType.value,
            onValueChange = {},
            label = { Text("Task Type") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded.value
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor() // Ensures the dropdown menu aligns correctly with the TextField
        )
        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier.fillMaxWidth() // Ensure the dropdown menu fills the width of the TextField
        ) {
            taskTypes.forEach { taskType ->
                DropdownMenuItem(
                    onClick = {
                        selectedTaskType.value = taskType
                        expanded.value = false
                    },
                    text = { Text(taskType) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh), // Ensure each item fills the width of the TextField
                )
                HorizontalDivider(thickness = 4.dp)
            }
        }
    }
}
