package com.app.routineturboa.ui.main

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

import com.app.routineturboa.data.room.TaskEntity
import com.app.routineturboa.data.room.TaskCompletionHistory
import com.app.routineturboa.utils.TaskTypes

import com.app.routineturboa.ui.models.TaskEventsToFunctions
import com.app.routineturboa.ui.models.TasksUiState
import com.app.routineturboa.ui.tasks.ParentTaskItem
import com.app.routineturboa.ui.tasks.dialogs.AddTaskDialog
import com.app.routineturboa.ui.reusable.EmptyTaskCardPlaceholder
import com.app.routineturboa.ui.tasks.dialogs.TaskCompletionDialog

@RequiresApi(Build.VERSION_CODES.S)
@Composable
// Usage: MainScreen
fun TasksLazyColumn(
    paddingValues: PaddingValues, // auto-calculated by Scaffold
    tasks: List<TaskEntity>,
    tasksCompleted: List<TaskCompletionHistory>,
    tasksUiState: TasksUiState,
    taskEventsToFunctions: TaskEventsToFunctions
) {
    val tag = "TasksLazyColumn"

    val mainTasks by remember(tasks) { mutableStateOf(tasks.filter { it.type == TaskTypes.MAIN }) }

    LazyColumn(
        modifier = Modifier.padding(paddingValues),  // Use paddingValues passed from MainScreen here
        contentPadding = PaddingValues(0.dp, 5.dp, 15.dp, 0.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        if (tasks.isNotEmpty()) {
            items(tasks, key = { task -> task.id }) { task ->
                ParentTaskItem(
                    task = task,
                    mainTasks = mainTasks,
                    tasksUiState = tasksUiState,
                    taskEventsToFunctions = taskEventsToFunctions
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


    // AddTaskDialog (Inside the parent Box)
    if (tasksUiState.isAddingNew && tasksUiState.clickedTaskId != null) {
        val clickedTask = tasks.find { it.id == tasksUiState.clickedTaskId }
        val taskBelowClickedTask = tasks.find {it.id == tasksUiState.taskBelowClickedTaskId}

        val boxColor = Color.Black.copy(alpha = 0.3f)

        Box(modifier = Modifier.background(boxColor)) {
            if (clickedTask != null){
                AddTaskDialog(
                    mainTasks = mainTasks,
                    clickedTask = clickedTask,
                    taskBelowClickedTask = taskBelowClickedTask,
                    onCancel = { taskEventsToFunctions.onCancelClick() },
                    onAddClick = { newTaskFormData ->
                        taskEventsToFunctions.onNewTaskSaveClick(newTaskFormData)
                    },
                )
            } else {
                Toast.makeText(
                    LocalContext.current,
                    "Select a task first", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    if (tasksUiState.isShowingCompletedTasks) {
        TaskCompletionDialog(tasksCompleted, taskEventsToFunctions.onCancelClick)
    }
}