package com.app.routineturboa.ui.scaffold

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.routineturboa.ui.models.TaskFormData

@Composable
fun MainBottomBar(
    onAddNewClick: () -> Unit,  // For setting isAddingTask state True
) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Home", "Tasks")
    val icons = listOf(Icons.Filled.Home, Icons.AutoMirrored.Filled.List, Icons.Filled.Settings)

    NavigationBar (
        Modifier.height(65.dp)
    ) {
        items.forEachIndexed { index, item ->

            NavigationBarItem(
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = { selectedItem = index },
                icon = { Icon(icons[index], contentDescription = item) },
            )

        }

        // Add New Task Button
        NavigationBarItem(
            label = { Text("Add") },
            selected = true,
            onClick = onAddNewClick, // For setting isAddingTask state True
            icon = {
                Icon (
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            },
        )
    }
}