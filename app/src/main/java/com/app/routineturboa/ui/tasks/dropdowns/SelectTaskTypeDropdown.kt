package com.app.routineturboa.ui.tasks.dropdowns

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.app.routineturboa.core.utils.TaskTypes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectTaskTypeDropdown(
    selectedTaskType: String,
    onTaskTypeSelected: (String) -> Unit,
    selectedLinkedMainTask: (Int?),
    onLinkedMainTaskSelected: (Int) -> Unit,
    taskTypeErrorMessage: String?
) {
    val expanded = remember { mutableStateOf(false) }

    val allTaskTypesOptions = TaskTypes.getAllTaskTypes()

    val backgroundColor = MaterialTheme.colorScheme.surfaceTint
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant

    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = it },
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            label = { Text("Task Type") },
            value = selectedTaskType,
            onValueChange = {},
            readOnly = true,
            isError = (taskTypeErrorMessage != null),
            supportingText = {
                if (taskTypeErrorMessage != null) {
                    Text(
                        text = taskTypeErrorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = if (expanded.value) "Collapse" else "Expand"
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = if (expanded.value) "Collapse" else "Expand"
                )
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
            modifier = Modifier.fillMaxWidth()
        ) {
            allTaskTypesOptions.forEach { taskType ->
                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth(),
                    text = { Text(taskType) },
                    onClick = {
                        onTaskTypeSelected(taskType) // Notify parent of the selection
                        expanded.value = false
                    },
                )
            }
        }
    }


}
