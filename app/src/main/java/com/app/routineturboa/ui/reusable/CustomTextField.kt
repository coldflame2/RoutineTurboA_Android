package com.app.routineturboa.ui.reusable

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String?,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    leadingIconResId: Int? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true
) {
    var isFocused by remember { mutableStateOf(false) }

    val backgroundColor =
        if (isFocused) MaterialTheme.colorScheme.surfaceDim.copy(alpha = 1f)
        else MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)

    val textColor =
        if (isFocused) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)

    if (label != null) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = if (label.isNotEmpty()) {
                { Text(label) }
            } else {
                {}
            },

            placeholder = { Text(placeholder) },
            leadingIcon = when {
                leadingIcon != null -> { { Icon(leadingIcon, contentDescription = null) } }
                leadingIconResId != null -> { { Icon(painterResource(id = leadingIconResId), contentDescription = null) } }
                else -> null
            },
            enabled = enabled,
            modifier = modifier
                .shadow(
                    elevation = if (isFocused) 8.dp else 2.dp,
                    shape = RoundedCornerShape(2.dp)
                )
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                }
                .then(modifier),
            shape = RoundedCornerShape(5.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
            ),
            singleLine = singleLine,
            keyboardOptions = keyboardOptions
        )
    }
}