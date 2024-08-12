package com.app.routineturboa.ui.main.scaffold

import android.os.Build
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
import androidx.compose.material.icons.automirrored.outlined.MenuOpen
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.app.routineturboa.R
import com.app.routineturboa.reminders.ReminderManager
import com.app.routineturboa.ui.components.SignInItem
import com.app.routineturboa.viewmodel.TasksViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainDrawer(
    drawerState: DrawerState,
    tasksViewModel: TasksViewModel,
    reminderManager: ReminderManager
) {
    val context = LocalContext.current
    val appName = context.getString(R.string.app_name)
    val coroutineScope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    ModalDrawerSheet(
        //region ModalDrawerSheet Parameters
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerContentColor = MaterialTheme.colorScheme.onSurface,
        drawerTonalElevation = 9.dp,

        modifier = Modifier
            .width(screenWidth * 0.7f)
            .height(screenHeight * 0.90f)
            .padding(top = 12.dp)
            .offset(x = if (drawerState.isClosed) - screenWidth else 0.dp)
            .shadow(15.dp),
        drawerShape = RectangleShape
        //endregion
    ) {
        // Content inside drawer
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top Header
            Surface(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.shadow(15.dp)
            ) {
                // Top Item with App Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 10.dp)
                ) {
                    // region App Main Icon
                    Image(
                        painter = painterResource(id = R.drawable.routineturbo),
                        contentDescription = "App Icon",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                    // endregion

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = appName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        Icons.AutoMirrored.Outlined.MenuOpen,
                        contentDescription = null, // Handle accessibility if needed
                        modifier = Modifier
                            .size(24.dp) // Adjust size as necessary
                            .clickable {
                                coroutineScope.launch { drawerState.close() }
                            },
                        tint = Color.Gray
                    )
                }
            }

            SignInItem()

            // schedule reminders
            DrawerItemTemplate("Schedule Reminders", Icons.Default.Build) {
                coroutineScope.launch {
                    reminderManager.observeAndScheduleReminders(context)
                }
            }

            // Insert Demo Tasks
            DrawerItemTemplate("Insert Demo Tasks", Icons.Default.Settings) {
                coroutineScope.launch {
                    tasksViewModel.insertDemoTasks(context)
                }
            }
        }
    }
}

@Composable
fun DrawerItemTemplate(text: String, icon: ImageVector, onItemClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
            .padding(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null, // Handle accessibility if needed
            modifier = Modifier.size(24.dp) // Adjust size as necessary
        )
        Spacer(modifier = Modifier.width(16.dp)) // Adjust spacing between icon and text
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

