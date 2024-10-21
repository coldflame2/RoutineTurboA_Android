package com.app.routineturboa.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider


@Composable
fun RoutineTurboATheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    useDynamicColors: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
//        useDynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            if (isDarkTheme) dynamicDarkColorScheme(context = LocalContext.current)
//            else dynamicLightColorScheme(context = LocalContext.current)
//        }
        isDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // logic for which custom palette to use
    val customColorsPalette = if (isDarkTheme) DarkCustomColorsPalette
        else LightCustomColorsPalette

    CompositionLocalProvider(
        LocalCustomColors provides customColorsPalette //  custom palette
    ) {
        MaterialTheme(
            colorScheme = colorScheme, // the MaterialTheme still uses the "normal" palette
        ) {
            content()
        }
    }
}