package com.app.routineturboa.ui.reusable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.routineturboa.ui.theme.LocalCustomColors
import kotlinx.coroutines.delay

@Composable
fun SuccessIndicator(
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val customColors = LocalCustomColors.current

    // Automatically show the indicator after a slight delay for a cute effect
    LaunchedEffect(Unit) {
        delay(300) // Delay before showing for cuteness
        isVisible = true

        // Reset the visibility after some time (e.g., 2 seconds)
        delay(2000)
        isVisible = false
        onReset()
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn(initialScale = 0f),
        exit = scaleOut(targetScale = 0f)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .background(customColors.successIndicatorColor, shape = CircleShape)
        ) {
            Text(
                text = "✓",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
        }
    }
}


