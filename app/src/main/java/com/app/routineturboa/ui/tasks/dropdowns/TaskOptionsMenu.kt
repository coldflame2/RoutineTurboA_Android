package com.app.routineturboa.ui.tasks.dropdowns

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.app.routineturboa.core.models.DataOperationEvents
import com.app.routineturboa.core.models.StateChangeEvents
import com.app.routineturboa.data.room.entities.TaskEntity
import com.app.routineturboa.ui.theme.LocalCustomColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TaskOptionsMenu (
    task: TaskEntity,
    coroutineScope: CoroutineScope,
    stateChangeEvents: StateChangeEvents,
    dataOperationEvents: DataOperationEvents,
    isThisTaskLongPressMenu: Boolean = true,
    bgColor: Color = Color.LightGray,
) {
    val tag = "TaskOptionsMenu"
    val customColors = LocalCustomColors.current

    Box {

        IconButton(
            onClick = { stateChangeEvents.onTaskLongPress(task) },
            modifier = Modifier.size(30.dp),
            colors = IconButtonColors(
                containerColor = bgColor.copy(alpha = 0.3f),
                contentColor = customColors.taskNameFontColor,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = customColors.taskNameFontColor.copy(alpha = 0.3f)
            )
        ) {
            Icon(
                imageVector = if (isThisTaskLongPressMenu) Icons.Default.ArrowDropUp
                else Icons.Default.ArrowDropDown,
                contentDescription = "Options",
            )
        }

        DropdownMenu(
            expanded = isThisTaskLongPressMenu,
            onDismissRequest = { stateChangeEvents.onDismissOrReset(task) },
            offset = DpOffset((-5).dp, (-7).dp),
            properties = PopupProperties(
                focusable = false, // Allow touch events to pass through
                dismissOnClickOutside = true,
                dismissOnBackPress = true,
            )
        ) {
            DropdownMenuItem(
                text = { Text("View Details") },
                onClick = {
                    stateChangeEvents.onShowTaskDetailsClick(task)
                },
            )

            DropdownMenuItem(
                text = { Text("Edit") },
                onClick = {
                    coroutineScope.launch {
                        stateChangeEvents.onShowFullEditClick()
                    }
                }
            )

            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    coroutineScope.launch {
                        dataOperationEvents.onDeleteTaskConfirmClick(task)
                    }
                }
            )
        }
    }
}