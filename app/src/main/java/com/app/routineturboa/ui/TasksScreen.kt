package com.app.routineturboa.ui

import TaskViewModelFactory
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.routineturboa.data.local.RoutineRepository
import com.app.routineturboa.data.model.TaskEntity
import com.app.routineturboa.reminders.ReminderManager
import com.app.routineturboa.ui.components.AddTaskScreen
import com.app.routineturboa.ui.components.EditTaskScreen
import com.app.routineturboa.ui.components.TasksLazyColumn
import com.app.routineturboa.viewmodel.TaskViewModel
import com.microsoft.identity.client.IAuthenticationResult
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun TasksScreen(reminderManager: ReminderManager) {
    val tag = "TasksScreen"

    val context = LocalContext.current
    val taskViewModelFactory = remember { TaskViewModelFactory(RoutineRepository(context)) }
    val taskViewModel: TaskViewModel = viewModel(factory = taskViewModelFactory)
    val tasks by taskViewModel.tasks.collectAsStateWithLifecycle()

    var clickedTask by remember { mutableStateOf<TaskEntity?>(null) }
    var taskBeingEdited by remember { mutableStateOf<TaskEntity?>(null) }
    var isAddingTask by remember { mutableStateOf(false) }

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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isAddingTask = true },
                modifier = Modifier.padding(end = 26.dp, bottom = 40.dp).size(60.dp, 60.dp),
                containerColor = Color.Blue.copy(alpha = 0.9f),
                contentColor = Color.White,
                shape = RoundedCornerShape(50.dp)
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add New Task",
                    modifier = Modifier.size(40.dp),
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.padding(paddingValues),
                color = MaterialTheme.colorScheme.background
            ) {
                TasksLazyColumn(
                    tasks = tasks,
                    onTaskSelected = { clickedTask = it },
                    onTaskEdited = { taskBeingEdited = it },
                    onTaskDelete = { taskViewModel.deleteTask(it) },
                    isTaskFirst = { taskViewModel.isTaskFirst(it) },
                    isTaskLast = { taskViewModel.isTaskLast(it) }
                )
            }

            // Show the add task screen as a dialog overlay
            if (isAddingTask) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                ) {
                    AddTaskScreen(
                        clickedTask = clickedTask,
                        onSaveNewTask = { newTask: TaskEntity ->
                            Log.d(tag, "Save Button in Add Screen clicked...Calling handleSaveTask")
                            taskViewModel.handleSaveTask(newTask, null)
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
                                taskViewModel.updateTask(updatedTask)
                                reminderManager.observeAndScheduleReminders(context)
                            }
                        },
                        onCancel = { taskBeingEdited = null }
                    )
                }
            }
        }
    }
}
