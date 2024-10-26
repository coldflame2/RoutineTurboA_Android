package com.app.routineturboa.ui.tasks

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.app.routineturboa.data.room.entities.TaskEntity
import com.app.routineturboa.shared.states.ActiveUiComponent
import com.app.routineturboa.shared.events.DataOperationEvents
import com.app.routineturboa.shared.events.StateChangeEvents
import com.app.routineturboa.shared.states.UiState
import com.app.routineturboa.ui.tasks.childItems.HourColumn
import com.app.routineturboa.ui.tasks.childItems.InLineQuickEdit
import com.app.routineturboa.ui.tasks.childItems.PrimaryTaskView
import com.app.routineturboa.ui.tasks.controls.OptionsMenu
import com.app.routineturboa.ui.tasks.dialogs.FullEditDialog
import com.app.routineturboa.ui.tasks.dialogs.TaskDetailsDialog
import com.app.routineturboa.ui.theme.LocalCustomColors
import com.app.routineturboa.utils.TaskTypes
import kotlinx.coroutines.launch
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.S)
@Composable
// Usage: TasksLazyColumn
fun ParentTaskItem(
    task: TaskEntity,
    mainTasks: List<TaskEntity>,
    uiState: UiState,
    onStateChangeEvents: StateChangeEvents,
    onDataOperationEvents: DataOperationEvents,
) {
    val tag = "ParentTaskItem"
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    var isShowContextMenu by remember { mutableStateOf(false) }
    var longPressOffset by remember { mutableStateOf(Offset.Zero) }

    val isTaskWithinCurrentTimeRange = remember { mutableStateOf(false) }

    val isThisTaskClicked = (uiState.uiTaskReferences.clickedTask?.id == task.id)

    val isThisTaskQuickEditing =
        (uiState.activeUiComponent is ActiveUiComponent.QuickEditOverlay) &&
                (uiState.uiTaskReferences.inEditTask?.id == task.id)

    val isThisTaskFullEditing =
        (uiState.activeUiComponent is ActiveUiComponent.FullEditing) &&
                (uiState.uiTaskReferences.inEditTask?.id == task.id)

    val isThisTaskShowDetails =
        (uiState.activeUiComponent is ActiveUiComponent.DetailsView) &&
                (uiState.uiTaskReferences.showingDetailsTask?.id == task.id)

    val isAnotherTaskEditing =
        (uiState.activeUiComponent is ActiveUiComponent.QuickEditOverlay) &&
                (uiState.uiTaskReferences.inEditTask?.id != task.id)

    val latestTask = uiState.uiTaskReferences.latestTask


    // region: colors, height, border, padding
    val customColors = LocalCustomColors.current

    val cardBgColor = remember(onStateChangeEvents) {
        when {
            isAnotherTaskEditing -> customColors.gray100
            task.type == TaskTypes.MAIN -> customColors.mainTaskColor
            task.type == TaskTypes.BASICS -> customColors.basicsTaskColor
            task.type == TaskTypes.HELPER -> customColors.helperTaskColor
            task.type == TaskTypes.QUICK -> customColors.quickTaskColor
            task.type == TaskTypes.UNDEFINED -> customColors.undefinedTaskColor
            else -> customColors.gray300
        }
    }

    val cardHeight: Dp by animateDpAsState(
        // region: Height of card, animated when QuickEditing, else static
        targetValue = if (isThisTaskQuickEditing) {
            120.dp // Inline Quick Editing
        }
        // Regular task view
        else {
            when (task.type) {
                // Based on duration
                TaskTypes.MAIN,
                TaskTypes.HELPER,
                TaskTypes.BASICS -> {
                    task.duration?.let { (1.dp * it).coerceAtLeast(60.dp) } ?: 60.dp
                }

                // Fixed
                TaskTypes.QUICK -> 40.dp
                else -> 55.dp
            }
        },
        animationSpec = spring(
            stiffness = Spring.StiffnessMedium, // Slower or faster animations
            dampingRatio = Spring.DampingRatioLowBouncy // Control how bouncy or smooth it is
        ),
        label = "card height animation",
        // endregion
    )

    val cardBorder = when {
        isTaskWithinCurrentTimeRange.value -> null
        isThisTaskQuickEditing -> BorderStroke(1.dp, MaterialTheme.colorScheme.secondaryContainer)
        isThisTaskClicked -> null
        else -> null
    }

    val topPadding = 10.dp

    val outsideCardPadding = when {
        // Space outside each card (NOT inside within the content)
        isThisTaskQuickEditing -> PaddingValues(0.dp, 0.dp, 0.dp, 0.dp)
        task.type == TaskTypes.MAIN -> PaddingValues(0.dp, 0.dp, 0.dp, 0.dp)
        task.type == TaskTypes.QUICK -> PaddingValues(0.dp, 0.dp, 0.dp, 0.dp)
        task.type == TaskTypes.HELPER -> PaddingValues(0.dp, 0.dp, 0.dp, 0.dp)
        task.type == TaskTypes.BASICS -> PaddingValues(0.dp, 0.dp, 0.dp, 0.dp)
        else -> PaddingValues(0.dp, 0.dp, 0.dp, 0.dp)
    }

    // endregion

    // get task within current time range
    LaunchedEffect(Unit) {
        val currentTime = LocalTime.now()
        isTaskWithinCurrentTimeRange.value = task.startTime?.let { start ->
            task.endTime?.let { end ->
                start <= currentTime && currentTime < end
            }
        } ?: false // Default to false if either startTime or endTime is null
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { offset ->
                        longPressOffset = offset
                        isShowContextMenu = true
                        onStateChangeEvents.onTaskLongPress(task)
                    },
                    onTap = { onStateChangeEvents.onTaskClick(task) }
                )
            }
    ) {
        Card(
            // region: card properties
            modifier = Modifier
                .padding(outsideCardPadding)
                .height(cardHeight) // animated height
                .alpha(if (isAnotherTaskEditing) 0.3f else 1f),
            colors = CardDefaults.cardColors(containerColor = cardBgColor.copy(alpha = 0.6f)),
            shape = RectangleShape,
            border = cardBorder
            // endregion

        ) {

            // region: Placement of Items
            Row {
                Spacer(modifier = Modifier.width(5.dp))

                HourColumn(
                    // region: arguments
                    startTime = task.startTime,
                    duration = task.duration,
                    isThisTaskClicked = isThisTaskClicked,
                    isCurrentTask = isTaskWithinCurrentTimeRange.value,
                    height = cardHeight,
                    topPadding=topPadding
                    //endregion
                )

                // Quick Editing
                if (isThisTaskQuickEditing) {
                    InLineQuickEdit(
                        mainTasks = mainTasks,
                        task = task,
                        onCancelClick = onStateChangeEvents.onCancelClick,
                        isFullEditing = isThisTaskFullEditing,
                        onShowFullEditClick = { id ->
                            coroutineScope.launch {
                                onStateChangeEvents.onShowFullEditClick(task)
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

                // Full Editing
                if (isThisTaskFullEditing) {
                    FullEditDialog(
                        mainTasks = mainTasks,
                        task = task,
                        onConfirmEdit = { updatedTaskForm ->
                            coroutineScope.launch {
                                onDataOperationEvents.onUpdateTaskConfirmClick(
                                    updatedTaskForm
                                )
                            }
                        },
                        onCancel = onStateChangeEvents.onCancelClick
                    )
                }

                // Show Task Details
                else if (isThisTaskShowDetails) {
                    TaskDetailsDialog(
                        task = task,
                        onDismiss = onStateChangeEvents.onCancelClick
                    )
                }

                // The main view for Task
                else {
                    PrimaryTaskView(
                        task = task,
                        isThisTaskClicked = isThisTaskClicked,
                        stateChangeEvents = onStateChangeEvents,
                        cardHeight = cardHeight,
                        topPadding = topPadding,
                        bgColor = cardBgColor
                    )


                }
            }
            // endregion
        }

        // This has to be inside the parent box for dropdown menu to position correctly
        if (isShowContextMenu) {
            OptionsMenu(
                onViewClick = { onStateChangeEvents.onShowTaskDetailsClick },
                onDismissRequest = { isShowContextMenu = false },
                offset = DpOffset(
                    x = with(density) { longPressOffset.x.toDp() + 10.dp },
                    y = with(density) { longPressOffset.y.toDp() - 112.dp }
                ),
                onEditClick = {
                    isShowContextMenu = false
                    onStateChangeEvents.onShowFullEditClick
                },
                onDeleteClick = {
                    isShowContextMenu = false
                    onDataOperationEvents.onDeleteTaskConfirmClick
                }
            )
        }
    }

    // region: Space at the bottom used as visible underline for clicked task
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(3.dp) // Thickness of the line
            .background(
                if (isThisTaskClicked) customColors.clickedTaskLineColor.copy(
                    alpha = 0.5f
                )
                else Color.Transparent
            )
    )
    // endregion

}
