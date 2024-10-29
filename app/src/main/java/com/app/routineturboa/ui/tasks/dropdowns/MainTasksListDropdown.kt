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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.app.routineturboa.data.room.entities.TaskEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTasksListDropdown(
    mainTasksRequested: suspend () -> List<TaskEntity>,    // Function to get all main tasks
    selectedLinkedMainTask: Int?,                          // Currently selected linked main task ID
    onLinkedMainTaskSelected: (Int) -> Unit                // Callback for when a task is selected
) {
    val expanded = remember { mutableStateOf(false) }

    // State to hold all main tasks
    val allMainTasksOptions = remember { mutableStateOf<List<TaskEntity>>(emptyList()) }

    // State for the selected task's name
    var selectedLinkedMainTaskName by remember { mutableStateOf("") }

    // Fetch the main tasks only once
    LaunchedEffect(Unit) {
        allMainTasksOptions.value = mainTasksRequested()
    }

    // Fetch and display the name of the selected linked main task
    LaunchedEffect(selectedLinkedMainTask) {
        selectedLinkedMainTaskName = selectedLinkedMainTask?.let {
            allMainTasksOptions.value.find { task -> task.id == it }?.name
        } ?: ""
    }

    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = it },
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            label = { Text("Linked Main Task") },
            value = selectedLinkedMainTaskName,
            onValueChange = {},
            readOnly = true,
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
                focusedContainerColor = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.09f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.25f),
                focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            allMainTasksOptions.value.forEach { task ->
                DropdownMenuItem(
                    onClick = {
                        onLinkedMainTaskSelected(task.id)
                        expanded.value = false
                    },
                    text = { Text(task.name) } // Display the name of each main task
                )
            }
        }
    }
}
