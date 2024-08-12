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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.routineturboa.data.local.RoutineRepository
import com.app.routineturboa.reminders.ReminderManager
import com.app.routineturboa.ui.main.scaffold.MainBottomBar
import com.app.routineturboa.ui.main.scaffold.MainDrawer
import com.app.routineturboa.ui.main.scaffold.MainTopBar
import com.app.routineturboa.viewmodel.TasksViewModel

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainScreen(reminderManager: ReminderManager) {
    Log.d("MainScreen", "MainScreen starts...")

    val context = LocalContext.current
    val taskViewModelFactory = remember { TaskViewModelFactory(RoutineRepository(context)) }
    val tasksViewModel: TasksViewModel = viewModel(factory = taskViewModelFactory)

    val isAddingTask = remember { mutableStateOf(false) }
    val clickedTaskId = remember { mutableStateOf<Int?>(null) }
    val editingTaskId = remember { mutableStateOf<Int?>(null) }
    val isQuickEditing = remember { mutableStateOf(false) }
    val isFullEditing = remember { mutableStateOf(false) }
    val isAnotherTaskEditing = remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ModalNavigationDrawer(
        // Rest of the UI color on drawer open
        scrimColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
        drawerState = drawerState,
        drawerContent = { MainDrawer(drawerState, tasksViewModel, reminderManager) },
    ) {
        Scaffold(
            topBar = { MainTopBar(drawerState) },
            bottomBar = {
                if (!isQuickEditing.value && !isFullEditing.value) {
                    MainBottomBar(
                        isAddingTask = isAddingTask,
                    )
                }
            }
        ) { paddingValues ->
            TasksLazyColumn(
                paddingValues = paddingValues,
                tasksViewModel = tasksViewModel,
                reminderManager = reminderManager,

                clickedTaskId = clickedTaskId,
                isAddingTask = isAddingTask,
                editingTaskId = editingTaskId,
                isQuickEditing = isQuickEditing,
                isFullEditing = isFullEditing,
                isAnotherTaskEditing = isAnotherTaskEditing,
            )
        }
    }
}