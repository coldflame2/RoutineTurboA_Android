package com.app.routineturboa.ui.main

import android.graphics.Color.alpha
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.routineturboa.data.room.entities.TaskEntity
import com.app.routineturboa.core.models.UiScreen
import com.app.routineturboa.core.models.DataOperationEvents
import com.app.routineturboa.core.models.StateChangeEvents
import com.app.routineturboa.core.models.UiState
import com.app.routineturboa.ui.tasks.childItems.HourColumn
import com.app.routineturboa.ui.tasks.childItems.InLineQuickEdit
import com.app.routineturboa.ui.tasks.childItems.PrimaryTaskView
import com.app.routineturboa.ui.tasks.dialogs.TaskDetailsDialog
import com.app.routineturboa.core.dbutils.TaskTypes
import com.app.routineturboa.core.dbutils.TaskTypes.utilsGetColor
import com.app.routineturboa.core.utils.adjustIntensity

import com.app.routineturboa.ui.reusable.animation.FullEditingAnimation
import com.app.routineturboa.ui.reusable.animation.PrimaryTaskViewAnimation
import com.app.routineturboa.ui.tasks.form.FullEditForm
import com.app.routineturboa.ui.theme.LocalCustomColors
import kotlinx.coroutines.launch
import java.time.LocalTime
import kotlin.math.pow

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ParentContainerLayout(
    task: TaskEntity,
    indexInList: Int,
    lazyListState: LazyListState,
    uiState: UiState,
    onStateChangeEvents: StateChangeEvents,
    onDataOperationEvents: DataOperationEvents,
) {
    val tag = "ParentContainerLayout"
    val coroutineScope = rememberCoroutineScope()

    // region: booleanStates (clicked, quickEdit, fullEdit, showDetails, longPressMenu)
    val isThisTaskClicked =  (uiState.taskContext.clickedTask?.id == task.id)

    val isThisTaskQuickEditing =
        (uiState.uiScreen is UiScreen.QuickEditOverlay) &&
                (uiState.taskContext.inEditTask?.id == task.id)

    val isThisTaskFullEditing =
        (uiState.uiScreen is UiScreen.FullEditing) &&
                (uiState.taskContext.inEditTask?.id == task.id)

    val isAnotherTaskEditing =
        (uiState.uiScreen is UiScreen.QuickEditOverlay) &&
                (uiState.taskContext.inEditTask?.id != task.id)

    val isThisTaskShowDetails =
        (uiState.uiScreen is UiScreen.DetailsView) &&
                (uiState.taskContext.showingDetailsTask?.id == task.id)

    val isThisLatestTask = uiState.taskContext.latestTasks.contains(task)

    val isThisTaskLongPressMenu =
        (uiState.uiScreen is UiScreen.LongPressMenu) &&
                (uiState.taskContext.longPressMenuTask?.id == task.id)

    val isTaskWithinCurrentTimeRange = remember { mutableStateOf(false) }

    val booleanStatesMap = mapOf(
        // region: booleanStates
        "isThisTaskClicked" to isThisTaskClicked,
        "isThisTaskQuickEditing" to isThisTaskQuickEditing,
        "isThisTaskFullEditing" to isThisTaskFullEditing,
        "isThisTaskShowDetails" to isThisTaskShowDetails,
        "isAnotherTaskEditing" to isAnotherTaskEditing,
        "isThisLatestTask" to isThisLatestTask,
        "isThisTaskLongPressMenu" to isThisTaskLongPressMenu
        // endregion
    )
    // endregion

    // region: colors, height, border, padding

    // Configuration for linear scaling of cardHeight based on duration
    val minHeight = 15.dp
    val minPadding = 0.dp
    val maxPadding = 10.dp
    val minFontSize = 8.sp
    val maxFontSize = 15.sp

    // Constant for the height after which padding and font size don't increment
    val maxHeightForPaddingAndFontSize = 55.dp

    // Height increment per minute of task duration
    val heightPerMinute = 2.dp

    val calculatedCardHeight  = if (isThisTaskFullEditing) {
        550.dp
    } else if (isThisTaskQuickEditing) {
        120.dp
    } else {
        when (task.type) {
            TaskTypes.MAIN, TaskTypes.HELPER, TaskTypes.BASICS -> {
                task.duration?.let { durationMinutes ->
                    // Linear scaling based on duration without maximum limit
                    val extraHeight = durationMinutes * heightPerMinute.value
                    val heightValue = minHeight.value + extraHeight
                    heightValue.dp.coerceAtLeast(minHeight)
                } ?: minHeight
            }
            TaskTypes.QUICK -> 40.dp
            else -> 55.dp
        }
    }

    val cardHeight = minOf(calculatedCardHeight, 150.dp)

    // Dynamic calculation for topPadding based on cardHeight
    val topPadding = when {
        cardHeight <= minHeight -> minPadding
        cardHeight >= maxHeightForPaddingAndFontSize -> maxPadding
        else -> {
            val paddingValue = minPadding.value +
                    ((cardHeight.value - minHeight.value) / (maxHeightForPaddingAndFontSize.value - minHeight.value) * (maxPadding.value - minPadding.value))
            paddingValue.dp
        }
    }

    // Dynamic calculation for taskNameFontSize based on cardHeight
    val taskNameFontSize = when {
        cardHeight <= minHeight -> minFontSize
        cardHeight >= maxHeightForPaddingAndFontSize -> maxFontSize
        else -> {
            val fontSizeValue = minFontSize.value +
                    ((cardHeight.value - minHeight.value) / (maxHeightForPaddingAndFontSize.value - minHeight.value) * (maxFontSize.value - minFontSize.value))
            fontSizeValue.sp
        }
    }
        // endregion

    val customColors = LocalCustomColors.current
    val cardBgColor = utilsGetColor(customColors, task.type)

    val cardBorder = when {
        isTaskWithinCurrentTimeRange.value -> null
        isThisTaskQuickEditing -> BorderStroke(3.dp, MaterialTheme.colorScheme.tertiary)
        isThisTaskClicked -> BorderStroke(1.dp, cardBgColor.adjustIntensity(0.8f))
        else -> null
    }

    var menuOffsetOnLongPress by remember { mutableStateOf(Offset.Zero) }

    // endregion

    // get task within current time range
    LaunchedEffect(Unit) {
        val currentTime = LocalTime.now()
        isTaskWithinCurrentTimeRange.value = task.startTime?.let { start ->
            task.endTime?.let { end ->
                val isWithinRange = start <= currentTime && currentTime < end
                if (isWithinRange) {
                    Log.d(tag, "LaunchedEffect: Task (${task.name}) is within current time range")
                }
                isWithinRange
            }
        } ?: false // Default to false if either startTime or endTime is null
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        shape = RoundedCornerShape(10.dp),
        border = cardBorder,
        modifier = Modifier
            .height(cardHeight) // Limit the card height to 200.dp
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { offset ->
                        onStateChangeEvents.onTaskLongPress(task)
                        menuOffsetOnLongPress = offset
                    },
                    onTap = { onStateChangeEvents.onTaskClick(task) }
                )
            },
    ) {
        // Use a Box to overlay the gradient within the Card's constrained height
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Your main content
            Row {
                // region: HourColumn
                HourColumn(
                    startTime = task.startTime,
                    duration = task.duration,
                    isThisTaskClicked = isThisTaskClicked,
                    isCurrentTask = isTaskWithinCurrentTimeRange.value,
                    height = cardHeight,
                    topPadding = topPadding
                )
                // endregion

                // PrimaryTaskView
                PrimaryTaskViewAnimation(
                    visible = !(isThisTaskQuickEditing || isThisTaskShowDetails || isThisTaskFullEditing)
                ) {
                    PrimaryTaskView(
                        task = task,
                        isThisTaskClicked = isThisTaskClicked,
                        isThisTaskLongPressMenu = isThisTaskLongPressMenu,
                        isThisLatestTask = isThisLatestTask,
                        stateChangeEvents = onStateChangeEvents,
                        dataOperationEvents = onDataOperationEvents,
                        cardHeight = cardHeight,
                        topPadding = topPadding,
                        taskNameFontSize = taskNameFontSize,
                        bgColor = MaterialTheme.colorScheme.background
                    )
                }

                // Full Editing
                FullEditingAnimation(visible = isThisTaskFullEditing) {
                    FullEditForm(
                        taskInEdit = task,
                        onStateChangeEvents = onStateChangeEvents,
                        onDataOperationEvents = onDataOperationEvents,
                        taskOperationState = uiState.taskOperationState,
                    )
                }

                // Quick Editing
                AnimatedVisibility(visible = isThisTaskQuickEditing) {
                    InLineQuickEdit(
                        task = task,
                        onCancelClick = { onStateChangeEvents.onDismissOrReset(task) },
                        isFullEditing = isThisTaskFullEditing,
                        onShowFullEditClick = { id ->
                            coroutineScope.launch {
                                onStateChangeEvents.onShowFullEditClick()
                            }
                        },
                        onUpdateTaskConfirmClick = { editedFormData ->
                            coroutineScope.launch {
                                onDataOperationEvents.onUpdateTaskConfirmClick(
                                    editedFormData
                                )
                            }
                        }
                    )
                }

                // Show Task Details
                AnimatedVisibility(visible = isThisTaskShowDetails) {
                    TaskDetailsDialog(
                        task = task,
                        onDismiss = { onStateChangeEvents.onDismissOrReset(task) }
                    )
                }
            }

            // Add the gradient overlay at the bottom of the Box
            if (calculatedCardHeight > 300.dp) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .align(Alignment.BottomCenter) // Align it to the bottomCenter
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,      // Start with transparent to show content
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.background, // Full background color (behind it) to complete the fade
                                )
                            )
                        )
                )

                // Dotted line effect at the bottom of the card
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp) // Increase the height to accommodate both lines
                        .align(Alignment.BottomCenter)
                ) {
                    val dotRadius = 3f // Radius for each dot
                    val dotSpacing = 5f  // Spacing between dots
                    val lineSpacing = 3.dp.toPx() // Vertical distance between the two dotted lines

                    var xPosition = 0f // Starting x-position of the dots

                    // Draw the first dotted line
                    while (xPosition < size.width) {
                        drawCircle(
                            color = cardBgColor,
                            radius = dotRadius,
                            center = Offset(xPosition, size.height / 2 - lineSpacing / 2)
                        )
                        xPosition += dotRadius * 2 + dotSpacing
                    }

                    // Reset xPosition for the second line
                    xPosition = 0f

                    // Draw the second dotted line
                    while (xPosition < size.width) {
                        drawCircle(
                            color = cardBgColor.copy(alpha = 0.5f), // Adjust alpha to 0.5 for transparency
                            radius = dotRadius,
                            center = Offset(xPosition, size.height / 2 + lineSpacing / 2)
                        )
                        xPosition += dotRadius * 2 + dotSpacing
                    }
                }


            }


        }
    }
}
