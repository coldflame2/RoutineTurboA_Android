package com.app.routineturboa.ui.scaffold

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.ArrowCircleLeft
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.routineturboa.core.models.UiScreen
import com.app.routineturboa.core.models.DataOperationEvents
import com.app.routineturboa.core.models.StateChangeEvents
import com.app.routineturboa.core.models.UiState
import com.app.routineturboa.ui.tasks.dropdowns.AccountOptionsSheet
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    drawerState: DrawerState,
    selectedDate: LocalDate?,
    uiState: UiState,
    onStateEvents: StateChangeEvents,
    onDataEvents: DataOperationEvents,
) {
    val coroutineScope = rememberCoroutineScope()

    // Formatting the current date for display in the TopAppBar title
    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM", Locale.getDefault())
    val currentDate = selectedDate?.format(dateFormatter) ?: "{Set Date}"

    val isEditingOrAddingNew = uiState.uiScreen is UiScreen.FullEditing ||
            uiState.uiScreen is UiScreen.QuickEditOverlay ||
            uiState.uiScreen is UiScreen.AddingNew

    Log.d("AppTopBar", "uiState.uiScreen is ${uiState.uiScreen}")

    val titleText = if (isEditingOrAddingNew) "Enter the task details" else currentDate

    val navigationIconClickAction: () -> Unit = if (isEditingOrAddingNew) {
        { onStateEvents.onDismissOrReset(uiState.taskContext.clickedTask) }
    } else {
        { coroutineScope.launch { drawerState.open() } }
    }

    val navigationIconImage = if (isEditingOrAddingNew)
        Icons.Default.ArrowCircleLeft
    else
        Icons.AutoMirrored.Filled.MenuOpen

    val navigationIconRotation = if (isEditingOrAddingNew) 0f else 180f

    val navigationIconContentDescription = if (isEditingOrAddingNew)
        "Cancel Editing"
    else
        "Open Drawer"

    val msalAuthState = uiState.msalAuthState
    val profileImageUrl = msalAuthState.profileImageUrl
    val signInStatus = msalAuthState.signInStatus

    var showAccountSheet by remember { mutableStateOf(false) }

    TopAppBar(
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),

        title = { Text(text = titleText) },

        navigationIcon = {
            IconButton(
                onClick = { navigationIconClickAction() }
            ) {
                Icon(
                    imageVector = navigationIconImage,
                    modifier = Modifier.rotate(navigationIconRotation),
                    contentDescription = navigationIconContentDescription,
                )
            }
        },

        actions = {
            if (isEditingOrAddingNew) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            // # TODO("Add save function for new task.")
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.Save,
                        contentDescription = "Confirm Editing"
                    )
                }
            } else {
                IconButton(
                    onClick = onStateEvents.onShowDatePickerClick
                ) {
                    Icon(
                        Icons.Outlined.CalendarMonth,
                        contentDescription = "View Day"
                    )
                }

                // Account button
                IconButton(onClick = { showAccountSheet = !showAccountSheet }) {
                    if (!profileImageUrl.isNullOrEmpty()) {
                        // Show the profile image if available
                        AsyncImage(
                            model = profileImageUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape), // Make the image circular
                            contentScale = ContentScale.Crop // Crop the image to fit the circle
                        )
                    } else {
                        // Fallback to person icon if no profile image
                        Icon(
                            imageVector = if (msalAuthState.isSignedIn) Icons.Default.Person else Icons.Outlined.Person,
                            contentDescription = "Account Options",
                            tint = if (msalAuthState.isSignedIn) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.error
                            },
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    )

    // Display the account menu
    if (showAccountSheet) {
        AccountOptionsSheet(
            uiState = uiState,
            onSignOutClick = { onDataEvents.onSignOutClick(it) },
            onSyncClick = { onDataEvents.onSyncClick(it) },
            onDismiss = { showAccountSheet = false }
        )
    }
}


