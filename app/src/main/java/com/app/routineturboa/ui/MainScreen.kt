package com.app.routineturboa.ui

import TaskViewModelFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.routineturboa.data.local.RoutineRepository
import com.app.routineturboa.reminders.ReminderManager
import com.app.routineturboa.ui.components.ContentForDrawer
import com.app.routineturboa.ui.components.TopBar
import com.app.routineturboa.viewmodel.TasksViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    hasNotificationPermission: Boolean,
    onRequestPermission: () -> Unit,
    reminderManager: ReminderManager
) {
    Log.d("MainScreen", "MainScreen starts...")

    val context = LocalContext.current

    val taskViewModelFactory = remember { TaskViewModelFactory(RoutineRepository(context)) }
    val tasksViewModel: TasksViewModel = viewModel(factory = taskViewModelFactory)

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var showPermissionDialog by remember { mutableStateOf(!hasNotificationPermission) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val insets = WindowInsets.statusBars
    val navInsets = WindowInsets.navigationBars
    val insetsPadding = insets.asPaddingValues()

    ModalNavigationDrawer(
        drawerState = drawerState,
        modifier = Modifier.consumeWindowInsets(navInsets),
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerTonalElevation = 9.dp,
                modifier = Modifier
                    .width(screenWidth * 0.5f)
                    .fillMaxHeight()
                    .padding(top = 10.dp)
                    .offset(x = if (drawerState.isClosed) -screenWidth else 0.dp)
            ) {
                ContentForDrawer(tasksViewModel, reminderManager, onCloseDrawer = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                }, onItemClicked = {
                    Log.d("MainScreen", "An item in the drawer was clicked")
                })
            }
        },
        scrimColor = Color.Blue
    ) {
        Scaffold(
            topBar = { TopBar(drawerState) },
            contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
        ) { paddingValues ->
            Row(modifier = Modifier.consumeWindowInsets(paddingValues).padding(paddingValues),
            ) {
                TasksScreen(context, tasksViewModel, reminderManager)
            }
        }

        if (showPermissionDialog) {
            AlertDialog(
                onDismissRequest = { showPermissionDialog = false },
                title = { Text("Notification Permission") },
                text = { Text("This app needs notification permission to send you reminders. Would you like to grant this permission?") },
                confirmButton = {
                    Button(onClick = {
                        showPermissionDialog = false
                        onRequestPermission()
                    }) {
                        Text("Grant Permission")
                    }
                },
                dismissButton = {
                    Button(onClick = { showPermissionDialog = false }) {
                        Text("Not Now")
                    }
                }
            )
        }
    }
}