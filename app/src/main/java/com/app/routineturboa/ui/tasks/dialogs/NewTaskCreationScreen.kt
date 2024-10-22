package com.app.routineturboa.ui.tasks.dialogs

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.sharp.AddAlert
import androidx.compose.material.icons.sharp.Start
import androidx.compose.material.icons.sharp.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.app.routineturboa.R
import com.app.routineturboa.data.room.TaskEntity
import com.app.routineturboa.ui.models.TaskFormData
import com.app.routineturboa.ui.reusable.CustomTextField
import com.app.routineturboa.ui.reusable.dropdowns.SelectTaskTypeDropdown
import com.app.routineturboa.data.dbutils.Converters.timeToString
import com.app.routineturboa.data.dbutils.Converters.stringToTime
import com.app.routineturboa.data.dbutils.RecurrenceType
import com.app.routineturboa.ui.reusable.dropdowns.ShowMainTasksDropdown
import com.app.routineturboa.utils.TaskTypes
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun NewTaskCreationScreen(
    clickedTask: TaskEntity,
    selectedDate: LocalDate?,
    mainTasks: List<TaskEntity>,
    onConfirm: (TaskFormData) -> Unit,
    onCancel: () -> Unit,
) {
    val tag = "NewTaskCreationScreen"
    Log.d(tag,
    "Showing NewTaskCreationScreen." +
        "clickedTask: $clickedTask" +
        "selectedDate: $selectedDate"
    )
    val context = LocalContext.current

    // region: State variables for new task details
    var taskName by remember { mutableStateOf("") }
    val clickedTaskEndTime = clickedTask.endTime
    var startTime by remember { mutableStateOf(clickedTaskEndTime) }
    val defaultDuration = 1L
    val newEndTime = startTime?.plusMinutes(defaultDuration) ?: LocalTime.now()
    var endTime by remember { mutableStateOf(newEndTime)}
    var notes by remember { mutableStateOf("") }
    var duration by remember { mutableLongStateOf(defaultDuration) }
    var reminder by remember { mutableStateOf(startTime) }
    var taskType by remember { mutableStateOf(TaskTypes.UNDEFINED) }
    val taskPosition by remember {mutableIntStateOf(clickedTask.position?.plus(1) ?: 2)}

    // Recurrence-related state variables
    var isRecurring by remember { mutableStateOf(false) }
    var recurrenceType by remember { mutableStateOf(RecurrenceType.DAILY) } // Default to 'DAILY'
    var recurrenceInterval by remember { mutableIntStateOf(1) }
    var recurrenceEndDate by remember { mutableStateOf<LocalDate?>(null) }

    // State for linked main task if this is a helper task
    var linkedMainTaskIdIfHelper by remember { mutableStateOf<Int?>(null) }

    // endregion

    // Dialog UI
    Dialog(
        onDismissRequest = { onCancel() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.9f),
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            Column(
                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Add New Task", style = MaterialTheme.typography.titleLarge)

                // region: Task name input
                CustomTextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = "Task Name",
                    placeholder = "Enter task name",
                    leadingIcon = Icons.Default.AddTask,
                    modifier = Modifier.fillMaxWidth()
                )
                // endregion

                // region: Start Time and reminders
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomTextField(
                        value = timeToString(startTime),
                        onValueChange = { startTime = stringToTime(it) },
                        label = "Start Time",
                        leadingIcon = Icons.Sharp.Start,
                        modifier = Modifier.weight(1f)
                    )

                    CustomTextField(
                        value = timeToString(reminder),
                        onValueChange = { reminder = stringToTime(it) },
                        label = "Reminder",
                        leadingIcon = Icons.Sharp.AddAlert,
                        modifier = Modifier.weight(1f)
                    )
                }
                // endregion

                // region: Duration and End Time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CustomTextField(
                        value = duration.toString(),
                        onValueChange = {
                            if (it.isEmpty()) {
                                duration = 1L  // Set duration to zero if the input is empty
                            } else {
                                try {
                                    duration = it.toLong()
                                } catch (e: NumberFormatException) {
                                    duration = 1L  // Handle invalid input gracefully by setting duration to zero
                                    Toast.makeText(context, "Invalid duration format", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        label = "Duration",
                        leadingIcon = Icons.Sharp.Timer,
                        modifier = Modifier.weight(1f)
                    )

                    CustomTextField(
                        value = timeToString(endTime),
                        onValueChange = { endTime = stringToTime(it) },
                        label = "End Time",
                        leadingIconResId = R.drawable.arrowrighttoleft,
                        modifier = Modifier.weight(1f)
                    )
                }
                // endregion

                // region: Task type dropdown
                SelectTaskTypeDropdown(
                    selectedTaskType = taskType,
                    onTaskTypeSelected = { taskType = it }
                )

                // Show the main task dropdown only if task type is "HelperTask"
                if (taskType == TaskTypes.HELPER) {
                    ShowMainTasksDropdown(
                        mainTasks = mainTasks,
                        selectedMainTaskId = linkedMainTaskIdIfHelper,
                        onTaskSelected = { taskId -> linkedMainTaskIdIfHelper = taskId }
                    )
                }

                // endregion

                // region: Recurrence checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Recurring Task?")
                    Switch(
                        checked = isRecurring,
                        onCheckedChange = { isRecurring = it }
                    )
                }
                // endregion

                // region: If recurring, show recurrence fields
                if (isRecurring) {
                    // Recurrence type and interval
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CustomTextField(
                            value = recurrenceType.name, // Convert the enum value to a string
                            onValueChange = {
                                val newType = try {
                                    // Convert input to uppercase and find the matching enum
                                    RecurrenceType.valueOf(it.uppercase())
                                } catch (e: IllegalArgumentException) {
                                    RecurrenceType.DAILY // Default to DAILY if input is invalid
                                }
                                recurrenceType = newType
                            },
                            label = "Recurrence Type",
                            placeholder = "e.g., DAILY, WEEKLY",
                            modifier = Modifier.weight(1f)
                        )

                        CustomTextField(
                            value = recurrenceInterval.toString(),
                            onValueChange = { recurrenceInterval = it.toInt() },
                            label = "Interval",
                            placeholder = "Enter interval (1 for daily)",
                            modifier = Modifier.weight(1f)
                        )
                    }


                    // Recurrence end date
                    CustomTextField(
                        value = recurrenceEndDate?.toString() ?: "",
                        onValueChange = { recurrenceEndDate = LocalDate.parse(it) },
                        label = "Recurrence End Date (optional)",
                        placeholder = "Enter end date",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // endregion

                // Notes input
                CustomTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "Notes",
                    modifier = Modifier.fillMaxWidth()
                )

                CustomTextField(
                    value = taskPosition.toString(),
                    onValueChange = { },
                    label = "Position (Don't Change) (Only for dev)",
                    placeholder = "Internal purposes. Don't change.",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )

                // Add/Cancel buttons
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = onCancel) { Text("Cancel") }
                    Button(onClick = {
                        Log.d(tag, "New Task Confirm clicked on AddTaskDialog")
                        val newTaskFormData = TaskFormData(
                            name = taskName,
                            startTime = startTime,
                            endTime = endTime,
                            notes = notes,
                            taskType = taskType,
                            position = taskPosition,
                            duration = duration.toInt(),
                            reminder = reminder,
                            mainTaskId = linkedMainTaskIdIfHelper,
                            startDate = selectedDate,
                            isRecurring = isRecurring,
                            recurrenceType = recurrenceType.takeIf { isRecurring }, // Set only if recurring
                            recurrenceInterval = recurrenceInterval.takeIf { isRecurring },
                            recurrenceEndDate = recurrenceEndDate.takeIf { isRecurring }
                        )
                        onConfirm(newTaskFormData)
                    }) { Text("Add Task") }
                }
            }
        }
    }
}
