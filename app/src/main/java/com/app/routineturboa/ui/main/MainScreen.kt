package com.app.routineturboa.ui.main

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.app.routineturboa.RoutineTurboApp
import com.app.routineturboa.shared.states.ActiveUiComponent
import com.app.routineturboa.shared.events.EventsHandler
import com.app.routineturboa.shared.states.TaskCreationState

import com.app.routineturboa.viewmodel.TasksViewModel

// Composable
import com.app.routineturboa.ui.scaffold.AppBottomBar
import com.app.routineturboa.ui.scaffold.AppDrawer
import com.app.routineturboa.ui.scaffold.AppTopBar
import com.app.routineturboa.ui.reusable.pickers.PickDateDialog
import com.app.routineturboa.ui.reusable.animation.NewTaskScreenAnimation
import com.app.routineturboa.ui.reusable.animation.TasksLazyColumnAnimation
import com.app.routineturboa.ui.reusable.animation.SuccessIndicator
import com.app.routineturboa.ui.tasks.ParentTaskItem
import com.app.routineturboa.ui.tasks.dialogs.NewTaskCreationScreen
import com.app.routineturboa.ui.tasks.dialogs.TaskCompletionDialog
import com.app.routineturboa.utils.TaskTypes
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainScreen(
    tasksViewModel: TasksViewModel,
    showExactAlarmDialog: MutableState<Boolean>
) {
    val tag = "MainScreen"
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val app = remember { context.applicationContext as RoutineTurboApp }
    val reminderManager = app.reminderManager

    val tasksByDate by tasksViewModel.tasksByDate.collectAsState()
    val tasksCompleted by tasksViewModel.taskCompletions.collectAsState()
    val mainTasks by remember(tasksByDate) {
        mutableStateOf(tasksByDate.filter { it.type == TaskTypes.MAIN })
    }

    val uiState by tasksViewModel.uiState.collectAsState()
    val selectedDate by tasksViewModel.selectedDate.collectAsState()

    val eventsHandler = remember { EventsHandler(tasksViewModel) }
    val onStateChangeEvents = eventsHandler.onStateChangeEvents()
    val onDataOperationEvents = eventsHandler.onDataOperationEvents()

    // Check if the current state is either AddingNewTask or FullEditing
    val isAddingOrFullEditing = uiState.activeUiComponent is ActiveUiComponent.AddingNew ||
            uiState.activeUiComponent is ActiveUiComponent.FullEditing


    // Get the clicked task from the UI state
    val clickedTask = uiState.uiTaskReferences.clickedTask

    // Get the task below the clicked task from the UI state
    val taskBelowClickedTask = uiState.uiTaskReferences.taskBelowClickedTask



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
                clickedTask = clickedTask,
                selectedDate = selectedDate,
                onShowCompletedTasks = onStateChangeEvents.onShowCompletedTasksClick,
            )
        }
    ) {
        Scaffold(
            topBar = {
                // Only show the top bar if isQuickOrFullEditing is false
                AppTopBar(
                    drawerState = drawerState,
                    selectedDate = selectedDate,
                    uiState = uiState,
                    onStateEvents = onStateChangeEvents,
                    onDataEvents = onDataOperationEvents,
                )
            },
            bottomBar = {
                if (!isAddingOrFullEditing) {
                    AppBottomBar(onStateChangeEvents.onShowAddNewTaskClick)
                }
            }
        ) { paddingValues ->  // These paddingValues are applied along the edges inside a box.

            // Show Main TasksLazyColumn
            TasksLazyColumnAnimation(
                visible = uiState.activeUiComponent.shouldShowLazyColumn() ?: true
            ) {
                LazyColumn(
                    modifier = Modifier.padding(paddingValues),  // Use paddingValues passed from MainScreen here
                    contentPadding = PaddingValues(0.dp, 0.dp, 6.dp, 0.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp) // space between tasks
                ) {
                    // Show ParentTaskItem for each task in the list
                    if (tasksByDate.isNotEmpty()) {
                        items(tasksByDate, key = { task -> task.id }) { task ->
                            ParentTaskItem(
                                task = task,
                                mainTasks = mainTasks,
                                uiState = uiState,
                                onStateChangeEvents = onStateChangeEvents,
                                onDataOperationEvents = onDataOperationEvents,
                            )
                        }
                    }

                    // Show message if no tasks exist for the selected date
                    else {
                        item {
                            Text(
                                text = "No tasks for this date",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // AnimatedVisibility for NewTaskCreationScreen
            NewTaskScreenAnimation(
                visible = uiState.activeUiComponent is ActiveUiComponent.AddingNew
            ) {
                // Remember the tasks when the screen is first displayed
                val rememberedClickedTask = remember { clickedTask }
                val rememberedTaskBelowClicked = remember { taskBelowClickedTask }

                NewTaskCreationScreen(
                    paddingValues = paddingValues,
                    clickedTask = rememberedClickedTask,
                    taskBelowClickedTask = rememberedTaskBelowClicked,
                    selectedDate = selectedDate,
                    mainTasks = mainTasks,
                    stateChangeEvents = onStateChangeEvents,
                    onConfirm = { confirmedClickedTask, confirmedTaskBelowClicked, newTaskFormData ->
                        coroutineScope.launch {
                            // set the state to loading
                            onDataOperationEvents.onNewTaskConfirmClick(
                                confirmedClickedTask,
                                confirmedTaskBelowClicked,
                                newTaskFormData
                            )
                        }
                    }
                )
            }

            // Show DatePickerDialog when showDatePickerDialog is true
            NewTaskScreenAnimation(
                visible = uiState.activeUiComponent is ActiveUiComponent.DatePicker
            ) {
                PickDateDialog(
                    selectedDate,
                    onStateChangeEvents.onDateChangeClick,
                    onStateChangeEvents.onCancelClick
                )
            }



            // Show completed tasks dialog
            if (uiState.activeUiComponent is ActiveUiComponent.FinishedTasks) {
                TaskCompletionDialog(tasksCompleted, onStateChangeEvents.onCancelClick)
            }

            // Success Indicator for the new task
            if (uiState.taskCreationState is TaskCreationState.Success) {
                Log.d(tag, "Showing SuccessIndicator after a new task.")
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(),
                    contentAlignment = Alignment.Center
                ) {
                    SuccessIndicator(
                        onReset = {
                            onStateChangeEvents.resetTaskCreationState
                        }
                    )
                }
            }


        }
    }
}