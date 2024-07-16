package com.app.routineturboa

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.app.routineturboa.reminders.ReminderManager
import com.app.routineturboa.ui.MainScreen
import com.app.routineturboa.ui.theme.RoutineTurboATheme
import com.app.routineturboa.utils.PermissionUtils
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var reminderManager: ReminderManager

    override fun onCreate(savedInstanceState: Bundle?) {

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")

        reminderManager = ReminderManager(this)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("MainActivity", "Notification permission granted")
                //
            } else {
                Log.d("MainActivity", "Notification permission denied")
            }
        }

        if (PermissionUtils.hasNotificationPermission(this)) {
            lifecycleScope.launch {
                reminderManager.observeAndScheduleReminders(applicationContext)
            }
        }

        setContent {
            RoutineTurboATheme {
                MainScreen(
                    hasNotificationPermission = PermissionUtils.hasNotificationPermission(this),
                    onRequestPermission = { requestNotificationPermission()},
                    reminderManager = reminderManager
                )

            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            lifecycleScope.launch {
                reminderManager.observeAndScheduleReminders(applicationContext)
            }
        }
    }


}

