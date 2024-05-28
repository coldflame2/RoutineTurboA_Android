package com.app.routineturboa.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


@Composable
fun RoutineTurboATheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFF6200EE),
            secondary = Color(0xFF03DAC6),
            background = Color(0xFF202B32),
            surfaceVariant = Color(0xFFB81818),
            outline = Color(0xFFDADADA)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF6200EE),
            secondary = Color(0xFF03DAC6),
            background = Color(0xFFF3F3F3),
            surfaceVariant = Color(0xFF5A0F0F),
            outline = Color(0xFF8B8B8B)

        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
