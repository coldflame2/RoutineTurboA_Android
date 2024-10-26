package com.app.routineturboa.ui.reusable.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun TasksLazyColumnAnimation(
    modifier: Modifier = Modifier,
    visible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it }, // Starts below the screen
            animationSpec = tween(durationMillis = 600, delayMillis = 200)
        ),
        exit = slideOutVertically(
            targetOffsetY = { -it }, // Slides up by its height
            animationSpec = tween(durationMillis = 600, delayMillis = 0)
        ),
        modifier = modifier
    ) {
        content()
    }
}


@Composable
fun NewTaskScreenAnimation(
    modifier: Modifier = Modifier,
    visible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it }, // Starts below the screen
            animationSpec = tween(durationMillis = 400, delayMillis = 0)
        ),
        exit = slideOutVertically(
            targetOffsetY = { it }, // Slides down when exiting
            animationSpec = tween(durationMillis = 400, delayMillis = 200)
        ),
        modifier = modifier
    ) {
        content()
    }
}


/**
 * Positive initialOffsetY in enter transitions means starting below and sliding up into place.
 * Negative initialOffsetY means starting above and sliding down into place.
 * Positive targetOffsetY in exit transitions means sliding down and exiting below.
 * Negative targetOffsetY means sliding up and exiting above.
 */