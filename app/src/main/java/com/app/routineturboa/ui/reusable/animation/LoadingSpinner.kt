package com.app.routineturboa.ui.reusable.animation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LoadingSpinner(
    height: Dp = 30.dp,
    width: Dp = 30.dp,
    strokeWidth: Dp = 3.dp,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    trackColor: Color = Color.LightGray,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Loading Infinite Transition")

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    Box(
        modifier = Modifier
            .rotate(angle)
            .height(height)
            .width(width),
    ) {
        CircularProgressIndicator(
            color = color,
            strokeWidth = strokeWidth,
            trackColor = trackColor,
            strokeCap = StrokeCap.Round
        )
    }
}