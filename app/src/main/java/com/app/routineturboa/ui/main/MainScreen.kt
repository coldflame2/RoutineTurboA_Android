package com.app.routineturboa.ui.main

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.app.routineturboa.RoutineTurboApp

import com.app.routineturboa.viewmodel.TasksViewModel
import com.app.routineturboa.ui.models.TaskEventsToFunctions

// Composable
import com.app.routineturboa.ui.scaffold.MainBottomBar
import com.app.routineturboa.ui.scaffold.MainDrawer
import com.app.routineturboa.ui.scaffold.MainTopBar

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainScreen(
    tasksViewModel: TasksViewModel,
    showExactAlarmDialog: MutableState<Boolean>
) {
    Log.d("MainScreen", "MainScreen starts...")

    val context = LocalContext.current

    // Access ReminderManager from the Application context via RoutineTurboApp
    val app = context.applicationContext as RoutineTurboApp
    val reminderManager = app.reminderManager

    val tasks by tasksViewModel.tasks.collectAsState()

    val tasksUiState by tasksViewModel.tasksUiState.collectAsState()

    val taskEventsToFunctions = TaskEventsToFunctions(
        onAnyTaskClick = tasksViewModel::onAnyTaskClick,
        onAnyTaskLongPress = tasksViewModel::onAnyTaskLongPress,
        onQuickEditClick = tasksViewModel::onQuickEditClick,
        onFullEditClick = tasksViewModel::onFullEditClick,
        onAddNewClick = tasksViewModel::onAddNewClick,
        onNewTaskSaveClick = tasksViewModel::onNewTaskSaveClick,
        onDeleteClick = tasksViewModel::onDeleteTask,
        onCancelClick = tasksViewModel::onCancelEdit,
        onConfirmEdit = tasksViewModel::onConfirmEdit,
        onShowTaskDetails = tasksViewModel::onShowTaskDetails,
    )

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    // trigger exact alarm permission check
    LaunchedEffect(Unit) {
        // Check if exact alarm permission is required
        if (!reminderManager.canScheduleExactAlarms()) {
            // Trigger the exact alarm permission dialog
            showExactAlarmDialog.value = true
        }
    }

    ModalNavigationDrawer(
        // Rest of the UI color on drawer open
        scrimColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
        drawerState = drawerState,
        drawerContent = { MainDrawer(drawerState, tasksViewModel, reminderManager, showExactAlarmDialog) },
    ) {
        Scaffold(
            topBar = { MainTopBar(drawerState) },
            bottomBar = { MainBottomBar(taskEventsToFunctions.onAddNewClick) }
        ) { paddingValues ->  // These paddingValues are applied along the edges inside a box.
            TasksLazyColumn(
                paddingValues = paddingValues,
                tasks = tasks,
                tasksUiState = tasksUiState,
                taskEventsToFunctions = taskEventsToFunctions
            )
        }
    }
}