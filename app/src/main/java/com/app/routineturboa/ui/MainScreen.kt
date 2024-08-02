package com.app.routineturboa.ui

import TaskViewModelFactory
import android.graphics.BlurMaskFilter
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.routineturboa.data.local.RoutineRepository
import com.app.routineturboa.reminders.ReminderManager
import com.app.routineturboa.ui.components.ItemsInsideDrawer
import com.app.routineturboa.ui.components.MainBarAboveTasks
import com.app.routineturboa.ui.components.TasksNavBar
import com.app.routineturboa.viewmodel.TasksViewModel

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainScreen(reminderManager: ReminderManager) {
    Log.d("MainScreen", "MainScreen starts...")

    val context = LocalContext.current
    val taskViewModelFactory = remember { TaskViewModelFactory(RoutineRepository(context)) }
    val tasksViewModel: TasksViewModel = viewModel(factory = taskViewModelFactory)
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val isAddingTask = remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        scrimColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f), // Rest of the UI color
        drawerState = drawerState,
        drawerContent = { ItemsInsideDrawer(drawerState, tasksViewModel, reminderManager) },
    ) {
        Scaffold(
            //  <editor-fold desc="Main Scaffold">
            topBar = { MainBarAboveTasks(drawerState) },
            bottomBar = { TasksNavBar() },
            floatingActionButton = {
                FloatingActionButton (
                    onClick = { isAddingTask.value = true },
                    elevation = FloatingActionButtonDefaults.elevation(6.dp),
                    modifier = Modifier.padding(end = 30.dp)
                ) { Text(text = "New") }
            },
            floatingActionButtonPosition = FabPosition.End,

            // </editor-fold>
        ) { paddingValues ->
            TasksScreen(paddingValues, isAddingTask, context, tasksViewModel, reminderManager)
        }
    }
}