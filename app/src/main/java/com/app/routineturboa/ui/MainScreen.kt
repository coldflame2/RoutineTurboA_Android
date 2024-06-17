// In MainScreen.kt
package com.app.routineturboa.ui

import TaskViewModelFactory
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.routineturboa.data.local.DatabaseHelper
import com.app.routineturboa.data.local.RoutineRepository
import com.app.routineturboa.data.model.Task
import com.app.routineturboa.services.MSALAuthManager
import com.app.routineturboa.services.OneDriveManager
import com.app.routineturboa.ui.components.AddTaskScreen
import com.app.routineturboa.ui.components.EditTaskScreen
import com.app.routineturboa.ui.components.SignInButton
import com.app.routineturboa.ui.components.TaskItem
import com.app.routineturboa.utils.TimeUtils
import com.app.routineturboa.viewmodel.TaskViewModel
import com.microsoft.graph.models.DriveItem
import com.microsoft.identity.client.IAuthenticationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MainScreen(taskViewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(RoutineRepository(LocalContext.current)))) {
    val tasks by taskViewModel.tasks.collectAsState()
    var selectedTaskForDisplay by remember { mutableStateOf<Task?>(null) }
    var taskBeingEdited by remember { mutableStateOf<Task?>(null) }
    var isAddingTask by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val msalAuthManager = remember { MSALAuthManager.getInstance(context) }
    var oneDriveFiles by remember { mutableStateOf<List<DriveItem>>(emptyList()) }
    var authenticationResult by remember { mutableStateOf<IAuthenticationResult?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(authenticationResult) {
        authenticationResult?.let { authResult ->
            coroutineScope.launch {
                downloadFromOneDrive(authResult, context, taskViewModel)
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
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
            onTaskSelected = { selectedTaskForDisplay = it },
            onTaskEdited = { taskBeingEdited = it },
            onAddTask = { isAddingTask = true },
            onCancelAddTask = { isAddingTask = false },
            onSaveTask = { newTask, selectedTask ->
                handleSaveTask(newTask, selectedTask, taskViewModel, tasks)
                isAddingTask = false
            },
            onSignInSuccess = { result ->
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp)
    ) {
        Text(
            text = selectedTaskForDisplay?.let { "Selected Task: ${it.taskName}" } ?: "No Task Selected",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 36.dp)
        )

        if (isAddingTask) {
            val initialStartTime = selectedTaskForDisplay?.endTime ?: if (tasks.isNotEmpty()) tasks.last().endTime else "08:00 AM"
            AddTaskScreen(
                initialStartTime = initialStartTime,
                onSave = { newTask -> onSaveTask(newTask, selectedTaskForDisplay) },
                onCancel = onCancelAddTask
            )
        } else if (taskBeingEdited != null) {
            EditTaskScreen(
                task = taskBeingEdited,
                onSave = { updatedTask ->
                    onTaskEdited(null)
                },
                onCancel = {
                    onTaskEdited(null)
                }
            )
        } else {
            SignInButton(msalAuthManager, authenticationResult, onSignInSuccess)

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(5.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
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

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = onAddTask,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            ) {
                Text("Add New Task")
            }
        }
    }
}

private suspend fun downloadFromOneDrive(authResult: IAuthenticationResult, context: Context, taskViewModel: TaskViewModel) {
    val authProvider = OneDriveManager.MsalAuthProvider(authResult)
    val oneDriveManager = OneDriveManager(authProvider)

    val files = withContext(Dispatchers.IO) {
        oneDriveManager.listFiles()
    }

    val routineTurboDir = files.find { it.name == "RoutineTurbo" && it.folder != null }

    routineTurboDir?.let { dir ->
        val dirFiles = dir.id?.let { dirId ->
            withContext(Dispatchers.IO) {
                oneDriveManager.listFiles(dirId)
            }
        }

        val dbFile = dirFiles?.find { it.name == "RoutineTurbo.db" }

        dbFile?.let { driveItem ->
            driveItem.id?.let { driveItemId ->
                val localDbFile = context.getDatabasePath(DatabaseHelper.DATABASE_NAME)
                withContext(Dispatchers.IO) {
                    oneDriveManager.downloadFile(driveItemId, localDbFile)
                }
            }
        }
    }
    taskViewModel.loadTasks()
}

private fun handleSaveTask(
    newTask: Task,
    selectedTaskForDisplay: Task?,
    taskViewModel: TaskViewModel,
    tasks: List<Task>
) {
    selectedTaskForDisplay?.let { selectedTask ->
        val newStartTime = selectedTask.endTime
        newTask.startTime = newStartTime
        newTask.endTime = TimeUtils.addDurationToTime(newStartTime, newTask.duration)
        taskViewModel.updatePositions(selectedTask.position + 1)
        newTask.position = selectedTask.position + 1
        taskViewModel.addTask(newTask)
        taskViewModel.adjustSubsequentTasks(newTask.position, newTask.endTime)
    } ?: run {
        if (tasks.isNotEmpty()) {
            val lastTask = tasks.last()
            newTask.startTime = lastTask.endTime
            newTask.endTime = TimeUtils.addDurationToTime(newTask.startTime, newTask.duration)
            newTask.position = tasks.size + 1
            taskViewModel.addTask(newTask)
        } else {
            newTask.startTime = "08:00 AM"
            newTask.endTime = TimeUtils.addDurationToTime(newTask.startTime, newTask.duration)
            newTask.position = 1
            taskViewModel.addTask(newTask)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}
