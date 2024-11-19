package com.app.routineturboa.core.utils

import androidx.compose.ui.graphics.Color

fun Color.adjustIntensity(intensity: Float): Color {
    return this.copy(
        red = (this.red * intensity).coerceIn(0f, 1f),
        green = (this.green * intensity).coerceIn(0f, 1f),
        blue = (this.blue * intensity).coerceIn(0f, 1f)
    )
}
