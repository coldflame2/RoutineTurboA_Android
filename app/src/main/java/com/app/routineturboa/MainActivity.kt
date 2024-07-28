package com.app.routineturboa

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.areSystemBarsVisible
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.app.routineturboa.reminders.ReminderManager
import com.app.routineturboa.ui.MainScreen
import com.app.routineturboa.ui.theme.RoutineTurboATheme
import com.app.routineturboa.utils.PermissionUtils
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var reminderManager: ReminderManager


    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalLayoutApi::class)
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "onCreate for MainActivity")

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        super.onCreate(savedInstanceState)

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
            val areSystem = WindowInsets.Companion.areSystemBarsVisible
            Log.d("MainActivity", "areSystemBarsVisible: $areSystem")

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
            lifecycleScope.launch {
                reminderManager.observeAndScheduleReminders(applicationContext)
            }
        }
    }
}
