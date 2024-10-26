package com.app.routineturboa.ui.scaffold

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.ArrowCircleLeft
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import com.app.routineturboa.shared.ActiveOverlayComponent
import com.app.routineturboa.shared.DataOperationEvents
import com.app.routineturboa.shared.StateChangeEvents
import com.app.routineturboa.shared.UiState
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

    val isEditingOrAddingNew = uiState.activeOverlayComponent is ActiveOverlayComponent.FullEditing ||
            uiState.activeOverlayComponent is ActiveOverlayComponent.QuickEditing ||
            uiState.activeOverlayComponent is ActiveOverlayComponent.AddingNew

    val titleText = if (isEditingOrAddingNew) "Enter the task details" else currentDate

    val navigationIconClickAction: () -> Unit = if (isEditingOrAddingNew) {
        onStateEvents.onCancelClick
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
                         onDataEvents.onNewTaskConfirmClick
                     }
                 ) {
                     Icon(
                         Icons.Default.Save,
                         contentDescription = "Confirm Editing"
                     )
                 }
            }
            else {
                IconButton(
                    onClick = onStateEvents.onShowDatePickerClick
                ) {
                    Icon(
                        Icons.Outlined.CalendarMonth,
                        contentDescription = "View Day"
                    )
                }
            }
        }
    )
}

