package com.app.routineturboa.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


@Composable
fun RoutineTurboATheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFFBB86FC), // Light Purple
            onPrimary = Color(0xFF000000), // Black
            primaryContainer = Color(0xFF6200EE), // Purple
            onPrimaryContainer = Color(0xFFFFFFFF), // White
            inversePrimary = Color(0xFF03DAC6), // Teal
            secondary = Color(0xFF03DAC6), // Teal
            onSecondary = Color(0xFF000000), // Black
            secondaryContainer = Color(0xFF018786), // Dark Teal
            onSecondaryContainer = Color(0xFFFFFFFF), // White
            tertiary = Color(0xFF03DAC6), // Teal
            onTertiary = Color(0xFF000000), // Black
            tertiaryContainer = Color(0xFF018786), // Dark Teal
            onTertiaryContainer = Color(0xFFFFFFFF), // White
            background = Color(0xFF121212), // Dark Gray
            onBackground = Color(0xFFFFFFFF), // White
            surface = Color(0xFF1E1E1E), // Slightly lighter Gray
            onSurface = Color(0xFFFFFFFF), // White
            surfaceVariant = Color(0xFF2C2C2C), // Medium Dark Gray
            onSurfaceVariant = Color(0xFFFFFFFF), // White
            surfaceTint = Color(0xFFBB86FC), // Light Purple
            inverseSurface = Color(0xFFFAFAFA), // Almost White
            inverseOnSurface = Color(0xFF000000), // Black
            error = Color(0xFFCF6679), // Light Red
            onError = Color(0xFF000000), // Black
            errorContainer = Color(0xFFB00020), // Red
            onErrorContainer = Color(0xFFFFFFFF), // White
            outline = Color(0xFF444444), // Dark Gray
            outlineVariant = Color(0xFF333333), // Darker Gray
            scrim = Color(0x80000000), // Semi-transparent Black
            surfaceBright = Color(0xFF2C2C2C), // Medium Dark Gray
            surfaceContainer = Color(0xFF1E1E1E), // Slightly lighter Gray
            surfaceContainerHigh = Color(0xFF2C2C2C), // Medium Dark Gray
            surfaceContainerHighest = Color(0xFF121212), // Dark Gray
            surfaceContainerLow = Color(0xFF333333), // Darker Gray
            surfaceContainerLowest = Color(0xFF444444) // Dark Gray


        )

    } else {
        lightColorScheme(
            primary = Color(0xFF6200EE), // Purple
            onPrimary = Color(0xFFFFFFFF), // White
            primaryContainer = Color(0xFFBB86FC), // Light Purple
            onPrimaryContainer = Color(0xFF000000), // Black
            inversePrimary = Color(0xFF03DAC6), // Teal
            secondary = Color(0xFF03DAC5), // Teal
            onSecondary = Color(0xFFFFFFFF), // White
            secondaryContainer = Color(0xFF018786), // Dark Teal
            onSecondaryContainer = Color(0xFF000000), // Black
            tertiary = Color(0xFF03DAC6), // Teal
            onTertiary = Color(0xFFFFFFFF), // White
            tertiaryContainer = Color(0xFF018786), // Dark Teal
            onTertiaryContainer = Color(0xFF000000), // Black
            background = Color(0xFFFFFFFF), // White
            onBackground = Color(0xFF000000), // Black
            surface = Color(0xFFFFFFFF), // White
            onSurface = Color(0xFF000000), // Black
            surfaceVariant = Color(0xFFF1F1F1), // Light Gray
            onSurfaceVariant = Color(0xFF000000), // Black
            surfaceTint = Color(0xFF6200EE), // Purple
            inverseSurface = Color(0xFF000000), // Black
            inverseOnSurface = Color(0xFFFFFFFF), // White
            error = Color(0xFFB00020), // Red
            onError = Color(0xFFFFFFFF), // White
            errorContainer = Color(0xFFCF6679), // Light Red
            onErrorContainer = Color(0xFF000000), // Black
            outline = Color(0xFF000000), // Black
            outlineVariant = Color(0xFFDDDDDD), // Light Gray
            scrim = Color(0xFF0D1492), // Semi-transparent Black
            surfaceBright = Color(0xFFF5F5F5), // Off-White
            surfaceContainer = Color(0xFFF5F5F5), // Off-White
            surfaceContainerHigh = Color(0xFFEEEEEE), // Very Light Gray
            surfaceContainerHighest = Color(0xFFFAFAFA), // Almost White
            surfaceContainerLow = Color(0xFFC5C5C5), // Light Gray
            surfaceContainerLowest = Color(0xFFBDBDBD) // Gray

        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                content()
            }
        }
    )
}