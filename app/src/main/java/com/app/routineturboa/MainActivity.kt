package com.app.routineturboa

import RoutineDrawerContent
import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.app.routineturboa.data.local.RoutineRepository
import com.app.routineturboa.reminders.ReminderManager
import com.app.routineturboa.ui.MainScreen
import com.app.routineturboa.ui.theme.RoutineTurboATheme
import com.app.routineturboa.utils.PermissionUtils
import com.app.routineturboa.utils.TimeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var reminderManager: ReminderManager

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")

        reminderManager = ReminderManager(this)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("MainActivity", "Notification permission granted")
                scheduleRemindersForAllTasks()
            } else {
                Log.d("MainActivity", "Notification permission denied")
            }
        }

        if (PermissionUtils.hasNotificationPermission(this)) {
            scheduleRemindersForAllTasks()
        }

        setContent {
            RoutineTurboATheme {
                MainScreenContent(
                    hasNotificationPermission = PermissionUtils.hasNotificationPermission(this),
                    onRequestPermission = { requestNotificationPermission() }
                )
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            scheduleRemindersForAllTasks()
        }
    }

    private fun scheduleRemindersForAllTasks() {
        CoroutineScope(Dispatchers.IO).launch {
            val dbRepository = RoutineRepository(applicationContext)
            val tasks = dbRepository.getAllTasks()
            tasks.forEach { task ->
                task.reminder.let { reminderTime ->
                    Log.d("MainActivity", "Reminder time: $reminderTime")
                    val reminderMillis = TimeUtils.timeStringToMilliseconds(reminderTime)
                    if (reminderMillis > System.currentTimeMillis()) {
                        reminderManager.scheduleReminder(task.id, reminderMillis)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
    hasNotificationPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val drawerWidth = with(LocalConfiguration.current.screenWidthDp.dp) { value * 2 / 3f }
    val currentDate: String = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date())

    var showPermissionDialog by remember { mutableStateOf(!hasNotificationPermission) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(drawerWidth.dp)
            ) {
                RoutineDrawerContent {
                    coroutineScope.launch { drawerState.close() }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    title = { Text(text = currentDate) },
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                MainScreen()
            }
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