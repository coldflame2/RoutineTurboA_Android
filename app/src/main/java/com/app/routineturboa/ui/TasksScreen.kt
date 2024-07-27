package com.app.routineturboa.ui

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.routineturboa.data.model.TaskEntity
import com.app.routineturboa.reminders.ReminderManager
import com.app.routineturboa.ui.components.AddTaskScreen
import com.app.routineturboa.ui.components.EmptyTaskCardPlaceholder
import com.app.routineturboa.ui.components.SingleTaskCard
import com.app.routineturboa.viewmodel.TasksViewModel
import com.microsoft.identity.client.IAuthenticationResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun TasksScreen(context: Context, tasksViewModel: TasksViewModel, reminderManager: ReminderManager) {
    val tag = "TasksScreen"

    val tasks by tasksViewModel.tasks.collectAsStateWithLifecycle()
    var clickedTask by remember { mutableStateOf<TaskEntity?>(null) }
    var taskBeingEdited by remember { mutableStateOf<TaskEntity?>(null) }
    var isAddingTask by remember { mutableStateOf(false) }

    val authenticationResult by remember { mutableStateOf<IAuthenticationResult?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }

    val isRefreshing by tasksViewModel.isRefreshing.collectAsState()

    val listState = rememberLazyListState()
    val currentDateTime = remember { java.time.LocalDateTime.now() }

    // Find the index of the task whose time range includes the current time
    val targetIndex = tasks.indexOfFirst { task ->
        val startTime = task.startTime
        val endTime = task.endTime
        currentDateTime.isAfter(startTime) && currentDateTime.isBefore(endTime)
    }

    // Use LaunchedEffect to perform scrolling when the targetIndex changes
    LaunchedEffect(targetIndex) {
        if (targetIndex != -1) {
            listState.animateScrollToItem(targetIndex)
        }
    }

    LaunchedEffect(authenticationResult, key2 = true) {
        Log.d(tag, "TasksScreen LaunchedEffect called")
        authenticationResult?.let { authResult ->
            coroutineScope.launch {
                Log.d(tag, "Downloading from OneDrive")
            }
        }
    }

    // Start a coroutine to delay the loading state change
    LaunchedEffect(tasks) {
        delay(500)
        isLoading = false
    }


    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(30.dp),

                title = { Text("Tasks") },
                actions = {
                    IconButton(
                        onClick = { tasksViewModel.refreshTasks()
                                    clickedTask = null },
                        enabled = !isRefreshing,

                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },

        // Plus/Add Floating Button
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (clickedTask != null) {
                        isAddingTask = true
                    } else {
                        Toast.makeText(context, "Please select a task.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .padding(end = 15.dp, bottom = 10.dp)
                    .size(60.dp, 60.dp),
                containerColor = Color.Blue.copy(alpha = 0.9f),
                contentColor = Color.White,
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add New Task"
                )
            }
        },

        floatingActionButtonPosition = FabPosition.EndOverlay

    ) { paddingValues ->
        // Tasks list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxHeight()
                .padding(paddingValues),
        ) {
            if (isLoading) {
                items(2) {
                    EmptyTaskCardPlaceholder()
                }
            } else {
                if (tasks.isEmpty()) {
                    item {
                        Text(
                            text = "No tasks available",
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize(Alignment.Center)
                        )
                    }
                } else {
                    items(tasks, key = { task ->
                        task.id
                    }) { task ->
                        SingleTaskCard(
                            context = context,
                            tasksViewModel = tasksViewModel,
                            task = task,
                            onClick = { clickedTask = task },
                            onEditClick = {
                                taskBeingEdited = it
                                clickedTask = task
                            },
                            canDelete = !tasksViewModel.isTaskFirst(task) && !tasksViewModel.isTaskLast(task),
                            onDelete = { tasksViewModel.deleteTask(it) },
                            isClicked = task == clickedTask,
                            taskBeingEdited = taskBeingEdited,
                            onTaskUpdate = { updatedTask ->
                                taskBeingEdited = null
                                Toast.makeText(context, "Task Edited.", Toast.LENGTH_SHORT).show()
                                coroutineScope.launch {
                                    tasksViewModel.updateTask(updatedTask)
                                    reminderManager.observeAndScheduleReminders(context)
                                }
//                                tasksViewModel.refreshTasks()
                            },
                            onCancel = { taskBeingEdited = null }
                        )
                    }
                    item {
                        Text(text = "${clickedTask?.taskName}")
                    }
                }
            }
        }


        // Show the add task screen
        if (isAddingTask && clickedTask != null) {
            Box(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.3f))
            ) {
                AddTaskScreen(
                    tasksViewModel = tasksViewModel,
                    clickedTask = clickedTask,
                    onAddClick = { newTask ->
                        tasksViewModel.beginNewTaskOperations(clickedTask!!, newTask)
                        isAddingTask = false
                    },
                    onCancel = { isAddingTask = false }
                )
            }
        }

//        // Show the edit task screen as a dialog overlay
//        if (taskBeingEdited != null) {
//            Log.d(tag, "Show Edit Task Screen")
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color.Black.copy(alpha = 0.3f))
//            ) {

//                EditTaskScreen(
//                    reminderManager = reminderManager,
//                    task = taskBeingEdited!!,
//                    onSave = { updatedTask ->
//                        taskBeingEdited = null
//                        Toast.makeText(context, "Task Edited.", Toast.LENGTH_SHORT).show()
//                        coroutineScope.launch {
//                            tasksViewModel.updateTask(updatedTask)
//                            reminderManager.observeAndScheduleReminders(context)
//                        }
//                        tasksViewModel.refreshTasks()
//                    },
//                    onCancel = { taskBeingEdited = null }
//                )
//            }
//        }
    }
}