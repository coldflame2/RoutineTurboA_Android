// MainActivity.kt

package com.app.routineturboa

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.app.routineturboa.data.repository.AppRepository
import com.app.routineturboa.reminders.ReminderManager
import com.app.routineturboa.shared.ActiveOverlayComponent
import com.app.routineturboa.ui.main.MainScreen
import com.app.routineturboa.ui.theme.RoutineTurboATheme
import com.app.routineturboa.utils.NotificationPermissionHandler
import com.app.routineturboa.viewmodel.TasksViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val tag = "MainActivity"

    @Inject lateinit var appRepository: AppRepository
    @Inject lateinit var reminderManager: ReminderManager
    private val tasksViewModel: TasksViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "MainActivity's onCreate...")

        WindowCompat.setDecorFitsSystemWindows(window, true)

        // Initialize NotificationPermissionHandler
        NotificationPermissionHandler.initialize(this)

        setContent {
            RoutineTurboATheme {
                ConfigureWindowColors()
                MainScreen(
                    tasksViewModel,
                    NotificationPermissionHandler.getShowExactAlarmDialog()
                )
                NotificationPermissionHandler.HandlePermissionDialogs()
            }
        }

        // Intercept the back press to prevent the app from closing
        onBackPressedDispatcher.addCallback(this) {
            // Custom back button behavior
            handleBackPress()
        }
    }

    @Composable
    private fun ConfigureWindowColors() {
        window.statusBarColor = MaterialTheme.colorScheme.primary.toArgb()
        window.navigationBarColor = MaterialTheme.colorScheme.primary.toArgb()
    }


    private fun handleBackPress() {
        Log.d(tag, "handleBackPress...")
        val uiState = tasksViewModel.uiState.value

        // If an overlay is active or task list visibility is false, reset to default state
        if (uiState.activeOverlayComponent != ActiveOverlayComponent.None || !uiState.isBaseTasksLazyColumnVisible) {
            tasksViewModel.setUiStateToDefault()
        } else {
            Log.d(tag, "Back button clicked with task list base view visible")
            // Do nothing as the task list is the active base view
        }
    }



}
