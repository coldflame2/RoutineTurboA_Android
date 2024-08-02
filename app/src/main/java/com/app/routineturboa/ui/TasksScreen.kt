package com.app.routineturboa.ui

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
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

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun TasksScreen(
    paddingValues: PaddingValues,
    isAddingTask: MutableState<Boolean>,
    context: Context,
    tasksViewModel: TasksViewModel,
    reminderManager: ReminderManager
) {
    // <editor-fold desc="variables">
    val tag = "TasksScreen"

    val tasks by tasksViewModel.tasks.collectAsStateWithLifecycle()
    var clickedTask by remember { mutableStateOf<TaskEntity?>(null) }
    val hasScrolledToTarget = remember { mutableStateOf(false) }

    val authenticationResult by remember { mutableStateOf<IAuthenticationResult?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val listState = rememberLazyListState()
    val currentDateTime = remember { java.time.LocalDateTime.now() }

    // Find the index of the task whose time range includes the current time
    val targetIndex = tasks.indexOfFirst { task ->
        val startTime = task.startTime
        val endTime = task.endTime
        currentDateTime.isAfter(startTime) && currentDateTime.isBefore(endTime)
    }
    // </editor-fold>

    // Use LaunchedEffect to perform scrolling only once after the tasks are loaded
    LaunchedEffect(tasks) {
        if (tasks.isNotEmpty() && targetIndex != -1 && !hasScrolledToTarget.value) {
            listState.animateScrollToItem(targetIndex)
            hasScrolledToTarget.value = true // Set the flag to true after scrolling
        }
    }

    LaunchedEffect(authenticationResult, key2 = true) {
        Log.d(tag, "TasksScreen LaunchedEffect called")
        authenticationResult?.let {
            Log.d(tag, "Downloading from OneDrive")
        }
    }

    // Start a coroutine to delay the loading state change
    LaunchedEffect(tasks) {
        delay(50)
        isLoading = false
    }

    Column {
        // <editor-fold desc="Lazy Column-SingleTaskCard">
        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(paddingValues),
            contentPadding = PaddingValues(top = 8.dp, bottom = 50.dp)  // Adjust the padding values as needed
        ) {
            // Loading state or no tasks
            if (isLoading || tasks.isEmpty()) {
                items(2) {
                    EmptyTaskCardPlaceholder()
                }
            }

            else {
                items(
                    tasks, key = { task ->
                        task.id
                    }
                ) { task ->
                    SingleTaskCard(
                        // <editor-fold desc="SingleTaskCard Parameters"
                        context = context,
                        tasksViewModel = tasksViewModel,
                        reminderManager = reminderManager,
                        task = task,
                        onClick = { clickedTask = task },
                        canDelete = !tasksViewModel.isTaskFirst(task) && !tasksViewModel.isTaskLast(task),
                        onDelete = { tasksViewModel.deleteTask(it) },
                        isClicked = task == clickedTask
                        // </editor-fold>
                    )
                }
            }
        }  // end of lazy column
        // </editor-fold>

        Text(
            text = "${clickedTask?.name}",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(PaddingValues())
        )
    } // End of Column that contains LazyColumn


    // AddTaskScreen (Inside the parent Box)
    if (isAddingTask.value && clickedTask != null) {
        Box(
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.3f))
        ) {
            AddTaskScreen(
                tasksViewModel = tasksViewModel,
                clickedTask = clickedTask,
                onAddClick = { newTask ->
                    tasksViewModel.beginNewTaskOperations(clickedTask!!, newTask)
                    isAddingTask.value = false
                },
                onCancel = { isAddingTask.value = false }
            )
        }
    }

}