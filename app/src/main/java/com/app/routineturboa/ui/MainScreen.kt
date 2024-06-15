package com.app.routineturboa.ui

import TaskViewModelFactory
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.app.routineturboa.MainActivity
import com.app.routineturboa.data.local.DatabaseHelper
import com.app.routineturboa.data.local.RoutineRepository
import com.app.routineturboa.data.model.Task
import com.app.routineturboa.services.MSALAuthManager
import com.app.routineturboa.services.OneDriveManager
import com.app.routineturboa.ui.components.EditTaskScreen
import com.app.routineturboa.ui.components.TaskItem
import com.app.routineturboa.viewmodel.TaskViewModel
import com.microsoft.graph.models.DriveItem
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAccount
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.exception.MsalException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MainScreen(taskViewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(RoutineRepository(LocalContext.current)))) {
    val tasks by taskViewModel.tasks.collectAsState()
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    val context = LocalContext.current
    val msalAuthManager = remember { MSALAuthManager(context) }
    var oneDriveFiles by remember { mutableStateOf<List<DriveItem>>(emptyList()) }
    var authenticationResult by remember { mutableStateOf<IAuthenticationResult?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(authenticationResult) {
        authenticationResult?.let { authResult ->
            val authProvider = OneDriveManager.MsalAuthProvider(authResult)
            val oneDriveManager = OneDriveManager(authProvider)
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    val files = oneDriveManager.listFiles()
                    val routineTurboDir = files.find { it.name == "RoutineTurbo" && it.folder != null }
                    routineTurboDir?.let { dir ->
                        val dirFiles = oneDriveManager.listFiles(dir.id)
                        oneDriveFiles = dirFiles
                        val dbFile = dirFiles.find { it.name == "RoutineTurbo.db" }
                        dbFile?.let { driveItem ->
                            driveItem.id?.let { driveItemId ->
                                val localDbFile = context.getDatabasePath(DatabaseHelper.DATABASE_NAME)
                                oneDriveManager.downloadFile(driveItemId, localDbFile)
                                taskViewModel.loadTasks()
                            }
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        Log.d("MainScreen", "Starting sign-in process")
        msalAuthManager.signIn(context as MainActivity, object : AuthenticationCallback {
            override fun onSuccess(result: IAuthenticationResult) {
                Log.d("MainScreen", "Sign-in successful")
                authenticationResult = result
            }

            override fun onError(exception: MsalException) {
                Log.e("MainScreen", "Sign-in error: ${exception.message}")
            }

            override fun onCancel() {
                Log.d("MainScreen", "Sign-in canceled")
            }
        })
        onDispose { }
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
        } else {
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

@Composable
fun SignInButton(
    msalAuthManager: MSALAuthManager,
    authenticationResult: IAuthenticationResult?,
    onSignInSuccess: (IAuthenticationResult) -> Unit
) {
    val context = LocalContext.current
    var isSigningIn by remember { mutableStateOf(false) }

    Button(
        onClick = {
            Log.d("SignInButton", "Sign-in button clicked")
            isSigningIn = true
            msalAuthManager.singleAccountApp?.getCurrentAccountAsync(object : ISingleAccountPublicClientApplication.CurrentAccountCallback {
                override fun onAccountLoaded(activeAccount: IAccount?) {
                    if (activeAccount != null) {
                        msalAuthManager.signOut(object : ISingleAccountPublicClientApplication.SignOutCallback {
                            override fun onSignOut() {
                                Log.d("SignInButton", "Signed out successfully, now signing in again.")
                                signIn(msalAuthManager, context as MainActivity, onSignInSuccess)
                            }

                            override fun onError(exception: MsalException) {
                                Log.e("SignInButton", "Sign-out error: ${exception.message}")
                                isSigningIn = false
                            }
                        })
                    } else {
                        signIn(msalAuthManager, context as MainActivity, onSignInSuccess)
                    }
                }

                override fun onAccountChanged(priorAccount: IAccount?, currentAccount: IAccount?) {
                }

                override fun onError(exception: MsalException) {
                    Log.e("SignInButton", "Error loading current account: ${exception.message}")
                    isSigningIn = false
                }
            })
        },
        enabled = authenticationResult == null && !isSigningIn
    ) {
        Text(text = if (authenticationResult != null) "Signed In" else "Sign In")
    }
}

fun signIn(msalAuthManager: MSALAuthManager, activity: MainActivity, onSignInSuccess: (IAuthenticationResult) -> Unit) {
    msalAuthManager.signIn(activity, object : AuthenticationCallback {
        override fun onSuccess(result: IAuthenticationResult) {
            Log.d("SignInButton", "Sign-in successful")
            onSignInSuccess(result)
        }

        override fun onError(exception: MsalException) {
            Log.e("SignInButton", "Sign-in error: ${exception.message}")
        }

        override fun onCancel() {
            Log.d("SignInButton", "Sign-in canceled")
        }
    })
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}
