package com.app.routineturboa.ui.task

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp


@Composable
fun TaskDropdownMenu(
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit,
    onViewClick: () -> Unit,
    canDelete: Boolean,
    onDeleteClick: () -> Unit,
    onDismissRequest: () -> Unit,
    offset: DpOffset,
) {
    DropdownMenu(
        expanded = true,
        onDismissRequest = onDismissRequest,
        offset = offset,
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text = "Edit",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            },
            onClick = onEditClick,
        )

        HorizontalDivider(
            thickness = 2.dp
        )

        DropdownMenuItem(
            text = {
                Text(
                    text = "View Details",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (canDelete) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier
                        .alpha(if (canDelete) 1f else 0.3f)
                )
            },
            onClick = onViewClick,
            colors = MenuDefaults.itemColors(
                disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
            )
        )

        DropdownMenuItem(
            text = {
                Text(
                    text = "Delete",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (canDelete) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier
                        .alpha(if (canDelete) 1f else 0.3f)
                )
            },
            onClick = onDeleteClick,
            enabled = canDelete,
            colors = MenuDefaults.itemColors(
                disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
            )
        )
    }
}