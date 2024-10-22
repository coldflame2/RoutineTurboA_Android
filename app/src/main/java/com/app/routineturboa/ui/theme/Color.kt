package com.app.routineturboa.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// <editor-fold desc="Light Theme Colors">
val LightPrimary = Color(0xFFE0C2F5)
val LightOnPrimary = Color(0xFF1A0429)
val LightPrimaryContainer = Color(0xFFDADBC7)
val LightOnPrimaryContainer = Color(0xFF000000)
val LightInversePrimary = Color(0xFFCB997E)
val LightSecondary = Color(0xFFB7B7A4)
val LightOnSecondary = Color(0xFF303001)
val LightSecondaryContainer = Color(0xFFECEAD8)
val LightOnSecondaryContainer = Color(0xFF000000)
val LightTertiary = Color(0xFFACD6A8)
val LightOnTertiary = Color(0xFF00310D)
val LightTertiaryContainer = Color(0xFFDCD7C9)
val LightOnTertiaryContainer = Color(0xFF000000)
val LightBackground = Color(0xFFF5F5F5)
val LightOnBackground = Color(0xFF000000)
val LightSurface = Color(0xFFF0EFEB)
val LightOnSurface = Color(0xFF000000)
val LightSurfaceVariant = Color(0xFFE5E5E5)
val LightOnSurfaceVariant = Color(0xFF000000)
val LightSurfaceTint = Color(0xFF6B705C)
val LightInverseSurface = Color(0xFF2F2F2F)
val LightInverseOnSurface = Color(0xFFFFFFFF)
val LightError = Color(0xFFB22222)
val LightOnError = Color(0xFFFFFFFF)
val LightErrorContainer = Color(0xFFFCD5D5)
val LightOnErrorContainer = Color(0xFF000000)
val LightOutline = Color(0xFF8D8D8D)
val LightOutlineVariant = Color(0xFFC4C4C4)
val LightScrim = Color(0x66000000)
val LightSurfaceBright = Color(0xFFFDFCFB)
val LightSurfaceContainer = Color(0xFFF8F8F8)
val LightSurfaceContainerHigh = Color(0xFFEDEDED)
val LightSurfaceContainerHighest = Color(0xFFE0E0E0)
val LightSurfaceContainerLow = Color(0xFFCCCCCC)
val LightSurfaceContainerLowest = Color(0xFFB3B3B3)
val LightSurfaceDim = Color(0xFFD6D6D6)

// </editor-fold>

// <editor-fold desc="Dark Theme Colors">
 val DarkPrimary = Color(0xFF264653)
val DarkOnPrimary = Color(0xFFFFFFFF)
val DarkPrimaryContainer = Color(0xFF2A9D8F)
val DarkOnPrimaryContainer = Color(0xFFFFFFFF)
val DarkInversePrimary = Color(0xFFE9C46A)
val DarkSecondary = Color(0xFF1D3557)
val DarkOnSecondary = Color(0xFFFFFFFF)
val DarkSecondaryContainer = Color(0xFF457B9D)
val DarkOnSecondaryContainer = Color(0xFFFFFFFF)
val DarkTertiary = Color(0xFFA8DADC)
val DarkOnTertiary = Color(0xFF000000)
val DarkTertiaryContainer = Color(0xFF457B9D)
val DarkOnTertiaryContainer = Color(0xFFFFFFFF)
val DarkBackground = Color(0xFF121212)
val DarkOnBackground = Color(0xFFFFFFFF)
val DarkSurface = Color(0xFF1E1E1E)
val DarkOnSurface = Color(0xFFFFFFFF)
val DarkSurfaceVariant = Color(0xFF2C2C2C)
val DarkOnSurfaceVariant = Color(0xFFFFFFFF)
val DarkSurfaceTint = Color(0xFF264653)
val DarkInverseSurface = Color(0xFFE0E0E0)
val DarkInverseOnSurface = Color(0xFF000000)
val DarkError = Color(0xFFB22222)
val DarkOnError = Color(0xFFFFFFFF)
val DarkErrorContainer = Color(0xFF8B0000)
val DarkOnErrorContainer = Color(0xFFFFFFFF)
val DarkOutline = Color(0xFF555555)
val DarkOutlineVariant = Color(0xFF444444)
val DarkScrim = Color(0x80000000)
val DarkSurfaceBright = Color(0xFF2C2C2C)
val DarkSurfaceDim = Color(0xFF1A1A1A)
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
    surfaceContainerLowest = LightSurfaceContainerLowest,
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

