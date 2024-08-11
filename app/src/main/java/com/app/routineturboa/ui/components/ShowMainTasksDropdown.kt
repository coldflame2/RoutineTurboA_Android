package com.app.routineturboa.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.app.routineturboa.data.local.TaskEntity

// Add a new composable to show the MainTask dropdown
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowMainTasksDropdown(
    mainTasks: List<TaskEntity>,
    selectedMainTaskId: Int?,
    onTaskSelected: (Int?) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }
    val selectedTaskName = remember(selectedMainTaskId) {
        mainTasks.find { it.id == selectedMainTaskId }?.name ?: "Select a task"
    }

    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = it },
        modifier = Modifier.fillMaxWidth(),
    ) {
        TextField(
            readOnly = true,
            value = selectedTaskName,
            onValueChange = {},
            label = { Text("Link to Main Task") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            mainTasks.forEach { task ->
                DropdownMenuItem(
                    onClick = {
                        onTaskSelected(task.id)
                        expanded.value = false
                    },
                    text = { Text(task.name) }
                )
            }
        }
    }
}