package com.app.routineturboa.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.core.content.ContextCompat

/**
 * @Name: NotificationPermissionHandler
 * @Description:
 * Companion Object: Contains all the necessary methods for handling notification permissions.
 * - initialize method sets up the requestPermissionLauncher with the activity and state.
 * - requestNotificationPermission method requests the notification permission.
 * - isNotificationPermissionGiven method checks if the notification permission is granted.
 *
 * Initialization: The initialize method sets up the ActivityResultLauncher that handles the permission request result.
 *
 */

class NotificationPermissionHandler {

    companion object {
        private const val TAG = "NotificationPermissionHandler"

        private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

        @RequiresApi(Build.VERSION_CODES.S)
        fun initialize(activity: ComponentActivity, isPermissionGranted: MutableState<Boolean>) {
            requestPermissionLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                isPermissionGranted.value = isGranted
                if (isGranted) {
                    Log.d(TAG, "Notification permission granted")
                } else {
                    Log.d(TAG, "Notification permission denied")
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.S)
        fun requestNotificationPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                Log.d(TAG, "Android version < 33, no notification permission request needed.")
            }
        }

        fun isNotificationPermissionGiven(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true // Permission is automatically granted on older Android versions
            }
        }
    }
}

