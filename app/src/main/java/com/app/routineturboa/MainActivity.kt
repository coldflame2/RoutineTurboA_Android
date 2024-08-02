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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.app.routineturboa.reminders.ReminderManager
import com.app.routineturboa.ui.MainScreen
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
        reminderManager = ReminderManager(this)
        // Initialize the state variables
        isNotificationPermissionGiven = mutableStateOf(NotificationPermissionHandler.isNotificationPermissionGiven(this))
        showPermissionDialog = mutableStateOf(!isNotificationPermissionGiven.value)

        // Register the ActivityResultLauncher
        NotificationPermissionHandler.initialize(this, isNotificationPermissionGiven)

        setContent {
            RoutineTurboATheme {
                window.statusBarColor = MaterialTheme.colorScheme.primary.toArgb()
                window.navigationBarColor = MaterialTheme.colorScheme.primary.toArgb()

                MainScreen(reminderManager = reminderManager)

                if (showPermissionDialog.value) {
                    AlertDialog(
                        title = { Text("Notification Permission") },
                        text = {
                            Text (text = "This app needs notification permission to send you reminders." +
                                    "Would you like to grant this permission?")
                        },
                        onDismissRequest = { showPermissionDialog.value = false },
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
                        },
                    )
                }

            }
        }
    }
}
