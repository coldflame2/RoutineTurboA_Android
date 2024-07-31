package com.app.routineturboa.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext


@Composable
fun RoutineTurboATheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    useDynamicColors: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        useDynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDarkTheme) dynamicDarkColorScheme(context = LocalContext.current)
            else dynamicLightColorScheme(context = LocalContext.current)
        }

        isDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // logic for which custom palette to use
    val customColorsPalette =
        if (isDarkTheme) DarkCustomColorsPalette
        else LightCustomColorsPalette

    // here is the important point, where you will expose custom objects
    CompositionLocalProvider(
        LocalCustomColorsPalette provides customColorsPalette // our custom palette
    ) {
        MaterialTheme(
            colorScheme = colorScheme, // the MaterialTheme still uses the "normal" palette
        ) {
            content()
        }
    }
}