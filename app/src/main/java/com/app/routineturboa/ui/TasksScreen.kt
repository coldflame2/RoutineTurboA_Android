package com.app.routineturboa.ui

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.routineturboa.data.model.TaskEntity
import com.app.routineturboa.reminders.ReminderManager
import com.app.routineturboa.ui.components.AddTaskScreen
import com.app.routineturboa.ui.components.EditTaskScreen
import com.app.routineturboa.ui.components.SingleTaskCard
import com.app.routineturboa.viewmodel.TasksViewModel
import com.microsoft.identity.client.IAuthenticationResult
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun TasksScreen(context: Context, tasksViewModel: TasksViewModel, reminderManager: ReminderManager) {
    val tag = "TasksScreen"

    val tasks by tasksViewModel.tasks.collectAsStateWithLifecycle()

    var clickedTask by remember { mutableStateOf<TaskEntity?>(null) }
    var taskBeingEdited by remember { mutableStateOf<TaskEntity?>(null) }
    var isAddingTask by remember { mutableStateOf(false) }

    var isTaskLast by remember { mutableStateOf(clickedTask?.let { tasksViewModel.isTaskLast(it) }) }
    var isTaskFirst by remember { mutableStateOf(clickedTask?.let { tasksViewModel.isTaskFirst(it) }) }

    val authenticationResult by remember { mutableStateOf<IAuthenticationResult?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(authenticationResult) {
        Log.d(tag, "TasksScreen LaunchedEffect called")
        authenticationResult?.let { authResult ->
            coroutineScope.launch {
                Log.d(tag, "Downloading from OneDrive")
            }
        }
    }

    Scaffold(
        // Plus/Add Floating Button
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isAddingTask = true },
                modifier = Modifier
                    .padding(end = 15.dp, bottom = 10.dp)
                    .size(60.dp, 60.dp),
                containerColor = Color.Blue.copy(alpha = 0.9f),
                contentColor = Color.White,
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add New Task"
                )
            }
        },

        floatingActionButtonPosition = FabPosition.EndOverlay

    ) { paddingValues ->
        // Tasks list
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .padding(paddingValues),
        ) {
            items(tasks, key = { task ->
                Log.d("TaskID", "Task ID: ${task.id}")
                task.id
            }) { task ->
                SingleTaskCard(
                    task = task,
                    onClick = { clickedTask = task },
                    onEditClick = { taskBeingEdited = it },
                    canDelete = !(isTaskFirst == true || isTaskLast == true),
                    onDelete = { tasksViewModel.deleteTask(it) },
                    isClicked = task == clickedTask
                )
            }
            item {
                Text(text = "${clickedTask?.taskName}")
            }
        }



        // Show the add task screen
        if (isAddingTask) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            ) {
                AddTaskScreen(
                    clickedTask = clickedTask,
                    onSaveNewTask = { newTask: TaskEntity ->
                        Log.d(tag, "Save Button in Add Screen clicked...newTask details: $newTask")
                        tasksViewModel.updateTaskPositions(clickedTask)
                        tasksViewModel.insertTask(newTask)
                        isAddingTask = false
                    },
                    onCancel = { isAddingTask = false }
                )
            }
        }

        // Show the edit task screen as a dialog overlay
        if (taskBeingEdited != null) {
            Log.d(tag, "Show Edit Task Screen")
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.1f))
            ) {
                EditTaskScreen(
                    reminderManager = reminderManager,
                    task = taskBeingEdited!!,
                    onSave = { updatedTask ->
                        taskBeingEdited = null
                        Toast.makeText(context, "Task Edited.", Toast.LENGTH_SHORT).show()
                        coroutineScope.launch {
                            tasksViewModel.updateTask(updatedTask)
                            reminderManager.observeAndScheduleReminders(context)
                        }
                    },
                    onCancel = { taskBeingEdited = null }
                )
            }
        }
    }
}