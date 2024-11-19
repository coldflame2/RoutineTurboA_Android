package com.app.routineturboa.ui.main

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.app.routineturboa.RoutineTurboApp
import com.app.routineturboa.core.models.UiScreen
import com.app.routineturboa.core.models.EventsHandler
import com.app.routineturboa.core.models.TaskOperationState

import com.app.routineturboa.viewmodel.TasksViewModel

// Composable
import com.app.routineturboa.ui.scaffold.AppBottomBar
import com.app.routineturboa.ui.scaffold.AppDrawer
import com.app.routineturboa.ui.scaffold.AppTopBar
import com.app.routineturboa.ui.tasks.pickers.PickDateDialog
import com.app.routineturboa.ui.reusable.animation.NewTaskScreenAnimation
import com.app.routineturboa.ui.reusable.animation.TasksLazyColumnAnimation
import com.app.routineturboa.ui.reusable.animation.SuccessIndicator
import com.app.routineturboa.ui.tasks.form.NewTaskForm
import com.app.routineturboa.ui.tasks.dialogs.TaskCompletionDialog
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainScreen(
    tasksViewModel: TasksViewModel,
    showExactAlarmDialog: MutableState<Boolean>
) {
    // region: variables
    val tag = "MainScreen"
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val app = remember { context.applicationContext as RoutineTurboApp }
    val reminderManager = app.reminderManager

    val selectedDate by tasksViewModel.selectedDate.collectAsState()
    val tasksByDate by tasksViewModel.tasksByDate.collectAsState()
    val tasksCompleted by tasksViewModel.taskCompletions.collectAsState()

    val uiState by tasksViewModel.uiState.collectAsState()

    val eventsHandler = remember { EventsHandler(tasksViewModel) }
    val onStateChangeEvents = eventsHandler.onStateChangeEvents()
    val onDataOperationEvents = eventsHandler.onDataOperationEvents()

    // Check if the current state is either AddingNewTask or FullEditing
    val isAddingOrFullEditing = uiState.uiScreen is UiScreen.AddingNew ||
            uiState.uiScreen is UiScreen.FullEditing

    // Get the clicked task from the UI state
    val clickedTask =  uiState.taskContext.clickedTask

    // Get the task below the clicked task from the UI state
    val taskBelowClicked = uiState.taskContext.taskBelowClickedTask

    fun scrollToTask(taskId: Int) {
        val index = tasksByDate.indexOfFirst { it.id == taskId }
        if (index != -1) {
            coroutineScope.launch {
                lazyListState.animateScrollToItem(index)
            }
        } else {
            // Handle the case where the task is not found
            Log.e("ScrollToTask", "Task with ID $taskId not found.")
        }
    }

    // endregion

    ModalNavigationDrawer(
        // Rest of the UI color on drawer open
        scrimColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
        drawerState = drawerState,
        drawerContent = {
            val rememberedClickedTask = remember { clickedTask }
            AppDrawer(
                drawerState = drawerState,
                tasksViewModel= tasksViewModel,
                uiState = uiState,
                onDataOperationEvents = onDataOperationEvents,
                reminderManager = reminderManager,
                showExactAlarmDialog = showExactAlarmDialog,
                clickedTask = rememberedClickedTask,
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
                    AppBottomBar(
                        onShowAddNewTaskClick= onStateChangeEvents.onShowAddNewTaskClick,
                        onShowFullEditTaskClick= onStateChangeEvents.onShowFullEditClick,
                        onShowQuickEditTaskClick = {
                            if (clickedTask != null) {
                                onStateChangeEvents.onShowQuickEditClick(clickedTask)
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->  // These paddingValues are applied along the edges inside a box.

            // Show Main TasksLazyColumn
            TasksLazyColumnAnimation(
                visible = uiState.uiScreen.shouldShowLazyColumn() ?: true
            ) {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background),  // Use paddingValues passed from MainScreen here
                    contentPadding = PaddingValues(4.dp, 0.dp, 6.dp, 0.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp) // space between tasks
                ) {
                    // Show ParentTaskItem for each task in the list
                    if (tasksByDate.isNotEmpty()) {
                        itemsIndexed(tasksByDate, key = {_, task -> task.id }) { index, task ->
                            ParentContainerLayout(
                                task = task,
                                indexInList = index,
                                lazyListState = lazyListState,
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

            // Show NewTaskCreationScreen
            NewTaskScreenAnimation(
                visible = uiState.uiScreen is UiScreen.AddingNew
            ) {
                // Remember the clicked and task below (but only after new task screen is first shown)
                val rememberedClickedTask = remember { clickedTask }
                val rememberedTaskBelowClicked = remember { taskBelowClicked }

                NewTaskForm(
                    paddingValues = paddingValues,
                    clickedTaskOrNull = rememberedClickedTask,
                    taskBelowClickedOrNull = rememberedTaskBelowClicked,
                    selectedDate = selectedDate,
                    onStateChangeEvents = onStateChangeEvents,
                    onDataOperationEvents = onDataOperationEvents,
                    taskOperationState = uiState.taskOperationState
                )


            }

            // Show DatePickerDialog
            NewTaskScreenAnimation(
                visible = uiState.uiScreen is UiScreen.DatePicker
            ) {
                PickDateDialog(
                    selectedDate = selectedDate,
                    onDateChange = {datePicked ->
                        onStateChangeEvents.onDateChangeClick (datePicked)
                    },
                    onCancel = { onStateChangeEvents.onDismissOrReset(null) }
                )
            }


            // Show FinishedTasksView dialog
            if (uiState.uiScreen is UiScreen.FinishedTasksView) {
                TaskCompletionDialog(
                    taskCompletionHistories = tasksCompleted,
                    onDismiss = { onStateChangeEvents.onDismissOrReset(clickedTask) }
                )
            }

            // show SuccessIndicator for the new task
            if (uiState.taskOperationState is TaskOperationState.Success) {
                Log.d(tag, "Showing SuccessIndicator after a new task.")
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(),
                    contentAlignment = Alignment.Center
                ) {
                    SuccessIndicator(
                        onReset = { onStateChangeEvents.onDismissOrReset }
                    )
                }
            }
        }
    }
}