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
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.app.routineturboa.RoutineTurboApp
import com.app.routineturboa.shared.EventsHandler

import com.app.routineturboa.viewmodel.TasksViewModel

// Composable
import com.app.routineturboa.ui.scaffold.AppBottomBar
import com.app.routineturboa.ui.scaffold.AppDrawer
import com.app.routineturboa.ui.scaffold.AppTopBar
import com.app.routineturboa.ui.reusable.PickDateDialog

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainScreen(
    tasksViewModel: TasksViewModel,
    showExactAlarmDialog: MutableState<Boolean>
) {
    val tag = "MainScreen"
    val context = LocalContext.current

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val app = remember { context.applicationContext as RoutineTurboApp }
    val reminderManager = app.reminderManager

    val tasksByDate by tasksViewModel.tasksByDate.collectAsState()
    val tasksCompleted by tasksViewModel.taskCompletions.collectAsState()

    val uiStates by tasksViewModel.uiStates.collectAsState()

    val eventsHandler = remember { EventsHandler(tasksViewModel) }
    val stateChangeEventsHandling = eventsHandler.stateChangeEventsHandling()
    val dataOperationEventsHandling = eventsHandler.dataOperationEventsHandling()

    val tasksBasedOnState by tasksViewModel.tasksBasedOnState.collectAsState()

    val selectedDate by tasksViewModel.selectedDate.collectAsState()

    LaunchedEffect(Unit) {
        Log.d(tag, "MainScreen starts...")
    }

    ModalNavigationDrawer(
        // Rest of the UI color on drawer open
        scrimColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                drawerState = drawerState,
                tasksViewModel= tasksViewModel,
                reminderManager = reminderManager,
                showExactAlarmDialog = showExactAlarmDialog,
                clickedTask = tasksBasedOnState.clickedTask,
                selectedDate = selectedDate,
                onShowCompletedTasks = stateChangeEventsHandling.onShowCompletedTasksClick,
                uiStates = uiStates
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    drawerState = drawerState,
                    selectedDate = selectedDate,
                    onDatePickerClick =  stateChangeEventsHandling.onShowDatePickerClick,
                )
            },
            bottomBar = { AppBottomBar(stateChangeEventsHandling.onShowAddNewTaskClick) }
        ) { paddingValues ->  // These paddingValues are applied along the edges inside a box.

            // Show DatePickerDialog when showDatePickerDialog is true
            if (uiStates.isShowingDatePicker) {
                PickDateDialog(
                    selectedDate,
                    stateChangeEventsHandling.onDateChangeClick,
                    stateChangeEventsHandling.onCancelClick
                )
            }

            TasksLazyColumn(
                paddingValues = paddingValues,
                tasks = tasksByDate,
                tasksCompleted = tasksCompleted,
                selectedDate = selectedDate,
                tasksBasedOnState = tasksBasedOnState,
                uiStates = uiStates,
                stateChangeEvents = stateChangeEventsHandling,
                dataOperationEvents = dataOperationEventsHandling
            )
        }
    }
}