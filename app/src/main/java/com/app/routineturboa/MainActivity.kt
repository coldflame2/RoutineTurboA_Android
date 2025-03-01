package com.app.routineturboa

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.app.routineturboa.data.local.AppData
import com.app.routineturboa.data.repository.AppRepository
import com.app.routineturboa.reminders.LocalReminderManager
import com.app.routineturboa.reminders.ReminderManager
import com.app.routineturboa.ui.main.MainScreen
import com.app.routineturboa.ui.theme.RoutineTurboATheme
import com.app.routineturboa.utils.NotificationPermissionHandler

class MainActivity : ComponentActivity() {
    private val tag = "MainActivity"

    private lateinit var isNotificationPermissionGiven: MutableState<Boolean>
    private lateinit var showPermissionDialog: MutableState<Boolean>
    private lateinit var reminderManager: ReminderManager

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate for MainActivity")

        // Set to true to make system windows visible
        WindowCompat.setDecorFitsSystemWindows(window, true)

        // Get TaskDao from the Room database
        val taskDao = AppData.getDatabase().taskDao()

        // Initialize the repository with TaskDao
        val appRepository = AppRepository()

        // Pass the repository to ReminderManager
        reminderManager = ReminderManager(this, appRepository)

        // Initialize the state variables for Notifications permissions
        isNotificationPermissionGiven = mutableStateOf(
            NotificationPermissionHandler.isNotificationPermissionGiven(this)
        )
        showPermissionDialog = mutableStateOf(!isNotificationPermissionGiven.value)

        // Register the ActivityResultLauncher
        NotificationPermissionHandler.initialize(this, isNotificationPermissionGiven)

        setContent {
            RoutineTurboATheme {
                window.statusBarColor = MaterialTheme.colorScheme.primary.toArgb()
                window.navigationBarColor = MaterialTheme.colorScheme.primary.toArgb()

                // Initialize the TasksViewModel

                // Provide ReminderManager to the composable hierarchy
                CompositionLocalProvider(LocalReminderManager provides reminderManager) {
                    MainScreen()
                }

                // Show AlertDialog for granting permissions
                if (showPermissionDialog.value) {
                    Log.d(tag, "Permissions not given. Showing dialog for granting permissions.")

                    AlertDialog(
                        title = { Text("Notification Permission") },
                        onDismissRequest = { showPermissionDialog.value = false },
                        text = {
                            Text (
                                text = "This app needs notification permission to send you reminders." +
                                    "Would you like to grant this permission?"
                            )
                        },
                        dismissButton = {
                            Button(onClick = { showPermissionDialog.value = false }) {
                                Text("Not Now")
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                showPermissionDialog.value = false
                                NotificationPermissionHandler.requestNotificationPermission()
                            }) {
                                Text("Grant Permission")
                            }
                        }
                    )
                }
            }
        }
    }
}
