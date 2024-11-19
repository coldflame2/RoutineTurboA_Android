package com.app.routineturboa.ui.tasks.form

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.sharp.AddAlert
import androidx.compose.material.icons.sharp.Start
import androidx.compose.material.icons.sharp.Timer
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.routineturboa.core.models.DataOperationEvents
import com.app.routineturboa.data.room.entities.TaskEntity
import com.app.routineturboa.core.models.StateChangeEvents
import com.app.routineturboa.core.models.TaskOperationState
import com.app.routineturboa.ui.tasks.dropdowns.MainTasksListDropdown
import com.app.routineturboa.ui.tasks.dropdowns.SelectRecurrenceTypeDropdown
import com.app.routineturboa.ui.tasks.fields.CustomTextField
import com.app.routineturboa.ui.tasks.dropdowns.SelectTaskTypeDropdown
import com.app.routineturboa.ui.tasks.pickers.TimePickerField
import com.app.routineturboa.ui.tasks.childItems.PrimaryTaskView
import com.app.routineturboa.core.dbutils.TaskTypes
import com.app.routineturboa.core.dbutils.TaskTypes.utilsGetColor
import com.app.routineturboa.ui.theme.LocalCustomColors
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullEditForm(
    taskInEdit: TaskEntity,
    onStateChangeEvents: StateChangeEvents,
    onDataOperationEvents: DataOperationEvents,
    taskOperationState: TaskOperationState,
) {
    val tag = "NewTaskCreationScreen"
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val customColors = LocalCustomColors.current
    val cardBgColor = utilsGetColor(customColors, taskInEdit.type)

    // region: State variables for new task details

    val initialFormData = (taskOperationState as? TaskOperationState.FillingDetails)?.formData ?: return

    var taskName by remember { mutableStateOf(initialFormData.name) }
    var startTime by remember { mutableStateOf(initialFormData.startTime) }
    var endTime by remember { mutableStateOf(initialFormData.endTime) }
    var notes by remember { mutableStateOf(initialFormData.notes) }
    var durationLong by remember { mutableStateOf(initialFormData.duration) }
    var reminder by remember { mutableStateOf(initialFormData.reminder) }
    var taskType by remember { mutableStateOf(initialFormData.type) }
    var linkedMainIfHelper by remember { mutableStateOf(initialFormData.linkedMainIfHelper) }
    val taskPosition by remember { mutableStateOf(initialFormData.position) }
    val startDate by remember { mutableStateOf(initialFormData.startDate) }
    var isRecurring by remember { mutableStateOf(initialFormData.isRecurring) }
    var recurrenceType by remember { mutableStateOf(initialFormData.recurrenceType) }
    var recurrenceInterval by remember { mutableStateOf(initialFormData.recurrenceInterval) }
    var recurrenceEndDate by remember { mutableStateOf(initialFormData.recurrenceEndDate) }

    // endregion

    // region: variables to hold error messages
    var taskNameError by remember { mutableStateOf<String?>(null) }
    var durationError by remember { mutableStateOf<String?>(null) }
    var taskTypeError by remember { mutableStateOf<String?>(null) }
    var recurrenceIntervalError by remember { mutableStateOf<String?>(null) }
    var recurrenceEndDateError by remember { mutableStateOf<String?>(null) }
    var recurrenceTypeError by remember { mutableStateOf<String?>(null) }
    // endregion

    val isInputDataValid by remember {
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
            val isDurationValid = run {
                val localDuration = durationLong // Store the value in a local variable
                if (localDuration != null && localDuration > 0) {
                    durationError = null
                    true
                } else {
                    durationError = "Error in duration"
                    false

                }
            }
            val isEndTimeValid = endTime != null && startTime != null
            val isTaskTypeValid = if (taskType != TaskTypes.UNDEFINED) {
                taskTypeError = null
                true
            } else {
                taskTypeError = "TaskType Cannot be Undefined"
                false
            }
            val isHelperTaskValid = if (taskType == TaskTypes.HELPER) {
                linkedMainIfHelper != null
            } else { true }



            val isRecurrenceValid = run {
                if (isRecurring) {
                    val isRecurrenceTypeValid = recurrenceType != null
                    val isIntervalValid = recurrenceInterval != null && recurrenceInterval!! > 0
                    val isEndDateValid = recurrenceEndDate == null || recurrenceEndDate!!.isAfter(startDate)

                    // Set specific error messages for each field if needed
                    if (!isRecurrenceTypeValid) {
                        recurrenceTypeError = "Please select a recurrence type"
                    }
                    if (!isIntervalValid) {
                        recurrenceIntervalError = "Interval must be a positive number"
                    }
                    if (!isEndDateValid) {
                        recurrenceEndDateError = "End date must be after start date or left blank"
                    }

                    // Combine all recurrence validations
                    isRecurrenceTypeValid && isIntervalValid && isEndDateValid
                } else {
                    // If not recurring, validation is true by default
                    true
                }
            }


            Log.d("Validation", "TaskName Valid: $isTaskNameValid, StartTime Valid: $isStartTimeValid, Duration Valid: $isDurationValid, EndTime Valid: $isEndTimeValid, TaskType Valid: $isTaskTypeValid, HelperTask Valid: $isHelperTaskValid, Recurrence Valid: $isRecurrenceValid")

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

    BottomSheetScaffold(
        // Cancel and Confirm Buttons
        sheetContent = {
            // Add/Cancel buttons
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(top=0.dp, bottom=0.dp).height(100.dp)
            ) {
                // Cancel Button
                Button(
                    onClick = { onStateChangeEvents.onDismissOrReset(taskInEdit) }
                ) {
                    Text("Cancel")
                }

                Button(
                    enabled = isInputDataValid,

                    onClick = {
                        Log.d(tag, "New Task Confirm clicked on AddTaskDialog")

                        coroutineScope.launch {
                            val updatedFormData = initialFormData.copy(
                                name = taskName,
                                startTime = startTime,
                                endTime = endTime,
                                notes = notes,
                                type = taskType,
                                position = taskPosition,
                                duration = durationLong,
                                reminder = reminder,
                                linkedMainIfHelper = linkedMainIfHelper,
                                startDate = startDate,
                                isRecurring = isRecurring,
                                recurrenceType = recurrenceType.takeIf { isRecurring }, // Set only if recurring
                                recurrenceInterval = recurrenceInterval.takeIf { isRecurring },
                                recurrenceEndDate = recurrenceEndDate.takeIf { isRecurring }
                            )
                            onDataOperationEvents.onUpdateTaskConfirmClick(
                                updatedFormData,
                            )
                        }
                    }
                ) { Text("Confirm Changes") }
            }
        },
        sheetPeekHeight = 60.dp // Adjust height to fit your buttons
    ) {
        Surface(
            modifier = Modifier
                .padding(bottom=90.dp)
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            Column{
                // region: Reference clickedTask
                Card(
                    // region: card properties
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .height(60.dp), // animated height
                    colors = CardDefaults.cardColors(containerColor = cardBgColor.copy(alpha = 0.6f)),
                    shape = RectangleShape,
                    // endregion
                ) {

                    Row {
                        Spacer(modifier = Modifier.width(5.dp))

                        PrimaryTaskView(
                            task = taskInEdit,
                            stateChangeEvents = onStateChangeEvents,
                            dataOperationEvents = onDataOperationEvents,
                            cardHeight = 60.dp,
                            topPadding = 8.dp,
                            bgColor = cardBgColor,
                            forReferenceView = true,
                        )
                    }
                    // endregion
                }
                //endregion

                // Task details input
                Column{
                    // region: Task name input
                    CustomTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = taskName,
                        onValueChange = { taskName = it },
                        label = "Name",
                        placeholder = "Enter task name",
                        leadingIcon = Icons.Default.AddTask,
                        errorMessage = taskNameError
                    )
                    // endregion

                    // region: Start Time and reminders
                    Row {
                        TimePickerField(
                            value = startTime,
                            onValueChange = {
                                Log.d(tag, "Clicked")
                                startTime = it
                            },
                            label = "Start",
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
                            modifier = Modifier.weight(1f),
                            value = durationLong.toString(),
                            onValueChange = { text ->
                                val parsedDuration = text.toLongOrNull()
                                if (parsedDuration != null && parsedDuration > 0) {
                                    startTime?.let { nonNullStartTime ->
                                        // Using `nonNullStartTime` here guarantees that it's not null
                                        durationLong = parsedDuration
                                        endTime = nonNullStartTime.plusMinutes(parsedDuration)
                                        durationError = null
                                    }
                                } else {
                                    durationLong = 0
                                    durationError = "Problem with duration. $parsedDuration"
                                }
                            },
                            label = "Duration",
                            leadingIcon = Icons.Sharp.Timer,
                            keyboardType = KeyboardType.Number,
                            errorMessage = durationError
                        )

                        TimePickerField(
                            value = endTime,
                            onValueChange = { endTime = it },
                            label = "End",
                            leadingIcon = Icons.Default.MoreTime,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // endregion

                    // region: Task type dropdown
                    SelectTaskTypeDropdown(
                        selectedTaskType = taskType,
                        onTaskTypeSelected = { taskType = it },
                        selectedLinkedMainTask = linkedMainIfHelper,
                        onLinkedMainTaskSelected = {linkedMainIfHelper = it},
                        taskTypeErrorMessage = taskTypeError
                    )
                    // endregion

                    if (taskType == TaskTypes.HELPER) {
                        MainTasksListDropdown(
                            mainTasksRequested = onDataOperationEvents.onMainTasksRequested,
                            selectedLinkedMainTask = linkedMainIfHelper,
                            onLinkedMainTaskSelected = { taskId ->
                                linkedMainIfHelper = taskId
                            }
                        )
                    }

                    // region: Notes (and Position if required)
                    CustomTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = notes,
                        onValueChange = { notes = it },
                        label = "Notes"
                    )
                    // endregion


                    // region: Recurrence related fields
                    Box (
                        modifier = Modifier
                            .border(BorderStroke(if (isRecurring) 2.dp else 0.dp, Color.LightGray))
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 15.dp, bottom = 10.dp),
                    ) {
                        Column {
                            // Recurrence toggle
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Recurring Task?",
                                    fontSize = 16.sp,
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Box(
                                    modifier = Modifier
                                        .size(width = 50.dp, height = 25.dp) // Set your desired size
                                        .background(
                                            color = if (isRecurring) Color.Magenta.copy(alpha = 0.3f) else Color.Gray,
                                            shape = RoundedCornerShape(12.5.dp) // Half of the height for a pill shape
                                        )
                                        .toggleable(
                                            value = isRecurring,
                                            onValueChange = { isRecurring = it },
                                            indication = rememberRipple(
                                                bounded = false,
                                                color = Color.White,
                                                radius = 25.dp, // Adjust radius to match your component
                                            ),
                                            interactionSource = remember { MutableInteractionSource() }
                                        )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(21.dp)
                                            .align(if (isRecurring) Alignment.CenterEnd else Alignment.CenterStart)
                                            .padding(2.dp)
                                            .background(
                                                color = Color.White,
                                                shape = CircleShape
                                            )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(5.dp))

                            // recurrence fields (if recurring)
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

                                    Spacer(modifier = Modifier.width(5.dp))

                                    // Recurrence interval
                                    Box(modifier = Modifier.weight(0.5f)) {
                                        CustomTextField(
                                            value = recurrenceInterval.toString(),
                                            onValueChange = { recurrenceInterval = it.toInt() },
                                            label = "Interval",
                                            placeholder = "Enter interval (1 for daily)"
                                        )
                                    }
                                }


                                // Recurrence end date
                                CustomTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = recurrenceEndDate?.toString() ?: "",
                                    onValueChange = { recurrenceEndDate = LocalDate.parse(it) },
                                    label = "Recurrence End Date (optional)",
                                    placeholder = "Enter end date"
                                )
                            }
                        }

                        //  Position (for dev)
//                CustomTextField(
//                    modifier = Modifier.fillMaxWidth(),
//                    value = taskPosition.toString(),
//                    onValueChange = { },
//                    label = "Position (for dev)",
//                    placeholder = "For development purposes.",
//                    enabled = false
//                )
                    }

                    // endregion

                }
            }

        }
    }


}
