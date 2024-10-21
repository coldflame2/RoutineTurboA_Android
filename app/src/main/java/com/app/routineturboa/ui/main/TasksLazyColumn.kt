package com.app.routineturboa.ui.main

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
    tasks: List<TaskEntity>,
    tasksCompleted: List<TaskCompletionHistory>,
    selectedDate: LocalDate?,
    tasksBasedOnState: TasksBasedOnState,
    uiStates: UiStates,
    stateChangeEvents: StateChangeEvents,
    dataOperationEvents: DataOperationEvents,
) {
    val tag = "TasksLazyColumn"
    val coroutineScope = rememberCoroutineScope()
    var confirmationResult = remember { mutableStateOf<Result<TaskOperationResult>?>(null) }

    val mainTasks by remember(tasks) { mutableStateOf(tasks.filter { it.type == TaskTypes.MAIN }) }

    // Determine if adding a new task is allowed
    val isAddNewTaskAllowed = remember(uiStates, tasks) {
        uiStates.isAddingNew && tasksBasedOnState.clickedTask != null
    }

    LazyColumn(
        modifier = Modifier.padding(paddingValues),  // Use paddingValues passed from MainScreen here
        contentPadding = PaddingValues(0.dp, 15.dp, 15.dp, 0.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        if (tasks.isNotEmpty()) {
            items(tasks, key = { task -> task.id }) { task ->
                ParentTaskItem(
                    task = task,
                    mainTasks = mainTasks,
                    tasksBasedOnState = tasksBasedOnState,
                    uiStates = uiStates,
                    stateChangeEvents = stateChangeEvents,
                    dataOperationEvents = dataOperationEvents
                )
            }

        // Show message if no tasks exist for the selected date
        } else {
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


    // NewTaskCreationScreen
    if (isAddNewTaskAllowed) {
        val boxColor = Color.Black.copy(alpha = 0.3f)

        Box(modifier = Modifier.background(boxColor)) {
            if (tasksBasedOnState.clickedTask != null){
                NewTaskCreationScreen(
                    clickedTask = tasksBasedOnState.clickedTask,
                    selectedDate = selectedDate,
                    taskBelowClickedTask = tasksBasedOnState.taskBelowClickedTask,
                    mainTasks = mainTasks,
                    onCancel = { stateChangeEvents.onCancelClick() },
                    onConfirm = { clickedTask, newTaskFormData ->
                        coroutineScope.launch {
                            confirmationResult.value = dataOperationEvents.onNewTaskConfirmClick(clickedTask, newTaskFormData )
                        }
                    },
                )
            } else {
                Toast.makeText(
                    LocalContext.current,
                    "Select a task first", Toast.LENGTH_SHORT
                ).show()
                stateChangeEvents.onCancelClick()
            }
        }
    }

    if (confirmationResult.value?.isSuccess == true){
        val result = confirmationResult.value

        result?.fold(
            onSuccess = { taskOperationResult ->
                if (taskOperationResult.success) {
                    Log.d(tag, "Task confirmed successfully with ID: ${taskOperationResult.newTaskId}")
                    Box(
                        modifier = Modifier
                            .fillMaxSize(), // Takes up the whole screen space
                        contentAlignment = Alignment.BottomEnd// Change this to position it anywhere
                    ) {
                        SuccessIndicator(confirmationResult)
                    }
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

    if (uiStates.isShowingCompletedTasks) {
        TaskCompletionDialog(tasksCompleted, stateChangeEvents.onCancelClick)
    }
}