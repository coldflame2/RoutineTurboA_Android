package com.app.routineturboa.ui

import TaskViewModelFactory
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.routineturboa.data.local.RoutineRepository
import com.app.routineturboa.data.model.TaskEntity
import com.app.routineturboa.services.downloadFromOneDrive
import com.app.routineturboa.ui.components.AddTaskScreen
import com.app.routineturboa.ui.components.EditTaskScreen
import com.app.routineturboa.ui.components.TaskList
import com.app.routineturboa.viewmodel.TaskViewModel
import com.microsoft.identity.client.IAuthenticationResult
import kotlinx.coroutines.launch


@Composable
fun MainScreen() {
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
        authenticationResult?.let { authResult ->
            coroutineScope.launch {
                downloadFromOneDrive(authResult, context, taskViewModel)
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isAddingTask = true }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add New Task")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Surface(
            modifier = Modifier.padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            when {
                isAddingTask -> {
                    AddTaskScreen(
                        initialStartTime = "08:00 AM",
                        onSave = { newTask: TaskEntity ->
                            taskViewModel.handleSaveTask(newTask, null)
                            isAddingTask = false
                        },
                        onCancel = { isAddingTask = false }
                    )
                }

                taskBeingEdited != null -> {
                    taskBeingEdited?.let { task ->
                        EditTaskScreen(
                            task = task,
                            onSave = { updatedTask: TaskEntity ->
                                taskViewModel.updateTask(updatedTask)
                                taskBeingEdited = null
                            },
                            onCancel = { taskBeingEdited = null }
                        )
                    }
                }

                else -> {
                    TaskList(
                        tasks = tasks,
                        onTaskSelected = { clickedTask = it },
                        onTaskEdited = { taskBeingEdited = it },
                        onTaskDelete = { taskViewModel.deleteTask(it) },
                        isTaskFirst = { taskViewModel.isTaskFirst(it) },
                        isTaskLast = { taskViewModel.isTaskLast(it) }
                    )
                }
            }
        }
    }
}

