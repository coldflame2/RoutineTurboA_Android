package com.app.routineturboa.utils

import androidx.compose.animation.core.Easing
import kotlin.math.PI
import kotlin.math.sin

object SineEasing : Easing {
    override fun transform(fraction: Float): Float {
        return sin(fraction * PI.toFloat() / 2)
    }
}