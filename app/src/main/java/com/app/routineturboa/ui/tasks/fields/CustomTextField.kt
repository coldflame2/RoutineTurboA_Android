package com.app.routineturboa.ui.tasks.fields

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.room.util.TableInfo
import com.app.routineturboa.ui.theme.LocalCustomColors

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String?,
    onValueChange: (String) -> Unit,
    label: String?,
    placeholder: String = "",
    isInFocus: MutableState<Boolean> = remember { mutableStateOf(false) },
    leadingIcon: ImageVector? = null,
    leadingIconResId: Int? = null,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    errorMessage: String? = null
) {
    val focusManager = LocalFocusManager.current
    val customColors = LocalCustomColors.current

    val backgroundColor = MaterialTheme.colorScheme.surfaceTint
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant

    if (label != null) {
        if (value != null) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                label = if (label.isNotEmpty()) {
                    { Text(label) }
                } else {
                    { }
                },

                placeholder = { Text(placeholder) },
                leadingIcon = when {
                    leadingIcon != null -> { { Icon(leadingIcon, contentDescription = null) } }
                    leadingIconResId != null -> { { Icon(painterResource(id = leadingIconResId), contentDescription = null) } }
                    else -> null
                },
                enabled = enabled,
                modifier = modifier
                    .then(modifier),
                shape = RoundedCornerShape(5.dp),
                singleLine = singleLine,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Default,
                    keyboardType = keyboardType
                ),
                keyboardActions = KeyboardActions {
                    focusManager.clearFocus()
                },
                isError = (errorMessage != null),
                supportingText = {
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
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


        }
    }
}