package com.app.routineturboa.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DottedLine(
    color: Color,
    thickness: Dp = 0.2.dp,
    dotLength: Dp = 8.dp,
    dotSpacing: Dp = 3.dp
) {
    val density = LocalDensity.current

    val pathEffect = PathEffect.dashPathEffect(
        floatArrayOf(
            with(density) { dotLength.toPx() },
            with(density) { dotSpacing.toPx() }
        ), 0f
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(thickness)
    ) {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = thickness.toPx(),
            pathEffect = pathEffect
        )
    }
}