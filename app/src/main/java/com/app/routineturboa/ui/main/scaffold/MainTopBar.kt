package com.app.routineturboa.ui.main.scaffold

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.ViewDay
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(drawerState: DrawerState) {
    val currentDate: String = SimpleDateFormat("MMMM d",
        Locale.getDefault()).format(Date())
    val coroutineScope = rememberCoroutineScope()

    val scrollBehavior = exitUntilCollapsedScrollBehavior()

    TopAppBar(
        //<editor-fold desc="TopAppBar Parameters">
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        title = { Text(text = currentDate) },
        navigationIcon = {
            IconButton(
                onClick = { coroutineScope.launch { drawerState.open() } }
            ) {
                Icon(
                    Icons.Outlined.Menu,
                    contentDescription = "Menu"
                )
            }
        },
        actions = {
            IconButton(
                onClick = { /*TODO: Main Top Bar button action.*/ }
            ) {
                Icon(
                    Icons.Outlined.ViewDay,
                    contentDescription = "View Day"
                )
            }
        },
        scrollBehavior = scrollBehavior,
        //</editor-fold>
    )
}
