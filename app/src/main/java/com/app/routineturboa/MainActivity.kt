package com.app.routineturboa

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.areSystemBarsVisible
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.app.routineturboa.reminders.ReminderManager
import com.app.routineturboa.ui.MainScreen
import com.app.routineturboa.ui.theme.RoutineTurboATheme
import com.app.routineturboa.utils.PermissionUtils


class MainActivity : ComponentActivity() {
    private val tag = "MainActivity"
    
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var reminderManager: ReminderManager


    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalLayoutApi::class)
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(tag, "onCreate for MainActivity")

        WindowCompat.setDecorFitsSystemWindows(window, true) // Set to true to make system windows visible
        window.statusBarColor = Color.Blue.toArgb()

        super.onCreate(savedInstanceState)

        reminderManager = ReminderManager(this)
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d(tag, "Notification permission granted")
                //
            } else {
                Log.d(tag, "Notification permission denied")
            }
        }

        setContent {
            val areSystem = WindowInsets.Companion.areSystemBarsVisible
            Log.d(tag, "areSystemBarsVisible: $areSystem")

            RoutineTurboATheme {
                MainScreen(
                    hasNotificationPermission = PermissionUtils.hasNotificationPermission(this),
                    onRequestPermission = { requestNotificationPermission()},
                    reminderManager = reminderManager
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            Log.d(tag, "Android version < 33, no notification permission request needed.")
        }
    }
}
