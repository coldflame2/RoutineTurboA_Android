package com.app.routineturboa.ui

import TaskViewModelFactory
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    val context = LocalContext.current
    val msalAuthManager = remember { MSALAuthManager.getInstance(context) }
    var oneDriveFiles by remember { mutableStateOf<List<DriveItem>>(emptyList()) }
    var authenticationResult by remember { mutableStateOf<IAuthenticationResult?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(authenticationResult) {
        authenticationResult?.let { authResult ->
            coroutineScope.launch {
                // Initialize OneDriveManager with the authentication result
                val authProvider = OneDriveManager.MsalAuthProvider(authResult)
                val oneDriveManager = OneDriveManager(authProvider)

                // Fetch the list of files from the root directory of OneDrive
                val files = withContext(Dispatchers.IO) {
                    oneDriveManager.listFiles()
                }

                // Find the directory named "RoutineTurbo" that is also a folder
                val routineTurboDir = files.find { it.name == "RoutineTurbo" && it.folder != null }

                routineTurboDir?.let { dir ->
                    // Fetch the list of files from the "RoutineTurbo" directory
                    val dirFiles = dir.id?.let { dirId ->
                        withContext(Dispatchers.IO) {
                            oneDriveManager.listFiles(dirId)
                        }
                    }

                    // Update the list of OneDrive files if `dirFiles` is not null
                    if (dirFiles != null) {
                        oneDriveFiles = dirFiles
                    }

                    // Find the database file named "RoutineTurbo.db" in the directory
                    val dbFile = dirFiles?.find { it.name == "RoutineTurbo.db" }

                    dbFile?.let { driveItem ->
                        driveItem.id?.let { driveItemId ->
                            // Get the local file path for the database
                            val localDbFile = context.getDatabasePath(DatabaseHelper.DATABASE_NAME)

                            // Download the database file from OneDrive to the local file path
                            val downloadSuccessful = withContext(Dispatchers.IO) {
                                oneDriveManager.downloadFile(driveItemId, localDbFile)
                            }

                            // Load tasks from the local database into the ViewModel if the download is successful
                            if (downloadSuccessful) {
                                withContext(Dispatchers.Main) {
                                    taskViewModel.loadTasks()
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (selectedTask == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SignInButton(msalAuthManager, authenticationResult) { result ->
                    authenticationResult = result
                    msalAuthManager.saveAuthResult(result)
                }

                LazyColumn(
                    contentPadding = PaddingValues(5.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskItem(task) {
                            selectedTask = it
                        }
                    }
                    items(oneDriveFiles, key = { it.id!! }) { file ->
                        Text(text = file.name ?: "No name")
                    }
                }
            }
        }

        else {
            EditTaskScreen(
                task = selectedTask!!,
                onSave = { updatedTask ->
                    taskViewModel.updateTask(updatedTask)
                    selectedTask = null
                },
                onCancel = {
                    selectedTask = null
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}
