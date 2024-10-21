package com.app.routineturboa.ui.reusable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.routineturboa.data.repository.TaskOperationResult
import kotlinx.coroutines.delay

@Composable
fun SuccessIndicator(confirmationResult:  MutableState<Result<TaskOperationResult>?>) {
    var isVisible by remember { mutableStateOf(false) }

    // Animate scale when isVisible changes
    val scale by animateFloatAsState(targetValue = if (isVisible) 1f else 0f)

    // Automatically show the indicator after a slight delay for a cute effect
    LaunchedEffect(Unit) {
        delay(300) // Delay before showing for cuteness
        isVisible = true
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(100.dp)
            .scale(scale) // Scale animation for "popping" effect
            .background(Color(0xFF4CAF50), shape = CircleShape) // Green background
    ) {
        Text(
            text = "✓",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
    }

    // Optionally, reset the visibility after some time (e.g., 2 seconds)
    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(2000)
            isVisible = false
            confirmationResult.value = null
        }
    }
}
