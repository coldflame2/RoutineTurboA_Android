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
import com.app.routineturboa.viewmodel.TaskViewModel
import com.microsoft.graph.models.DriveItem
import com.microsoft.identity.client.IAuthenticationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MainScreen(taskViewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(RoutineRepository(LocalContext.current)))) {
    val tasks by taskViewModel.tasks.collectAsState()
    var selectedTaskForDisplay by remember { mutableStateOf<Task?>(null) } // Added tracking for selected task for display
    var taskBeingEdited by remember { mutableStateOf<Task?>(null) } // Added tracking for task being edited
    var isAddingTask by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val msalAuthManager = remember { MSALAuthManager.getInstance(context) }
    val oneDriveFiles by remember { mutableStateOf<List<DriveItem>>(emptyList()) }
    var authenticationResult by remember { mutableStateOf<IAuthenticationResult?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(authenticationResult) {
        authenticationResult?.let { authResult ->
            coroutineScope.launch {
                downloadFromOneDrive(authResult, context)
                taskViewModel.loadTasks()
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Display the currently selected task
            Text(
                text = selectedTaskForDisplay?.let { "Selected Task: ${it.taskName}" } ?: "No Task Selected",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp) // Added padding for spacing
            )

            if (isAddingTask) {
                AddTaskScreen(
                    onSave = { newTask ->
                        selectedTaskForDisplay?.let { // Added to check if a task is selected
                            taskViewModel.updatePositions(it.position + 1) // Added to update positions of tasks below the new task
                            newTask.position = it.position + 1 // Added to set the position of the new task
                            taskViewModel.addTask(newTask)
                        } ?: run {
                            newTask.position = tasks.size + 1 // Set position if no task is selected
                            taskViewModel.addTask(newTask)
                        }
                        isAddingTask = false
                    },
                    onCancel = {
                        isAddingTask = false
                    }
                )
            } else if (taskBeingEdited != null) { // Check if a task is selected for editing
                EditTaskScreen(
                    task = taskBeingEdited!!,
                    onSave = { updatedTask ->
                        taskViewModel.updateTask(updatedTask)
                        taskBeingEdited = null
                    },
                    onCancel = {
                        taskBeingEdited = null
                    }
                )
            } else {
                SignInButton(msalAuthManager, authenticationResult) { result ->
                    authenticationResult = result
                    msalAuthManager.saveAuthResult(result)
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(5.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            isSelected = task == selectedTaskForDisplay, // Pass whether the task is selected
                            onEditClick = { taskBeingEdited = it }, // Set the task to be edited
                            onClick = { selectedTaskForDisplay = task } // Update the selected task when clicked
                        )
                    }
                    items(oneDriveFiles, key = { it.id!! }) { file ->
                        Text(text = file.name ?: "No name")
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))

                Button(
                    onClick = { isAddingTask = true },
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                ) {
                    Text("Add New Task")
                }
            }
        }
    }
}

suspend fun downloadFromOneDrive(authResult: IAuthenticationResult, context: Context) {
    val authProvider = OneDriveManager.MsalAuthProvider(authResult)
    val oneDriveManager = OneDriveManager(authProvider)

    // Fetch the list of files from the root directory of OneDrive
    val files = withContext(Dispatchers.IO) {
        oneDriveManager.listFiles()
    }

    // Find the directory named "RoutineTurbo"
    val routineTurboDir = files.find { it.name == "RoutineTurbo" && it.folder != null }

    routineTurboDir?.let { dir ->
        // Fetch the list of files from the "RoutineTurbo" directory
        val dirFiles = dir.id?.let { dirId ->
            withContext(Dispatchers.IO) {
                oneDriveManager.listFiles(dirId)
            }
        }

        // Find "RoutineTurbo.db" among the files
        val dbFile = dirFiles?.find { it.name == "RoutineTurbo.db" }

        dbFile?.let { driveItem ->
            driveItem.id?.let { driveItemId ->
                // Get the local file path for the database
                val localDbFile = context.getDatabasePath(DatabaseHelper.DATABASE_NAME)

                // Download the database file from OneDrive to the local file path
                withContext(Dispatchers.IO) {
                    oneDriveManager.downloadFile(driveItemId, localDbFile)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}
