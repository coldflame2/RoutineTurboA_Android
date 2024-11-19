package com.app.routineturboa.ui.tasks.dropdowns

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.routineturboa.core.dbutils.RecurrenceType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectRecurrenceTypeDropdown(
    selectedRecurrenceType: RecurrenceType?,
    onRecurrenceTypeSelected: (RecurrenceType) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }
    val allRecurrenceTypes = RecurrenceType.entries.map { it.name } // Convert to list of strings
    val backgroundColor = MaterialTheme.colorScheme.surfaceTint
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant

    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = it },
    ) {
        TextField(
            label = { Text("Recurrence Type") },
            readOnly = true,
            value = selectedRecurrenceType?.name ?: RecurrenceType.DAILY.name,
            onValueChange = {},
            modifier = Modifier
                .menuAnchor(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
            },
            colors = TextFieldDefaults.colors(
                // region: colors
                // **Container Colors**
                focusedContainerColor = backgroundColor.copy(alpha = 0.09f),
                unfocusedContainerColor = backgroundColor.copy(alpha = 0.25f),
                disabledContainerColor = backgroundColor.copy(alpha = 0.25f),

                errorContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.2f),

                // **Text Colors**
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                disabledTextColor = textColor,

                // **Label Colors**
                focusedLabelColor = textColor,
                unfocusedLabelColor = textColor,
                disabledLabelColor = textColor,
                errorLabelColor = MaterialTheme.colorScheme.onError,

                // **Placeholder Colors**
                focusedPlaceholderColor = textColor,
                unfocusedPlaceholderColor = textColor,
                disabledPlaceholderColor = textColor,
                errorPlaceholderColor = MaterialTheme.colorScheme.error,

                // **Leading Icon Colors**
                focusedLeadingIconColor = textColor,
                unfocusedLeadingIconColor = textColor,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSecondary,
                errorLeadingIconColor = MaterialTheme.colorScheme.error,

                // **Trailing Icon Colors**
                focusedTrailingIconColor = textColor,
                unfocusedTrailingIconColor = textColor,
                disabledTrailingIconColor = textColor,
                errorTrailingIconColor = MaterialTheme.colorScheme.error,

                // **Indicator Colors**
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = textColor,
                errorIndicatorColor = MaterialTheme.colorScheme.onError,

                // **Cursor Color**
                errorCursorColor = MaterialTheme.colorScheme.error
                //endregion
            ),
        )

        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
        ) {
            allRecurrenceTypes.forEachIndexed { index, taskTypeName ->
                DropdownMenuItem(
                    onClick = {
                        onRecurrenceTypeSelected(RecurrenceType.valueOf(taskTypeName)) // Convert string back to enum
                        expanded.value = false
                    },
                    text = {
                        Text(
                            taskTypeName,
                            fontSize = 14.sp // Set smaller text size
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
                if (index < allRecurrenceTypes.size - 1) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp)) // Add a divider with padding
                }
            }
        }
    }
}
