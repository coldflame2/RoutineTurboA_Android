package com.app.routineturboa.ui.main

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.routineturboa.reminders.ReminderManager
import com.app.routineturboa.ui.task.dialogs.AddTaskDialog
import com.app.routineturboa.ui.components.EmptyTaskCardPlaceholder
import com.app.routineturboa.ui.task.ParentTaskItem
import com.app.routineturboa.viewmodel.TasksViewModel
import com.microsoft.identity.client.IAuthenticationResult
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun TasksLazyColumn(
    paddingValues: PaddingValues,
    tasksViewModel: TasksViewModel,
    reminderManager: ReminderManager,

    clickedTaskId: MutableState<Int?>,
    isAddingTask: MutableState<Boolean>,
    editingTaskId: MutableState<Int?>,
    isQuickEditing: MutableState<Boolean>,
    isFullEditing: MutableState<Boolean>,
    isAnotherTaskEditing: MutableState<Boolean>,
) {
    // <editor-fold desc="variables">
    val tag = "TasksLazyColumn"

    val tasks by tasksViewModel.tasks.collectAsStateWithLifecycle()
    var areTasksLoading by remember { mutableStateOf(true) }
    val oneDriveAuthResult by remember { mutableStateOf<IAuthenticationResult?>(null) }
    val listState = rememberLazyListState()
    val hasScrolledToTarget = remember { mutableStateOf(false) }


    // Find the index of the task whose time range includes the current time
    val targetIndex = tasks.indexOfFirst { task ->
        val currentDateTime = remember { java.time.LocalDateTime.now() }
        val startTime = task.startTime
        val endTime = task.endTime
        currentDateTime.isAfter(startTime) && currentDateTime.isBefore(endTime)
    }

    // </editor-fold variables>

    LaunchedEffect(tasks, listState) {
        Log.d(tag, "Checking conditions for scrolling")
        if (tasks.isNotEmpty() && targetIndex != -1) {
            Log.d(tag, "Conditions met, attempting to scroll to $targetIndex")
            if (!hasScrolledToTarget.value) {
                delay(2000)
                listState.animateScrollToItem(targetIndex)
                hasScrolledToTarget.value = true
                Log.d(tag, "Scrolled to $targetIndex")
            }
        }
    }


    LaunchedEffect(oneDriveAuthResult, key2 = true) {
        Log.d(tag, "TasksScreen LaunchedEffect called")
        oneDriveAuthResult?.let {
            Log.d(tag, "Downloading from OneDrive")
        }
    }

    // Start a coroutine to delay the loading state change
    LaunchedEffect(tasks) {
        delay(50)
        areTasksLoading = false
    }

    // <editor-fold desc="Lazy Column-TaskCard">
    LazyColumn(
        state = listState,
        modifier = Modifier.padding(paddingValues),
        contentPadding = PaddingValues(bottom = 50.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Loading state or no tasks
        if (areTasksLoading || tasks.isEmpty()) {
            items(2) {
                EmptyTaskCardPlaceholder()
            }
        }
        else {
            items( tasks, key = { task -> task.id }) { task ->
                // Determine if another task is being edited
                isAnotherTaskEditing.value = (isQuickEditing.value || isFullEditing.value) &&
                        editingTaskId.value != task.id

                ParentTaskItem(
                    // <editor-fold desc="SingleTaskCard Parameters"
                    tasksViewModel = tasksViewModel,
                    reminderManager = reminderManager,

                    task = task,
                    onTaskClick = {
                        // Cancel editing if another task is clicked while a different task is being edited
                        if (isAnotherTaskEditing.value && editingTaskId.value != task.id) {
                            editingTaskId.value = null
                            isQuickEditing.value = false
                            isFullEditing.value = false
                        }

                        clickedTaskId.value = task.id
                    },

                    isThisTaskClicked = task.id == clickedTaskId.value,
                    isThisTaskQuickEditing = isQuickEditing.value && editingTaskId.value == task.id,
                    isThisTaskFullEditing = isFullEditing.value && editingTaskId.value == task.id,
                    isAnotherTaskEditing = isAnotherTaskEditing.value,

                    canDelete = !tasksViewModel.isTaskFirst(task) && !tasksViewModel.isTaskLast(task),
                    onDelete = { tasksViewModel.deleteTask(it) },

                    onStartQuickEdit = {
                        editingTaskId.value = task.id
                        clickedTaskId.value = task.id
                        isQuickEditing.value = true
                        isFullEditing.value = false
                    },

                    onStartFullEdit = {
                        editingTaskId.value = task.id
                        isQuickEditing.value = false
                        isFullEditing.value = true
                    },

                    onEndEditing = {
                        editingTaskId.value = null
                        isQuickEditing.value = false
                        isFullEditing.value = false
                    }

                // </editor-fold>
                )
            }
        }
    }  // end of lazy column
    // </editor-fold>

    Text(
        text = tasks.find { it.id == clickedTaskId.value }?.name ?: "",
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(PaddingValues())
    )

    // AddTaskDialog (Inside the parent Box)
    if (isAddingTask.value && clickedTaskId.value != null) {
        val clickedTask = tasks.find { it.id == clickedTaskId.value }
        val boxColor = Color.Black.copy(alpha = 0.3f)

        Box(modifier = Modifier.background(boxColor)) {
            AddTaskDialog(
                tasksViewModel = tasksViewModel,
                clickedTask = clickedTask,
                onAddClick = { newTask ->
                    tasksViewModel.beginNewTaskOperations(clickedTask!!, newTask)
                    isAddingTask.value = false
                },
                onCancel = { isAddingTask.value = false }
            )
        }
    }
}