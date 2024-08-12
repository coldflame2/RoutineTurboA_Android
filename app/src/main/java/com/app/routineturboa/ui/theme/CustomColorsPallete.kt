package com.app.routineturboa.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * How to use:
 * LocalCustomColorsPalette.current.gray200.copy(alpha=0.8f)
 *
 */

@Immutable
data class CustomColorsPalette(
    val gray100: Color = Color.Unspecified,
    val gray200: Color = Color.Unspecified,
    val gray300: Color = Color.Unspecified,
    val gray400: Color = Color.Unspecified,

    val mainTaskColor: Color = Color.Unspecified,
    val basicsTaskColor: Color = Color.Unspecified
)

//<editor-fold "Colors for LightColorScheme"
val LightGray100 = Color(color = 0xFFCECECE)
val LightGray200 = Color(color = 0xFF8F8F8F)
val LightGray300 = Color(color = 0xFF525252)
val LightGray400 = Color(color = 0xFF292929)
val LightMainTaskColor = Color(color = 0xFFA9E2FD)

//</editor-fold>


//<editor-fold "Colors for DarkColorScheme"
val DarkGray100 = Color(color = 0xFFCECECE)
val DarkGray200 = Color(color = 0xFF8F8F8F)
val DarkGray300 = Color(color = 0xFF525252)
val DarkGray400 = Color(color = 0xFF292929)
val DarkMainTaskColor = Color(color = 0xFF02143F)
//</editor-fold>

val LightCustomColorsPalette = CustomColorsPalette(
    gray100 = LightGray100,
    gray200 = LightGray200,
    gray300 = LightGray300,
    gray400 = LightGray400,
    mainTaskColor = LightMainTaskColor
)

val DarkCustomColorsPalette = CustomColorsPalette(
    gray100 = DarkGray100,
    gray200 = DarkGray200,
    gray300 = DarkGray300,
    gray400 = DarkGray400,
    mainTaskColor = DarkMainTaskColor
)

val LocalCustomColorsPalette = staticCompositionLocalOf { CustomColorsPalette() }