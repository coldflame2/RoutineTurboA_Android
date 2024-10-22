package com.app.routineturboa.ui.main

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.app.routineturboa.data.repository.TaskOperationResult

import com.app.routineturboa.data.room.TaskEntity
import com.app.routineturboa.data.room.TaskCompletionHistory
import com.app.routineturboa.shared.DataOperationEvents
import com.app.routineturboa.utils.TaskTypes

import com.app.routineturboa.shared.StateChangeEvents
import com.app.routineturboa.shared.TasksBasedOnState
import com.app.routineturboa.shared.UiStates
import com.app.routineturboa.ui.reusable.SuccessIndicator
import com.app.routineturboa.ui.tasks.ParentTaskItem
import com.app.routineturboa.ui.tasks.dialogs.NewTaskCreationScreen
import com.app.routineturboa.ui.tasks.dialogs.TaskCompletionDialog
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.S)
@Composable
// Usage: MainScreen
fun TasksLazyColumn(
    paddingValues: PaddingValues, // auto-calculated by Scaffold
    tasksByDate: List<TaskEntity>,
    tasksCompleted: List<TaskCompletionHistory>,
    selectedDate: LocalDate?,
    tasksBasedOnState: TasksBasedOnState,
    uiStates: UiStates,
    stateChangeEvents: StateChangeEvents,
    dataOperationEvents: DataOperationEvents,
) {
    val tag = "TasksLazyColumn"
    val coroutineScope = rememberCoroutineScope()
    val newTaskAdditionResult = remember {
        mutableStateOf<Result<TaskOperationResult>?>(null)
    }
    val mainTasks by remember(tasksByDate) {
        mutableStateOf(tasksByDate.filter { it.type == TaskTypes.MAIN })
    }

    val clickedTask = tasksBasedOnState.clickedTask
    val taskBelowClickedTask = tasksBasedOnState.taskBelowClickedTask

    val isAddNewTaskAllowed = remember(uiStates, tasksByDate) {
        uiStates.isAddingNew &&
        clickedTask != null &&
        taskBelowClickedTask != null
    }

    val newlyAddedTaskId = remember { mutableIntStateOf(-1) }

    // LazyColumn for displaying tasks
    LazyColumn(
        modifier = Modifier.padding(paddingValues),  // Use paddingValues passed from MainScreen here
        contentPadding = PaddingValues(0.dp, 1.dp, 10.dp, 0.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Show ParentTaskItem for each task in the list
        if (tasksByDate.isNotEmpty()) {
            items(tasksByDate, key = { task -> task.id }) { task ->
                ParentTaskItem(
                    task = task,
                    mainTasks = mainTasks,
                    tasksBasedOnState = tasksBasedOnState,
                    uiStates = uiStates,
                    stateChangeEvents = stateChangeEvents,
                    dataOperationEvents = dataOperationEvents,
                    newTaskAdditionResult = newTaskAdditionResult,
                    newlyAddedTaskId = newlyAddedTaskId
                )

                // region: Space at the bottom used as visible underline for clicked task
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp) // Thickness of the line
                        .padding(horizontal = 0.dp) // padding for the line
                        .background(
                            if (tasksBasedOnState.clickedTask?.id == task.id) Color.Blue.copy(alpha=0.6f)
                            else Color.Transparent
                        )
                )
                // endregion
            }

        }

        // Show message if no tasks exist for the selected date
        else {
            item {
                Text(
                    text = "No tasks for this date",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

// Show NewTaskCreationScreen
    if (isAddNewTaskAllowed) {
        val boxColor = Color.Black.copy(alpha = 0.3f)

        // ensure again that clickedTask and taskBelowClickedTask are non-null
        clickedTask?.let { nonNullClickedTask ->
            taskBelowClickedTask?.let { nonNullTaskBelowClickedTask ->
                Row(
                    modifier = Modifier
                        .background(boxColor)
                        .padding(paddingValues) // Apply the padding here to avoid overlap
                ) {
                    NewTaskCreationScreen(
                        clickedTask = nonNullClickedTask,
                        selectedDate = selectedDate,
                        mainTasks = mainTasks,
                        onCancel = { stateChangeEvents.onCancelClick() },
                        onConfirm = { newTaskFormData ->
                            coroutineScope.launch {
                                newTaskAdditionResult.value =
                                    dataOperationEvents.onNewTaskConfirmClick(
                                        newTaskFormData,
                                        nonNullClickedTask,
                                        nonNullTaskBelowClickedTask
                                    )
                            }
                        },
                    )
                }
            }
        }
    }


    // set value for success indicator state
    if (newTaskAdditionResult.value?.isSuccess == true) {
        val result = newTaskAdditionResult.value

        result?.fold(
            onSuccess = { taskOperationResult ->
                if (taskOperationResult.success) {
                    val newTaskId = taskOperationResult.newTaskId
                    if (newTaskId != null) {
                        newlyAddedTaskId.intValue = newTaskId
                    }
                    Log.d(tag, "Task confirmed successfully with ID: $newlyAddedTaskId")

                } else {
                    Log.d(tag, "Task confirmed but marked as not successful")
                }
            },

            onFailure = { exception ->
                Log.e(tag, "Failed to confirm task: ${exception.message}")
                Toast.makeText(
                    LocalContext.current,
                    "New Task ID: {",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        )
    }

    // Success Indicator for the new task
    if (newlyAddedTaskId.intValue != -1) {
        Box(
            modifier = Modifier
                .fillMaxSize(), // Make the Box fill the available space
            contentAlignment = Alignment.Center // Center the content inside the Box
        ) {
            SuccessIndicator(onReset = {
                newTaskAdditionResult.value = null
                newlyAddedTaskId.intValue = -1
            })
        }
    }

    // Show completed tasks dialog
    if (uiStates.isShowingCompletedTasks) {
        TaskCompletionDialog(tasksCompleted, stateChangeEvents.onCancelClick)
    }
}