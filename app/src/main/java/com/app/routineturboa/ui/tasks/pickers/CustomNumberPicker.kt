package com.app.routineturboa.ui.tasks.pickers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun DurationPickerField(
    modifier: Modifier = Modifier,
    value: Pair<Int, Int>?, // Pair of (hours, minutes)
    onValueChange: (Pair<Int, Int>) -> Unit,
    label: String,
    leadingIcon: ImageVector? = null,
    leadingIconResId: Int? = null,
) {
    val context = LocalContext.current
    val showPicker = remember { mutableStateOf(false) }
    val currentValue = value ?: Pair(0, 30)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { showPicker.value = true }
    ) {
        TextField(
            value = "${currentValue.first}h ${currentValue.second}m",
            onValueChange = { /* Read-only field */ },
            label = { Text(label) },
            leadingIcon = {
                when {
                    leadingIcon != null -> Icon(
                        leadingIcon, modifier = Modifier.size(18.dp),
                        contentDescription = null,
                    )
                    leadingIconResId != null -> Icon(
                        painterResource(id = leadingIconResId),
                        contentDescription = null
                    )
                    else -> null
                }
            },
            readOnly = true,
            enabled = false, // Make it look like a read-only field
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                unfocusedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                disabledTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
        )
    }

    if (showPicker.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { showPicker.value = false }, // Dismiss on outside click
            contentAlignment = Alignment.Center
        ) {
            DurationPicker(
            )
        }
    }
}


@Composable
fun DurationPicker () {
    Text(text = "Hey")

}

@Composable
fun DurationPicker2(
    currentHours: Int,
    currentMinutes: Int,
    onDurationSelected: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
    ) {
        Column {

            // Hours Picker
            LazyColumn(
                modifier = Modifier.height(50.dp), // Limit height for scrolling
            ) {
                items(24) { hour ->
                    Text(
                        text = "$hour",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDurationSelected(hour, currentMinutes) }
                            .padding(8.dp)
                    )
                }
            }

            // Minutes Picker
            LazyColumn(
                modifier = Modifier.height(50.dp), // Limit height for scrolling
            ) {
                items(60) { minute ->
                    Text(
                        text = "$minute",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDurationSelected(currentHours, minute) }
                    )
                }
            }
        }
    }
}
