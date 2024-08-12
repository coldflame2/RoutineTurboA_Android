package com.app.routineturboa.utils

import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

object AnimatedAlphaUtils {
    @Composable
    fun animatedAlpha(
        transition: InfiniteTransition,
        initialValue: Float = 0.2f,
        targetValue: Float = 0.5f,
        duration: Int = 500,
    ): Float {
        val animatedAlpha by transition.animateFloat(
            label = "",
            initialValue = initialValue,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, easing = SineEasing),
                repeatMode = RepeatMode.Reverse
            ),
        )
        return animatedAlpha
    }
}