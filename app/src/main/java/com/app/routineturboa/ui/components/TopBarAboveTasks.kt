package com.app.routineturboa.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.ViewDay
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarAboveTasks(drawerState: DrawerState) {
    val currentDate: String = SimpleDateFormat("MMMM d",
        Locale.getDefault()).format(Date())
    val coroutineScope = rememberCoroutineScope()


    /**
     * title - the title to be displayed in the top app bar. This title will be used in the app bar's expanded and collapsed states, although in its collapsed state it will be composed with a smaller sized TextStyle
     * modifier - the Modifier to be applied to this top app bar
     * navigationIcon - the navigation icon displayed at the start of the top app bar. This should typically be an IconButton or IconToggleButton.
     * actions - the actions displayed at the end of the top app bar. This should typically be IconButtons. The default layout here is a Row, so icons inside will be placed horizontally.
     * windowInsets - a window insets that app bar will respect.
     * colors - TopAppBarColors that will be used to resolve the colors used for this top app bar in different states. See TopAppBarDefaults. largeTopAppBarColors.
     * scrollBehavior - a TopAppBarScrollBehavior which holds various offset values that will be applied by this top app bar to set up its height and colors. A scroll behavior is designed to work in conjunction with a scrolled content to change the top app bar appearance as the content scrolls. See TopAppBarScrollBehavior. nestedScrollConnection.
     */

    LargeTopAppBar(
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
                onClick = { /*TODO*/ }
            ) {
                Icon(
                    Icons.Outlined.ViewDay,
                    contentDescription = "View Day"
                )
            }
        },
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        //</editor-fold>
    )

}