package com.app.routineturboa.ui.scaffold

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    drawerState: DrawerState,
    selectedDate: LocalDate?,
    onDatePickerClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    // Formatting the current date for display in the TopAppBar title
    val dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())
    val currentDate = selectedDate?.format(dateFormatter)

    TopAppBar(
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
        ),

        title = { Text(text = currentDate?: "{Set Date}") },

        // To open the drawer
        navigationIcon = {
            IconButton(
                onClick = { coroutineScope.launch { drawerState.open() } }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.MenuOpen,
                    modifier = Modifier.rotate(180f), // Rotates the icon 180 degrees
                    contentDescription = "Open Drawer",
                )
            }
        },

        actions = {
            // PickDateDialog button
            IconButton(
                onClick = onDatePickerClick
            ) {
                Icon(
                    Icons.Outlined.CalendarMonth,
                    contentDescription = "View Day"
                )
            }
        }
    )

}
