package com.app.routineturboa.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp


@Composable
fun TaskDropdownMenu(
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit,
    canDelete: Boolean,
    onDeleteClick: () -> Unit,
    onDismissRequest: () -> Unit,
    offset: DpOffset,
) {
    DropdownMenu(
        expanded = true,
        onDismissRequest = onDismissRequest,
        offset = offset,
        modifier = modifier
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text = "Edit",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(8.dp)
                )
            },
            onClick = onEditClick
        )

        HorizontalDivider()

        DropdownMenuItem(
            text = {
                Text(
                    text = "Delete",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(8.dp)
                )
            },
            onClick = onDeleteClick,
            enabled = canDelete
        )
    }
}