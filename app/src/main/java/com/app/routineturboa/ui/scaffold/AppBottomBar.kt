package com.app.routineturboa.ui.scaffold

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun AppBottomBar(
    onAddNewTaskClick: suspend () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val items = listOf("Home", "New")
    var selectedItemIndex by remember { mutableIntStateOf(0) }

    // Add New Task Icon
    val addNewTaskIcon = @Composable {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add"
        )
    }

    val tasksIcon = @Composable {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Tasks"
        )
    }


    NavigationBar (
        Modifier.height(65.dp)
    ) {
        // Home Button
        NavigationBarItem(
                label = { Text (items[0])},
                onClick = { selectedItemIndex = 0 },
                selected = (selectedItemIndex == 0),
                icon = tasksIcon,
            )

        // Add New Task Button
        NavigationBarItem(
            label = { Text (items[1])},
            onClick = {
                coroutineScope.launch { onAddNewTaskClick() }
            },
            selected = (selectedItemIndex == 1),
            icon = addNewTaskIcon
        )
    }
}