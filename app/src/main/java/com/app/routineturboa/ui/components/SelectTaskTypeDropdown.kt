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
import androidx.compose.ui.platform.LocalContext
import com.app.routineturboa.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectTaskTypeDropdown(
    selectedTaskType: String,
    onTaskTypeSelected: (String) -> Unit
) {
    val context = LocalContext.current
        val taskTypes = listOf(
            context.getString(R.string.task_type_main),
            context.getString(R.string.task_type_basics),
            context.getString(R.string.task_type_helper),
            context.getString(R.string.task_type_quick)
        )
        val expanded = remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded.value,
            onExpandedChange = { expanded.value = it },
            modifier = Modifier.fillMaxWidth(),
        ) {
            TextField(
                readOnly = true,
                value = selectedTaskType,
                onValueChange = {},
                label = { Text("Task Type") },
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
                taskTypes.forEach { taskType ->
                    DropdownMenuItem(
                        onClick = {
                            onTaskTypeSelected(taskType) // Notify parent of the selection
                            expanded.value = false
                        },
                        text = { Text(taskType) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }