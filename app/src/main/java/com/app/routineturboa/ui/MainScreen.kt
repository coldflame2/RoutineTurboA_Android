package com.app.routineturboa.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.app.routineturboa.reminders.ReminderManager
import com.app.routineturboa.ui.components.ContentForDrawer
import com.app.routineturboa.ui.components.TopBar
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainScreen(
    windowWidthSizeClass: WindowWidthSizeClass,
    hasNotificationPermission: Boolean,
    onRequestPermission: () -> Unit,
    reminderManager: ReminderManager
) {
    Log.d("MainScreen", "MainScreen starts...")

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var showPermissionDialog by remember { mutableStateOf(!hasNotificationPermission) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Log.d("MainScreen", "Current WindowWidthSizeClass: $windowWidthSizeClass")
    val drawerWidth = when (windowWidthSizeClass) {
        WindowWidthSizeClass.Compact -> {screenWidth * 0.75f}
        WindowWidthSizeClass.Medium -> {screenWidth * 0.65f}
        WindowWidthSizeClass.Expanded -> {screenWidth * 0.45f}
        else -> { screenWidth * 0.75f }
    }

    Log.d("MainScreen", "Calculated drawer width: $drawerWidth")

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(drawerWidth)
                    .fillMaxHeight()
                    .offset(x = if (drawerState.isClosed) -drawerWidth else 0.dp)
            ) {
                ContentForDrawer(reminderManager) {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                }
            }
        },
    ) {
        Scaffold(
            topBar = { TopBar(drawerState) },
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                TasksScreen(reminderManager)
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