package com.app.routineturboa.ui

import TaskViewModelFactory
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.routineturboa.data.local.RoutineRepository
import com.app.routineturboa.data.model.Task
import com.app.routineturboa.services.MSALAuthManager
import com.app.routineturboa.services.downloadFromOneDrive
import com.app.routineturboa.ui.components.AddTaskScreen
import com.app.routineturboa.ui.components.EditTaskScreen
import com.app.routineturboa.ui.components.SignInButton
import com.app.routineturboa.ui.components.TaskItem
import com.app.routineturboa.viewmodel.TaskViewModel
import com.microsoft.graph.models.DriveItem
import com.microsoft.identity.client.IAuthenticationResult
import kotlinx.coroutines.launch

@Composable
fun MainScreen(taskViewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(RoutineRepository(LocalContext.current)))) {

    val tasks by taskViewModel.tasks.collectAsStateWithLifecycle()

    var selectedTaskForDisplay by remember { mutableStateOf<Task?>(null) }
    var taskBeingEdited by remember { mutableStateOf<Task?>(null) }
    var isAddingTask by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val msalAuthManager = remember { MSALAuthManager.getInstance(context) }
    var oneDriveFiles by remember { mutableStateOf<List<DriveItem>>(emptyList()) }
    var authenticationResult by remember { mutableStateOf<IAuthenticationResult?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(authenticationResult) {
        Log.d("MainScreen", "LaunchedEffect triggered for authenticationResult: $authenticationResult")
        authenticationResult?.let { authResult ->
            coroutineScope.launch {
                Log.d("MainScreen", "Starting downloadFromOneDrive")
                downloadFromOneDrive(authResult, context, taskViewModel)
                Log.d("MainScreen", "Finished downloadFromOneDrive")
            }
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        MainContent(
            tasks = tasks,
            selectedTaskForDisplay = selectedTaskForDisplay,
            taskBeingEdited = taskBeingEdited,
            isAddingTask = isAddingTask,
            msalAuthManager = msalAuthManager,
            authenticationResult = authenticationResult,
            oneDriveFiles = oneDriveFiles,

            onTaskSelected = { task ->
                if (task != null) {
                    Log.d("MainScreen", "Task selected: ${task.taskName}")
                }
                selectedTaskForDisplay = task
            },
            onTaskEdited = { task ->
                Log.d("MainScreen", "Editing task: ${task?.taskName}")
                taskBeingEdited = task
            },
            onTaskEditSave = { updatedTask ->
                Log.d("MainScreen", "Saving edited task: ${updatedTask.taskName}")
                taskViewModel.updateTask(updatedTask)
                taskBeingEdited = null
            },

            onAddTask = {
                Log.d("MainScreen", "Adding new task")
                isAddingTask = true
            },
            onCancelAddTask = {
                Log.d("MainScreen", "Canceling add task")
                isAddingTask = false
            },
            onSaveTask = { newTask, selectedTask ->
                Log.d("MainScreen", "Saving task: ${newTask.taskName}")
                taskViewModel.handleSaveTask(newTask, selectedTask)
                isAddingTask = false
            },
            onSignInSuccess = { result ->
                Log.d("MainScreen", "Sign-in successful: $result")
                authenticationResult = result
                msalAuthManager.saveAuthResult(result)
            }
        )
    }
}

@Composable
fun MainContent(
    tasks: List<Task>,
    selectedTaskForDisplay: Task?,
    taskBeingEdited: Task?,
    onTaskEditSave: (Task) -> Unit,
    isAddingTask: Boolean,
    msalAuthManager: MSALAuthManager,
    authenticationResult: IAuthenticationResult?,
    oneDriveFiles: List<DriveItem>,
    onTaskSelected: (Task?) -> Unit,
    onTaskEdited: (Task?) -> Unit,
    onAddTask: () -> Unit,
    onCancelAddTask: () -> Unit,
    onSaveTask: (Task, Task?) -> Unit,
    onSignInSuccess: (IAuthenticationResult) -> Unit
) {
    // Retain selected task and tasks list across recompositions
    val selectedTask = remember { mutableStateOf(selectedTaskForDisplay) }
    val tasksState = remember { mutableStateOf(tasks) }

    selectedTask.value = selectedTaskForDisplay
    tasksState.value = tasks

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {


        if (isAddingTask) {
            val initialStartTime = selectedTaskForDisplay?.endTime ?: if (tasks.isNotEmpty()) tasks.last().endTime else "08:00 AM"
            AddTaskScreen(
                initialStartTime = initialStartTime,
                onSave = { newTask -> onSaveTask(newTask, selectedTaskForDisplay) },
                onCancel = onCancelAddTask
            )
        }

        else if (taskBeingEdited != null) {
            EditTaskScreen(
                task = taskBeingEdited,
                onSave = { updatedTask ->
                    onTaskEditSave(updatedTask)
                    onTaskEdited(null)
                },
                onCancel = {
                    onTaskEdited(null)
                }
            )
        }

        else {

            LazyColumn(
                modifier = Modifier.weight(1f)
                    .height(350.dp),
                contentPadding = PaddingValues(2.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                items(tasksState.value, key = { it.id }) { task ->
                    TaskItem(
                        task = task,
                        isSelected = task == selectedTaskForDisplay,
                        onEditClick = { onTaskEdited(it) },
                        onClick = { onTaskSelected(task) }
                    )
                }
                items(oneDriveFiles, key = { it.id!! }) { file ->
                    Text(text = file.name ?: "No name")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row {
                SignInButton(msalAuthManager, authenticationResult, onSignInSuccess)

                // Add new Task Button
                Button(onClick = onAddTask) {
                    Text("Add New Task")
                }

                // Task currently clicked
                Text(
                    text = selectedTaskForDisplay?.let { "Selected Task: ${it.taskName}" } ?: "No Task Selected",
                    style = MaterialTheme.typography.labelMedium,
                    // align vertically center
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

        }
    }
}
