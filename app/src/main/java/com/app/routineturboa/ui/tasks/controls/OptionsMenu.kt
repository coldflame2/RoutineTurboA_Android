package com.app.routineturboa.ui.tasks.controls

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

    /**
     * @name TaskDropDownMenu
     * This has to be inside the same box for DropdownMenu to be positioned correctly.
     *
     * Do not move it outside the parent box.
     */

@Composable
fun OptionsMenu(
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit,
    onViewClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDismissRequest: () -> Unit,
    offset: DpOffset,
) {
    val canDelete = true // Temporarily

    DropdownMenu(
        expanded = true,
        onDismissRequest = onDismissRequest,
        offset = offset,
    ) {
        // "Edit" item
        DropdownMenuItem(
            onClick = onEditClick,
            text = {
                Text(
                    text = "Edit",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            },
        )

        HorizontalDivider(thickness = 2.dp)

        // "View Details" Item
        DropdownMenuItem(
            onClick = onViewClick,
            colors = MenuDefaults.itemColors(
                disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
            ),
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
        )

        // "Delete" item
        DropdownMenuItem(
            onClick = onDeleteClick,
            enabled = canDelete,
            colors = MenuDefaults.itemColors(
                disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
            ),
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
        )
    }
}