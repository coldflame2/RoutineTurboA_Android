package com.app.routineturboa.ui.scaffold

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.ReadMore
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    drawerState: DrawerState,
    selectedDate: LocalDate,
    onDatePickerClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    // Formatting the current date for display in the TopAppBar title
    val dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())
    val currentDate = selectedDate.format(dateFormatter)

    TopAppBar(
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
        ),

        title = { Text(text = currentDate) },

        // To open the drawer
        navigationIcon = {
            IconButton(
                onClick = { coroutineScope.launch { drawerState.open() } }
            ) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Menu"
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
