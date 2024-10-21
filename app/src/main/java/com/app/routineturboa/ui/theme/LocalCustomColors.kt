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

    val editIconColor: Color = Color.Unspecified,

    val mainTaskColor: Color = Color.Unspecified,
    val basicsTaskColor: Color = Color.Unspecified,
    val helperTaskColor: Color = Color.Unspecified,
    val quickTaskColor: Color = Color.Unspecified,
)

//<editor-fold "Colors for LightColorScheme"
val LightGray100 = Color(color = 0xFFCECECE)
val LightGray200 = Color(color = 0xFF8F8F8F)
val LightGray300 = Color(color = 0xFF525252)
val LightGray400 = Color(color = 0xFF292929)
val LightEditIconColor = Color(color = 0xFF02064B)

val LightMainTaskColor = Color(color = 0xFFE1F5FF)
val LightBasicsTaskColor = Color(color = 0xFFE9F5D7)
val LightHelperTaskColor = Color(color = 0xFFF9C8FF)
val LightQuickTaskColor = Color(color = 0xFFFFF0A3)
//</editor-fold>

//<editor-fold "Colors for DarkColorScheme"
val DarkGray100 = Color(color = 0xFFCECECE)
val DarkGray200 = Color(color = 0xFF8F8F8F)
val DarkGray300 = Color(color = 0xFF525252)
val DarkGray400 = Color(color = 0xFF292929)
val DarkIconColor = Color(color = 0xFF000000)

val DarkMainTaskColor = Color(color = 0xFF02143F)
val DarkBasicsTaskColor = Color(color = 0xFF17251A)
val DarkHelperTaskColor = Color(color = 0xFF3F3502)
val DarkQuickTaskColor = Color(color = 0xFF3F0202)
//</editor-fold>

val LightCustomColorsPalette = CustomColorsPalette(
    gray100 = LightGray100,
    gray200 = LightGray200,
    gray300 = LightGray300,
    gray400 = LightGray400,
    editIconColor = LightEditIconColor,
    mainTaskColor = LightMainTaskColor,
    helperTaskColor = LightHelperTaskColor,
    basicsTaskColor = LightBasicsTaskColor,
    quickTaskColor = LightQuickTaskColor
)

val DarkCustomColorsPalette = CustomColorsPalette(
    gray100 = DarkGray100,
    gray200 = DarkGray200,
    gray300 = DarkGray300,
    gray400 = DarkGray400,
    editIconColor = DarkIconColor,
    mainTaskColor = DarkMainTaskColor,
    helperTaskColor = DarkHelperTaskColor,
    basicsTaskColor = DarkBasicsTaskColor,
    quickTaskColor = DarkQuickTaskColor
)

val LocalCustomColors = staticCompositionLocalOf { CustomColorsPalette() }