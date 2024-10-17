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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.app.routineturboa.RoutineTurboApp

import com.app.routineturboa.viewmodel.TasksViewModel
import com.app.routineturboa.ui.models.TaskEventsToFunctions

// Composable
import com.app.routineturboa.ui.scaffold.MainBottomBar
import com.app.routineturboa.ui.scaffold.MainDrawer
import com.app.routineturboa.ui.scaffold.MainTopBar
import com.app.routineturboa.ui.reusable.PickDateDialog
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainScreen(
    tasksViewModel: TasksViewModel,
    showExactAlarmDialog: MutableState<Boolean>
) {
    val tag = "MainScreen"
    Log.d(tag, "MainScreen starts...")

    val context = LocalContext.current

    // Access ReminderManager from the Application context via RoutineTurboApp
    val app = context.applicationContext as RoutineTurboApp
    val reminderManager = app.reminderManager
    val tasksByDate by tasksViewModel.tasksByDate.collectAsState()
    val tasksCompleted by tasksViewModel.taskCompletions.collectAsState()
    val tasksUiState by tasksViewModel.tasksUiState.collectAsState()

    val selectedDate = tasksUiState.selectedDate

    val taskEventsToFunctions = TaskEventsToFunctions(
        onAnyTaskClick = tasksViewModel::onAnyTaskClick,
        onAnyTaskLongPress = tasksViewModel::onAnyTaskLongPress,

        onShowAddNewClick = tasksViewModel::onAddNewClick,
        onShowQuickEditClick = tasksViewModel::onQuickEditClick,
        onShowFullEditClick = tasksViewModel::onFullEditClick,
        onShowTaskDetails = tasksViewModel::onShowTaskDetails,
        onShowCompletedTasks = tasksViewModel::onShowCompletedTasks,

        onCancelClick = tasksViewModel::onCancelEdit,

        onNewTaskSaveClick = tasksViewModel::onConfirmNewTaskClick,
        onConfirmEdit = tasksViewModel::onConfirmEdit,
        onDeleteClick = tasksViewModel::onDeleteTask,

        onDateChange = tasksViewModel::onDateChange
    )

    val drawerState = rememberDrawerState(DrawerValue.Closed)


    // State to show/hide the DatePickerDialog
    val isShowPickDateDialog = remember { mutableStateOf(false) }

    // Formatting the current date for display in the TopAppBar title
    val dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())
    val currentDate = selectedDate.format(dateFormatter)

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
        drawerContent = {
            MainDrawer(
                drawerState,
                tasksViewModel,
                reminderManager,
                showExactAlarmDialog,
                taskEventsToFunctions.onShowCompletedTasks
            )
        }
    ) {
        Scaffold(
            topBar = {
                MainTopBar(
                    drawerState = drawerState,
                    selectedDate = selectedDate,
                    onDatePickerClick = { isShowPickDateDialog.value = true },
                )
            },
            bottomBar = { MainBottomBar(taskEventsToFunctions.onShowAddNewClick) }
        ) { paddingValues ->  // These paddingValues are applied along the edges inside a box.

            // Show DatePickerDialog when showDatePickerDialog is true
            if (isShowPickDateDialog.value) {
                PickDateDialog(isShowPickDateDialog, selectedDate, taskEventsToFunctions.onDateChange)
            }

            TasksLazyColumn(
                paddingValues = paddingValues,
                tasks = tasksByDate,
                tasksCompleted = tasksCompleted,
                tasksUiState = tasksUiState,
                taskEventsToFunctions = taskEventsToFunctions
            )
        }
    }
}