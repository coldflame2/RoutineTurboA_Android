// NotificationPermissionHandler.kt

package com.app.routineturboa.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

class NotificationPermissionHandler {

    companion object {
        private const val TAG = "NotificationPermissionHandler"

        private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

        // State variables
        private lateinit var isNotificationPermissionGiven: MutableState<Boolean>
        private lateinit var showPermissionDialog: MutableState<Boolean>
        private lateinit var showExactAlarmDialog: MutableState<Boolean>

        @RequiresApi(Build.VERSION_CODES.S)
        fun initialize(activity: ComponentActivity) {
            isNotificationPermissionGiven = mutableStateOf(
                isNotificationPermissionGranted(activity)
            )
            showPermissionDialog = mutableStateOf(!isNotificationPermissionGiven.value)
            showExactAlarmDialog = mutableStateOf(false)

            requestPermissionLauncher = activity.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                isNotificationPermissionGiven.value = isGranted
                if (isGranted) {
                    Log.d(TAG, "Notification permission granted")
                } else {
                    Log.d(TAG, "Notification permission denied")
                }
            }
        }

        private fun isNotificationPermissionGranted(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true // Automatically granted on older versions
            }
        }

        @RequiresApi(Build.VERSION_CODES.S)
        fun requestNotificationPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                Log.d(TAG, "No permission request needed for versions below Tiramisu.")
            }
        }

        // Expose state variables if needed
        fun getShowExactAlarmDialog(): MutableState<Boolean> = showExactAlarmDialog
        fun getShowPermissionDialog(): MutableState<Boolean> = showPermissionDialog

        // Composable function to handle permission dialogs
        @RequiresApi(Build.VERSION_CODES.S)
        @Composable
        fun HandlePermissionDialogs() {
            HandleNotificationPermissionDialog()
            HandleExactAlarmPermissionDialog()
        }

        @RequiresApi(Build.VERSION_CODES.S)
        @Composable
        private fun HandleNotificationPermissionDialog() {
            if (showPermissionDialog.value) {
                AlertDialog(
                    title = { Text("Notification Permission") },
                    onDismissRequest = { showPermissionDialog.value = false },
                    text = {
                        Text("This app needs notification permission to send you reminders. Would you like to grant this permission?")
                    },
                    dismissButton = {
                        Button(onClick = { showPermissionDialog.value = false }) {
                            Text("Not Now")
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            showPermissionDialog.value = false
                            requestNotificationPermission()
                        }) {
                            Text("Grant Permission")
                        }
                    }
                )
            }
        }

        @RequiresApi(Build.VERSION_CODES.S)
        @Composable
        private fun HandleExactAlarmPermissionDialog() {
            val context = LocalContext.current
            if (showExactAlarmDialog.value) {
                AlertDialog(
                    title = { Text("Exact Alarm Permission Required") },
                    onDismissRequest = { showExactAlarmDialog.value = false },
                    text = {
                        Text("This app needs permission to schedule exact alarms. Without this permission, reminders may not work properly. Would you like to grant this permission?")
                    },
                    dismissButton = {
                        Button(onClick = { showExactAlarmDialog.value = false }) {
                            Text("Not Now")
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            showExactAlarmDialog.value = false
                            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        }) {
                            Text("Go to Settings")
                        }
                    }
                )
            }
        }
    }
}
