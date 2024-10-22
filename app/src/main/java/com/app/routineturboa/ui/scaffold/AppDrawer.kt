package com.app.routineturboa.ui.scaffold

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.app.routineturboa.R
import com.app.routineturboa.data.room.TaskEntity
import com.app.routineturboa.reminders.ReminderManager
import com.app.routineturboa.shared.UiStates
import com.app.routineturboa.ui.reusable.SignInAndSyncButtons
import com.app.routineturboa.ui.reusable.SmoothCircularProgressIndicator
import com.app.routineturboa.viewmodel.TasksViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AppDrawer(
    drawerState: DrawerState,
    tasksViewModel: TasksViewModel,
    reminderManager: ReminderManager,
    showExactAlarmDialog: MutableState<Boolean>,
    onShowCompletedTasks: () -> Unit,
    uiStates: UiStates,
    clickedTask: TaskEntity?,
    selectedDate: LocalDate
) {
    val tag = "MainDrawer"
    val context = LocalContext.current
    val appName = context.getString(R.string.app_name)
    val coroutineScope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    ModalDrawerSheet(
        drawerTonalElevation = 1.dp,  // translucent primary color overlay
        drawerShape = RectangleShape,
        modifier = Modifier
            .width(screenWidth * 0.7f)
            .height(screenHeight * 0.90f)
            .padding(top = 12.dp)
            .offset(x = if (drawerState.isClosed) -screenWidth else 0.dp)
            .shadow(10.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // region: Header with App Name
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha=0.8f),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 10.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.routineturbo),
                        contentDescription = "App Icon",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = appName,
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        Icons.AutoMirrored.Default.MenuOpen,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                coroutineScope.launch { drawerState.close() }
                            }
                    )
                }
            }

            SignInAndSyncButtons(tasksViewModel)

            // test reminder
            DrawerItemTemplate(
                "Test Reminder",
                Icons.Default.DeveloperMode,
                drawerState
            ) {
                val clickedTaskId = clickedTask?.id
                if (clickedTaskId != null) {
                    coroutineScope.launch {
                        val clickedTaskName = tasksViewModel.getTaskName(clickedTaskId)
                        if (clickedTaskName != null) {
                            reminderManager.manuallyTriggerReminder(clickedTaskId, clickedTaskName)
                        }
                    }
                }

            }

            ScheduleRemindersButton(
                drawerState,
                reminderManager,
                selectedDate,
                showExactAlarmDialog,
                context
            )

            DrawerItemTemplate("Insert Basic Tasks", Icons.Default.AddTask, drawerState) {
                coroutineScope.launch {
                    tasksViewModel.insertBasicTasks()

                }
            }

            DrawerItemTemplate("Insert Sample Tasks", Icons.Default.Add, drawerState) {
                coroutineScope.launch {
                    tasksViewModel.insertSampleTasks()
                }
            }

            DrawerItemTemplate("Delete All", Icons.Default.Settings, drawerState) {
                coroutineScope.launch {
                    tasksViewModel.deleteAllTasks()
                }
            }

            DrawerItemTemplate("Show Completed Tasks", Icons.Default.ChecklistRtl, drawerState) {
                Log.d(tag, "Show Completed Tasks clicked on drawer...")
                coroutineScope.launch {
                    tasksViewModel.loadTaskCompletions()
                    onShowCompletedTasks()
                }
            }

            DrawerItemTemplate(
                text = "Log tasks",
                icon = Icons.Default.DeveloperMode,
                drawerState = drawerState
            ) {
                coroutineScope.launch {
                    val tasks = tasksViewModel.tasksByDate.first() // This will get the current value of allTasks and then stop observing
                    Log.d(tag, "***************\n")
                    Log.d(tag, "$tasks")
                }
            }
        }
    }
}


@Composable
fun DrawerItemTemplate(
    text: String,
    icon: ImageVector,
    drawerState: DrawerState,
    onItemClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                coroutineScope.launch {
                    onItemClick()
                    drawerState.close()  // Close the drawer after the action
                }
            }
            .padding(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ScheduleRemindersButton(
    drawerState: DrawerState,
    reminderManager: ReminderManager,
    selectedDate: LocalDate?,
    showExactAlarmDialog: MutableState<Boolean>,
    context: Context
) {
    val coroutineScope = rememberCoroutineScope()
    val isLoading = remember { mutableStateOf(false) } // State to manage loading

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                coroutineScope.launch {
                    isLoading.value = true // Show loading spinner
                    val success = reminderManager.scheduleAllReminders(
                        selectedDate ?: LocalDate.now()
                    )

                    if (!success) {
                        Log.e(
                            "ReminderSchedule",
                            "Exact alarm permission not granted. Showing dialog."
                        )
                        showExactAlarmDialog.value = true
                    } else {
                        Toast
                            .makeText(context, "Reminders scheduled...", Toast.LENGTH_SHORT)
                            .show()
                    }

                    isLoading.value = false
                    drawerState.close()
                }
            }
            .padding(16.dp)
    ) {
        // Display CircularProgressIndicator if loading, otherwise the icon
        if (isLoading.value) {
            SmoothCircularProgressIndicator(
                modifier = Modifier.size(24.dp) // Small circular loading indicator
            )
        } else {
            Icon(
                imageVector = Icons.Default.Alarm,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "Schedule Reminders",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
