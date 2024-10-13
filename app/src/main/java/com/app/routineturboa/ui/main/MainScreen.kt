package com.app.routineturboa.ui.main

import TaskViewModelFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.routineturboa.data.repository.AppRepository

import com.app.routineturboa.reminders.LocalReminderManager
import com.app.routineturboa.viewmodel.TasksViewModel
import com.app.routineturboa.ui.models.TaskEventsToFunctions

// Composable
import com.app.routineturboa.ui.scaffold.MainBottomBar
import com.app.routineturboa.ui.scaffold.MainDrawer
import com.app.routineturboa.ui.scaffold.MainTopBar

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainScreen() {
    Log.d("MainScreen", "MainScreen starts...")

    // Access ReminderManager using CompositionLocal LocalReminderManager
    val reminderManager = LocalReminderManager.current

    val taskViewModelFactory = remember { TaskViewModelFactory(AppRepository()) }
    val tasksVM: TasksViewModel = viewModel(factory = taskViewModelFactory)
    val tasks by tasksVM.tasks.collectAsState()

    val tasksUiState by tasksVM.tasksUiState.collectAsState()

    val taskEventsToFunctions = TaskEventsToFunctions(
        onAnyTaskClick = tasksVM::onAnyTaskClick,
        onAnyTaskLongPress = tasksVM::onAnyTaskLongPress,
        onQuickEditClick = tasksVM::onQuickEditClick,
        onFullEditClick = tasksVM::onFullEditTask,
        onAddNewClick = tasksVM::onAddNewClick,
        onNewTaskSaveClick = tasksVM::onNewTaskSaveClick,
        onDeleteClick = tasksVM::onDeleteTask,
        onCancelClick = tasksVM::onCancelEdit,
        onConfirmEdit = tasksVM::onConfirmEdit,
        onShowTaskDetails = tasksVM::onShowTaskDetails,
    )

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ModalNavigationDrawer(
        // Rest of the UI color on drawer open
        scrimColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
        drawerState = drawerState,
        drawerContent = { MainDrawer(drawerState, tasksVM, reminderManager) },
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