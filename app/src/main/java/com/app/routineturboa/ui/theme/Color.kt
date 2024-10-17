package com.app.routineturboa.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// <editor-fold desc="Light Theme Colors">
val LightPrimary = Color(0xFFCCB9FF)
val LightOnPrimary = Color(0xFF1A0553)
val LightPrimaryContainer = Color(0xFFBB86FC)
val LightOnPrimaryContainer = Color(0xFF332727)
val LightInversePrimary = Color(0xFF03DAC6)
val LightSecondary = Color(0xFFA1BBFF)
val LightOnSecondary = Color(0xFF00192C)
val LightSecondaryContainer = Color(0xF7E9F8F8)
val LightOnSecondaryContainer = Color(0xFF000000)
val LightTertiary = Color(0xFF03DAC6)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFF018786)
val LightOnTertiaryContainer = Color(0xFF000000)
val LightBackground = Color(0xFFFFFFFF)
val LightOnBackground = Color(0xFF000000)
val LightSurface = Color(0xFFF0F0F0)
val LightOnSurface = Color(0xFF000000)
val LightSurfaceVariant = Color(0xFFC0C0C0)
val LightOnSurfaceVariant = Color(0xFF000000)
val LightSurfaceTint = Color(0xFF6200EE)
val LightInverseSurface = Color(0xFF000000)
val LightInverseOnSurface = Color(0xFFFFFFFF)
val LightError = Color(0xFFB00020)
val LightOnError = Color(0xFFFFFFFF)
val LightErrorContainer = Color(0xFFCF6679)
val LightOnErrorContainer = Color(0xFF000000)
val LightOutline = Color(0xFF000000)
val LightOutlineVariant = Color(0xFFDDDDDD)
val LightScrim = Color(0xFF0D1492)
val LightSurfaceBright = Color(0xFFF5F5F5)
val LightSurfaceContainer = Color(0xFFF5F5F5)
val LightSurfaceContainerHigh = Color(0xFFBBBBBB)
val LightSurfaceContainerHighest = Color(0xFF8D8D8D)
val LightSurfaceContainerLow = Color(0xFF585858)
val LightSurfaceContainerLowest = Color(0xFF353535)
val LightSurfaceDim = Color(0xFFD9F3DF)
// </editor-fold>

// <editor-fold desc="Dark Theme Colors">
val DarkPrimary = Color(0xFFBB86FC)
val DarkOnPrimary = Color(0xFF000000)
val DarkPrimaryContainer = Color(0xFF6200EE)
val DarkOnPrimaryContainer = Color(0xFFFFFFFF)
val DarkInversePrimary = Color(0xFF03DAC6)
val DarkSecondary = Color(0xFF03DAC6)
val DarkOnSecondary = Color(0xFF000000)
val DarkSecondaryContainer = Color(0xFF018786)
val DarkOnSecondaryContainer = Color(0xFFFFFFFF)
val DarkTertiary = Color(0xFF03DAC6)
val DarkOnTertiary = Color(0xFF000000)
val DarkTertiaryContainer = Color(0xFF018786)
val DarkOnTertiaryContainer = Color(0xFFFFFFFF)
val DarkBackground = Color(0xFF121212)
val DarkOnBackground = Color(0xFFFFFFFF)
val DarkSurface = Color(0xFF1E1E1E)
val DarkOnSurface = Color(0xFFFFFFFF)
val DarkSurfaceVariant = Color(0xFF2C2C2C)
val DarkOnSurfaceVariant = Color(0xFFFFFFFF)
val DarkSurfaceTint = Color(0xFFBB86FC)
val DarkInverseSurface = Color(0xFFFAFAFA)
val DarkInverseOnSurface = Color(0xFF000000)
val DarkError = Color(0xFFCF6679)
val DarkOnError = Color(0xFF000000)
val DarkErrorContainer = Color(0xFFB00020)
val DarkOnErrorContainer = Color(0xFFFFFFFF)
val DarkOutline = Color(0xFF444444)
val DarkOutlineVariant = Color(0xFF333333)
val DarkScrim = Color(0x80000000)
val DarkSurfaceBright = Color(0xFF2C2C2C)
val DarkSurfaceDim = Color(0xFFD9F3DF)
val DarkSurfaceContainer = Color(0xFF1E1E1E)
val DarkSurfaceContainerHigh = Color(0xFF2C2C2C)
val DarkSurfaceContainerHighest = Color(0xFF121212)
val DarkSurfaceContainerLow = Color(0xFF333333)
val DarkSurfaceContainerLowest = Color(0xFF444444)
// </editor-fold>

val LightColorScheme = lightColorScheme(
    // <editor-fold desc="Light Theme Scheme">
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    inversePrimary = LightInversePrimary,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    surfaceTint = LightSurfaceTint,
    inverseSurface = LightInverseSurface,
    inverseOnSurface = LightInverseOnSurface,
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    scrim = LightScrim,
    surfaceBright = LightSurfaceBright,
    surfaceDim = LightSurfaceDim,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerHigh = LightSurfaceContainerHigh,
    surfaceContainerHighest = LightSurfaceContainerHighest,
    surfaceContainerLow = LightSurfaceContainerLow,
    surfaceContainerLowest = LightSurfaceContainerLowest
    // </editor-fold>
)

val DarkColorScheme = darkColorScheme(
    // <editor-fold desc="Dark Theme Scheme">
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    inversePrimary = DarkInversePrimary,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceTint = DarkSurfaceTint,
    inverseSurface = DarkInverseSurface,
    inverseOnSurface = DarkInverseOnSurface,
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    scrim = DarkScrim,
    surfaceBright = DarkSurfaceBright,
    surfaceDim = DarkSurfaceDim,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest,
    surfaceContainerLow = DarkSurfaceContainerLow,
    surfaceContainerLowest = DarkSurfaceContainerLowest
    // </editor-fold>
)

