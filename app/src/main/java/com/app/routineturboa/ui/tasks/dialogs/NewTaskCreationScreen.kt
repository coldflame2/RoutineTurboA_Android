package com.app.routineturboa.ui.tasks.dialogs

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.Assignment
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.sharp.AddAlert
import androidx.compose.material.icons.sharp.Start
import androidx.compose.material.icons.sharp.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.app.routineturboa.data.dbutils.RecurrenceType
import com.app.routineturboa.data.room.entities.TaskEntity
import com.app.routineturboa.shared.events.StateChangeEvents
import com.app.routineturboa.ui.models.TaskFormData
import com.app.routineturboa.ui.reusable.dropdowns.SelectRecurrenceTypeDropdown
import com.app.routineturboa.ui.reusable.fields.CustomTextField
import com.app.routineturboa.ui.reusable.dropdowns.SelectTaskTypeDropdown
import com.app.routineturboa.ui.reusable.dropdowns.ShowMainTasksDropdown
import com.app.routineturboa.ui.reusable.pickers.TimePickerField
import com.app.routineturboa.ui.tasks.childItems.HourColumn
import com.app.routineturboa.ui.tasks.childItems.PrimaryTaskView
import com.app.routineturboa.ui.theme.LocalCustomColors
import com.app.routineturboa.utils.TaskTypes
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun NewTaskCreationScreen(
    paddingValues: PaddingValues,
    clickedTask: TaskEntity?,
    taskBelowClickedTask: TaskEntity?,
    selectedDate: LocalDate?,
    mainTasks: List<TaskEntity>,
    stateChangeEvents: StateChangeEvents,
    onConfirm: (TaskEntity, TaskEntity, TaskFormData) -> Unit,
) {
    val tag = "NewTaskCreationScreen"
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val nonNullClickedTask = clickedTask ?: return
    val nonNullTaskBelowClickedTask = taskBelowClickedTask ?: return
    val context = LocalContext.current

    val isInFocus = remember { mutableStateOf(false) }

    val customColors = LocalCustomColors.current
    val cardBgColor = remember {
        when (nonNullClickedTask.type) {
            TaskTypes.MAIN -> customColors.mainTaskColor
            TaskTypes.BASICS -> customColors.basicsTaskColor
            TaskTypes.HELPER -> customColors.helperTaskColor
            TaskTypes.QUICK -> customColors.quickTaskColor
            TaskTypes.UNDEFINED -> customColors.undefinedTaskColor
            else -> customColors.gray300
        }
    }


    // region: State variables for new task details
    var taskName by remember { mutableStateOf("New Task") }
    val clickedTaskEndTime = nonNullClickedTask?.endTime
    var startTime by remember { mutableStateOf(clickedTaskEndTime) }
    val defaultDuration = 1L
    val newEndTime = startTime?.plusMinutes(defaultDuration) ?: LocalTime.now()
    var endTime by remember { mutableStateOf(newEndTime)}
    var notes by remember { mutableStateOf("") }

    var durationLong by remember { mutableLongStateOf(defaultDuration) }
    var durationString by remember { mutableStateOf(durationLong.toString()) }

    var reminder by remember { mutableStateOf(startTime) }
    var taskType by remember { mutableStateOf(TaskTypes.QUICK) }
    val taskPosition by remember {mutableIntStateOf(nonNullClickedTask?.position?.plus(1) ?: 2)}

    // Recurrence-related state variables
    var isRecurring by remember { mutableStateOf(false) }
    var recurrenceType by remember { mutableStateOf(RecurrenceType.DAILY) } // Default to 'DAILY'
    var recurrenceInterval by remember { mutableIntStateOf(1) }
    var recurrenceEndDate by remember { mutableStateOf<LocalDate?>(null) }

    // State for linked main task if this is a helper task
    var linkedMainTaskIdIfHelper by remember { mutableStateOf<Int?>(null) }

    val newTaskFormData = remember {
        mutableStateOf(
            TaskFormData(
                name = taskName,
                startTime = startTime,
                endTime = endTime,
                notes = notes,
                taskType = taskType,
                position = taskPosition,
                duration = durationLong.toInt(),
                reminder = reminder,
                mainTaskId = linkedMainTaskIdIfHelper,
                startDate = selectedDate,
                isRecurring = isRecurring,
                recurrenceType = recurrenceType.takeIf { isRecurring },
                recurrenceInterval = recurrenceInterval.takeIf { isRecurring },
                recurrenceEndDate = recurrenceEndDate
            )
        )
    }

    var taskNameError by remember { mutableStateOf<String?>(null) }
    var durationError by remember { mutableStateOf<String?>(null) }
    var taskTypeError by remember { mutableStateOf<String?>(null) }


    val isAddingEnabled by remember {
        derivedStateOf {
            // Validation rules
            val isTaskNameValid = if (taskName.isNotBlank()) {
                taskNameError = null
                true
            } else {
                taskNameError = "Task name cannot be empty"
                false
            }
            val isStartTimeValid = startTime != null
            val isDurationValid = if (durationLong > 0) {
                durationError = null
                true
            } else {
                durationError = "Duration must be greater than 0"
                false
            }
            val isEndTimeValid = endTime != null && startTime != null && endTime.isAfter(startTime)
            val isTaskTypeValid = if (taskType != TaskTypes.UNDEFINED) {
                taskTypeError = null
                true
            } else {
                taskTypeError = "TaskType Cannot be Undefined"
                false
            }
            val isHelperTaskValid = if (taskType == TaskTypes.HELPER) {
                linkedMainTaskIdIfHelper != null
            } else { true }
            val isRecurrenceValid = if (isRecurring) {
                recurrenceInterval > 0 &&
                        (recurrenceEndDate == null)
            } else { true }

            // Combine all validations
            isTaskNameValid &&
                isStartTimeValid &&
                isDurationValid &&
                isEndTimeValid &&
                isTaskTypeValid &&
                isHelperTaskValid &&
                isRecurrenceValid
        }
    }





    // endregion

    Surface(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxWidth()
            .verticalScroll(scrollState),
    ) {
        // Reference clicked Card
        Column {
            Card(
                // region: card properties
                modifier = Modifier
                    .padding(end = 10.dp)
                    .height(60.dp), // animated height
                colors = CardDefaults.cardColors(containerColor = cardBgColor.copy(alpha = 0.6f)),
                shape = RectangleShape,
                // endregion
            ) {
                // region: Reference clickedTask

                Row {
                    Spacer(modifier = Modifier.width(5.dp))

                    HourColumn(
                        // region: arguments
                        startTime = nonNullClickedTask.startTime,
                        duration = nonNullClickedTask.duration,
                        isThisTaskClicked = false,
                        isCurrentTask = false,
                        height = 60.dp,
                        topPadding = 8.dp
                        //endregion
                    )

                    PrimaryTaskView(
                        task = nonNullClickedTask,
                        isThisTaskClicked = false,
                        stateChangeEvents = stateChangeEvents,
                        cardHeight = 60.dp,
                        topPadding = 8.dp,
                        forReferenceView = true,
                        bgColor = cardBgColor
                    )
                }
                // endregion
            }

            // Task details input
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // region: Task name input
                CustomTextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = "Name",
                    isInFocus = isInFocus,
                    placeholder = "Enter task name",
                    leadingIcon = Icons.Default.AddTask,
                    modifier = Modifier.fillMaxWidth(),
                    errorMessage = taskNameError
                )
                // endregion

                // region: Start Time and reminders
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimePickerField(
                        value = startTime,
                        onValueChange = {
                            Log.d(tag, "Clicked")
                            startTime = it
                        },
                        label = "Start Time",
                        leadingIcon = Icons.Sharp.Start,
                        modifier = Modifier.weight(1f)
                    )

                    TimePickerField(
                        value = reminder,
                        onValueChange = { reminder = it },
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
                        value = durationString,

                        onValueChange = { text ->
                            durationString = text

                            // Safely attempt to parse the input to a Long
                            val parsedDuration = text.toLongOrNull()
                            if (parsedDuration != null && parsedDuration > 0 && startTime != null) {
                                // Update duration and endTime only if input is valid
                                durationLong = parsedDuration
                                endTime = startTime!!.plusMinutes(durationLong)

                            } else {
                                durationLong = 0
                                Log.e(tag, "Problem with duration. $parsedDuration")
                            }
                            // Else, do not update duration or endTime to prevent unexpected behavior
                        },

                        label = "Duration",
                        isInFocus = isInFocus,
                        leadingIcon = Icons.Sharp.Timer,
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f),
                        errorMessage = durationError
                    )

                    TimePickerField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = "End Time",
                        leadingIcon = Icons.Default.MoreTime,
                        modifier = Modifier.weight(1f)
                    )
                }

                // endregion

                // region: Task type dropdown
                SelectTaskTypeDropdown(
                    selectedTaskType = taskType,
                    onTaskTypeSelected = { taskType = it },
                    leadingIcon = Icons.AutoMirrored.Sharp.Assignment,
                    taskTypeErrorMessage = taskTypeError
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
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // region: Recurrence type dropdown
                        Box(modifier = Modifier.weight(1f)) {
                            SelectRecurrenceTypeDropdown(
                                selectedRecurrenceType = recurrenceType,
                                onRecurrenceTypeSelected = { recurrenceType = it }
                            )
                        }
                        // endregion

                        Box(modifier = Modifier.weight(0.5f)) {
                            CustomTextField(
                                value = recurrenceInterval.toString(),
                                onValueChange = { recurrenceInterval = it.toInt() },
                                label = "Interval",
                                isInFocus = isInFocus,
                                placeholder = "Enter interval (1 for daily)"
                            )
                        }
                    }





                    // Recurrence end date
                    CustomTextField(
                        value = recurrenceEndDate?.toString() ?: "",
                        onValueChange = { recurrenceEndDate = LocalDate.parse(it) },
                        label = "Recurrence End Date (optional)",
                        isInFocus = isInFocus,
                        placeholder = "Enter end date",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // endregion

                // region: Notes and Position
                CustomTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "Notes",
                    isInFocus = isInFocus,
                    modifier = Modifier.fillMaxWidth()
                )

                CustomTextField(
                    value = taskPosition.toString(),
                    onValueChange = { },
                    label = "Position (Don't Change) (Only for dev)",
                    placeholder = "Internal purposes. Don't change.",
                    modifier = Modifier.fillMaxWidth(),
                    isInFocus = isInFocus,
                    enabled = false
                )
                // endregion

                // Add/Cancel buttons
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Cancel Button
                    Button(onClick = stateChangeEvents.onCancelClick) {
                        Text("Cancel")
                    }

                    Button(
                        enabled = isAddingEnabled,
                        onClick = {
                        Log.d(tag, "New Task Confirm clicked on AddTaskDialog")
                        newTaskFormData.value = TaskFormData(
                            name = taskName,
                            startTime = startTime,
                            endTime = endTime,
                            notes = notes,
                            taskType = taskType,
                            position = taskPosition,
                            duration = durationLong.toInt(),
                            reminder = reminder,
                            mainTaskId = linkedMainTaskIdIfHelper,
                            startDate = selectedDate,
                            isRecurring = isRecurring,
                            recurrenceType = recurrenceType.takeIf { isRecurring }, // Set only if recurring
                            recurrenceInterval = recurrenceInterval.takeIf { isRecurring },
                            recurrenceEndDate = recurrenceEndDate.takeIf { isRecurring }
                        )
                        Log.d(tag, "${newTaskFormData.value}")
                        onConfirm(
                            nonNullClickedTask,
                            nonNullTaskBelowClickedTask,
                            newTaskFormData.value,
                        )
                    }) { Text("Add") }
                }
            }
        }

    }
}
