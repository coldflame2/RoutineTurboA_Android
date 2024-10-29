package com.app.routineturboa.ui.tasks.pickers

import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.app.routineturboa.core.dbutils.Converters.timeToString
import java.time.LocalTime

@Composable
fun TimePickerField(
    modifier: Modifier = Modifier,
    value: LocalTime?,
    onValueChange: (LocalTime) -> Unit,
    label: String,
    leadingIcon: ImageVector? = null,
    leadingIconResId: Int? = null,
) {
    val context = LocalContext.current
    val isFocused by remember { mutableStateOf(false) }

    val backgroundColor = MaterialTheme.colorScheme.surfaceTint
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant


    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                Log.d("TimePicker", "Okay, clicked")
                val now = LocalTime.now()
                val hour = value?.hour ?: now.hour
                val minute = value?.minute ?: now.minute

                TimePickerDialog(
                    context,
                    { _, selectedHour: Int, selectedMinute: Int ->
                        onValueChange(LocalTime.of(selectedHour, selectedMinute))
                    },
                    hour,
                    minute,
                    true
                ).show()
            }
    ) {

        TextField(
            value = timeToString(value) ?: " ",
            onValueChange = { /* Read-only field */ },
            label = { Text(label) },
            suffix = { },
            supportingText = { },
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
            ),

        )
    }
}


sealed class TextFieldValue {
    data class Text(val value: String) : TextFieldValue()
    data class Time(val value: LocalTime) : TextFieldValue()
}
