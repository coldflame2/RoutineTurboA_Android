package com.app.routineturboa.ui.scaffold

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun AppBottomBar(
    onShowAddNewTaskClick: suspend () -> Unit,
    onShowFullEditTaskClick: suspend () -> Unit,
    onShowQuickEditTaskClick: suspend () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedItemIndex by remember { mutableIntStateOf(0) }
    val itemColor = NavigationBarItemColors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.onPrimary,
        selectedIndicatorColor = MaterialTheme.colorScheme.tertiary,
        unselectedIconColor = MaterialTheme.colorScheme.secondary,
        unselectedTextColor = MaterialTheme.colorScheme.onSecondary,
        disabledTextColor = MaterialTheme.colorScheme.surfaceVariant,
        disabledIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    NavigationBar(
        Modifier.height(65.dp)
    ) {
        // Home Button
        NavigationBarItem(
            label = { Text(NavigationItem.HOME.label) },
            icon = { Icon(
                imageVector = NavigationItem.HOME.icon,
                contentDescription = NavigationItem.HOME.label) },
            selected = (selectedItemIndex == 0),
            onClick = { selectedItemIndex = 0 },
            colors = itemColor
        )

        // QuickEdit Button
        NavigationBarItem(
            label = { Text(NavigationItem.QUICKEDIT.label) },
            icon = { Icon(
                imageVector = NavigationItem.QUICKEDIT.icon,
                contentDescription = NavigationItem.QUICKEDIT.label) },
            selected = (selectedItemIndex == 2),
            onClick = { selectedItemIndex = 2
                coroutineScope.launch {
                    onShowQuickEditTaskClick()
                } },
            colors = itemColor
        )


        // Edit Button
        NavigationBarItem(
            label = { Text(NavigationItem.EDIT.label) },
            icon = { Icon(
                imageVector = NavigationItem.EDIT.icon,
                contentDescription = NavigationItem.EDIT.label) },
            selected = (selectedItemIndex == 3),
            onClick = { selectedItemIndex = 3
                coroutineScope.launch {
                    onShowFullEditTaskClick()
                } },
            colors = itemColor
        )

        // New Button with a coroutine for adding a new task
        NavigationBarItem(
            label = { Text(NavigationItem.NEW.label) },
            icon = { Icon(imageVector = NavigationItem.NEW.icon,
                contentDescription = NavigationItem.NEW.label) },
            selected = (selectedItemIndex == 4),
            onClick = {
                selectedItemIndex = 4
                coroutineScope.launch {
                    onShowAddNewTaskClick()
                }
            },
            colors = itemColor
        )
    }
}

// enum with label and icon for each item
enum class NavigationItem(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    EDIT("Edit", Icons.Default.Edit),
    QUICKEDIT("Q-Edit", Icons.Default.EditNote),
    NEW("New", Icons.Default.Add),

}