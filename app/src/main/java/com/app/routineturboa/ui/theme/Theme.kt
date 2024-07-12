package com.app.routineturboa.ui.theme

import android.util.Log
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
    Log.d("RoutineTurboATheme", "Theme applied")
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFF6200EE),  // Existing
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFF3700B3),
            onPrimaryContainer = Color(0xFFEADDFF),
            secondary = Color(0xFF03DAC6),  // Existing
            onSecondary = Color(0xFF000000),
            secondaryContainer = Color(0xFF018786),
            onSecondaryContainer = Color(0xFFA1F4F1),
            tertiary = Color(0xFFEFB8C8),
            onTertiary = Color(0xFF492532),
            tertiaryContainer = Color(0xFF633B48),
            onTertiaryContainer = Color(0xFFFFD8E4),
            error = Color(0xFFCF6679),
            onError = Color(0xFF000000),
            errorContainer = Color(0xFFB00020),
            onErrorContainer = Color(0xFFFFDAD6),
            background = Color(0xFF202B32),  // Existing
            onBackground = Color(0xFFE6E1E5),
            surface = Color(0xFF1C1B1F),
            onSurface = Color(0xFFE6E1E5),
            surfaceVariant = Color(0xFFB81818),  // Existing
            onSurfaceVariant = Color(0xFFCAC4D0),
            outline = Color(0xFFDADADA),  // Existing
            outlineVariant = Color(0xFF49454F),
            scrim = Color(0xFF000000)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF6200EE),  // Existing
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFEADDFF),
            onPrimaryContainer = Color(0xFF21005D),
            secondary = Color(0xFF03DAC6),  // Existing
            onSecondary = Color(0xFF000000),
            secondaryContainer = Color(0xFFCEFAF8),
            onSecondaryContainer = Color(0xFF002021),
            tertiary = Color(0xFFBE708C),
            onTertiary = Color(0xFFFFFFFF),
            tertiaryContainer = Color(0xFFFFD8E4),
            onTertiaryContainer = Color(0xFF31111D),
            error = Color(0xFFB00020),
            onError = Color(0xFFFFFFFF),
            errorContainer = Color(0xFFFCDAD6),
            onErrorContainer = Color(0xFF410002),
            background = Color(0xFFF3F3F3),  // Existing
            onBackground = Color(0xFF1C1B1F),
            surface = Color(0xFFFFFBFE),
            onSurface = Color(0xFF1C1B1F),
            surfaceVariant = Color(0xFFFFFFFF),  // Existing
            onSurfaceVariant = Color(0xFF49454F),
            outline = Color(0xFF8B8B8B),  // Existing
            outlineVariant = Color(0xFFCAC4D0),
            scrim = Color(0xFF000000)
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