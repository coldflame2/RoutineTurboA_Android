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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.LibraryBooks
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.routineturboa.data.room.TaskEntity
import com.app.routineturboa.shared.DataOperationEvents
import com.app.routineturboa.shared.StateChangeEvents
import com.app.routineturboa.shared.TasksBasedOnState
import com.app.routineturboa.shared.UiStates
import com.app.routineturboa.ui.tasks.childItems.OptionalTaskTimings
import com.app.routineturboa.ui.tasks.childItems.HourColumn
import com.app.routineturboa.ui.tasks.childItems.InLineQuickEdit
import com.app.routineturboa.ui.tasks.controls.OptionsMenu
import com.app.routineturboa.ui.tasks.dialogs.TaskDetailsDialog
import com.app.routineturboa.ui.reusable.AlphabetIcon
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
    tasksBasedOnState: TasksBasedOnState,
    uiStates: UiStates,
    stateChangeEvents: StateChangeEvents,
    dataOperationEvents: DataOperationEvents,
) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    var isShowContextMenu by remember { mutableStateOf(false) }
    var longPressOffset by remember { mutableStateOf(Offset.Zero) }

    val isTaskWithinCurrentTimeRange = remember { mutableStateOf(false) }  // Set in Launched Effect

    val isThisTaskClicked = (tasksBasedOnState.clickedTask?.id == task.id)
    val isThisTaskQuickEditing = (tasksBasedOnState.inEditTask?.id == task.id)
    val isThisTaskFullEditing = uiStates.isFullEditing && (tasksBasedOnState.inEditTask?.id == task.id)
    val isThisTaskShowDetails = (uiStates.isShowingDetails) && (tasksBasedOnState.showingDetailsTask?.id == task.id)

    val isAnotherTaskEditing = uiStates.isQuickEditing && (tasksBasedOnState.inEditTask?.id != task.id)

    var taskTypeFirstLetter = ' ' // Initialize Empty
    if (task.type?.isNotEmpty() == true) {
        taskTypeFirstLetter = task.type[0] // Get first letter of task type
    }

    val customColors = LocalCustomColors.current

    val cardBgColor = when {
        isAnotherTaskEditing -> customColors.gray100
        task.type == TaskTypes.MAIN -> customColors.mainTaskColor
        task.type == TaskTypes.BASICS -> customColors.basicsTaskColor
        task.type == TaskTypes.HELPER -> customColors.helperTaskColor
        task.type == TaskTypes.QUICK -> customColors.quickTaskColor
        else -> MaterialTheme.colorScheme.surface.copy(alpha = 1f)
    }

    val cardHeight: Dp by animateDpAsState(
        targetValue = if (isThisTaskQuickEditing) {
            120.dp // Inline Quick Editing
        } else {
            // Regular task view
            when (task.type) {
                TaskTypes.MAIN, TaskTypes.HELPER, TaskTypes.BASICS -> {
                    task.duration?.let { (1.dp * it).coerceAtLeast(55.dp) } ?: 55.dp
                }
                TaskTypes.QUICK -> 40.dp
                else -> 55.dp
            }
        },
        animationSpec = spring(
            stiffness = Spring.StiffnessMedium, // Slower or faster animations
            dampingRatio = Spring.DampingRatioLowBouncy // Control how bouncy or smooth it is
        ),
        label = "card height animation",
    )


    val cardBorder = when {
        isTaskWithinCurrentTimeRange.value -> null
        isThisTaskQuickEditing -> BorderStroke(1.dp, MaterialTheme.colorScheme.secondaryContainer)
        isThisTaskClicked -> null
        else -> null
    }

    val outsideCardPadding = when {
        // Space outside the card (NOT inside within the content)
        isThisTaskQuickEditing -> PaddingValues(0.dp, 10.dp, 0.dp, 10.dp)
        task.type == TaskTypes.MAIN -> PaddingValues(0.dp, 0.dp, 0.dp, 0.dp)
        task.type == TaskTypes.QUICK -> PaddingValues(0.dp, 0.dp, 0.dp, 0.dp)
        task.type == TaskTypes.HELPER -> PaddingValues(0.dp, 0.dp, 0.dp, 0.dp)
        task.type == TaskTypes.BASICS -> PaddingValues(0.dp, 0.dp, 0.dp, 0.dp)
        else -> PaddingValues(0.dp, 0.dp, 0.dp, 0.dp)
    }

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
                        stateChangeEvents.onTaskLongPress(task)
                    },
                    onTap = { stateChangeEvents.onTaskClick(task) }
                )
            }
    ) {
        Card(
            modifier = Modifier
                .padding(outsideCardPadding)
                .height(cardHeight) // animated height
                .alpha(if (isAnotherTaskEditing) 0.3f else 1f),
            colors = CardDefaults.cardColors(containerColor = cardBgColor),
            shape = RectangleShape,
            border = cardBorder
        ) {
            Row {
                HourColumn(
                    isThisTaskClicked = isThisTaskClicked,
                    isCurrentTask = isTaskWithinCurrentTimeRange.value,
                    cardHeight = cardHeight,
                    startTime = task.startTime
                )

                if (isThisTaskQuickEditing) {
                    InLineQuickEdit(
                        mainTasks = mainTasks,
                        task = task,
                        isFullEditing = isThisTaskFullEditing,
                        onCancelClick = stateChangeEvents.onCancelClick,
                        onShowFullEditClick = { id ->
                            coroutineScope.launch {
                                stateChangeEvents.onShowFullEditClick(task)
                            }
                        },
                        onUpdateTaskConfirmClick = { id, editedFormData->
                            coroutineScope.launch {
                                dataOperationEvents.onUpdateTaskConfirmClick(task, editedFormData)
                            }
                        }
                    )
                }

                else if (isThisTaskShowDetails) {
                    TaskDetailsDialog(
                        task = task,
                        onDismiss = stateChangeEvents.onCancelClick
                    )
                }

                else {
                    // View for Task Details
                    Column{
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Task Type Icon
                            AlphabetIcon(
                                letter = taskTypeFirstLetter,
                                modifier = Modifier.padding(top = 8.dp),
                                backgroundColor = cardBgColor,
                            )

                            // taskName
                            Text(
                                text = task.name,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                                modifier = Modifier
                                    .weight(0.3f)
                                    .padding(start = 8.dp, top = 8.dp)
                            )

                            // Task Duration
                            Text(
                                text = "${task.duration} min",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier
                                    .weight(0.09f)
                            )

                            // Show Task Details Icon
                            IconButton(
                                onClick = { stateChangeEvents.onShowTaskDetailsClick(task) },
                                modifier = Modifier
                                    .size(18.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Sharp.LibraryBooks,
                                    contentDescription = "Show Task Details",
                                    tint = if (isThisTaskClicked) MaterialTheme.colorScheme.surfaceTint.copy(alpha = 1f)
                                    else MaterialTheme.colorScheme.surfaceTint.copy(alpha = 1f)
                                )
                            }

                            Spacer(modifier = Modifier.width(15.dp))

                            // Quick Edit Task Icon
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        stateChangeEvents.onShowQuickEditClick(task)
                                    }
                                },
                                modifier = Modifier
                                    .alpha(0.7f)
                                    .size(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Task",
                                    tint = if (isThisTaskClicked) customColors.editIconColor
                                    else customColors.gray300.copy(alpha = 0.8f)
                                )
                            }
                        }

                        /**
                         * Below MainTaskDetails (visible only if enough card height)
                         */
                        if (cardHeight > 60.dp) {
                            Spacer(modifier = Modifier.height(10.dp))
                            OptionalTaskTimings(
                                startTime = task.startTime,
                                endTime = task.endTime,
                                duration = task.duration ?: 0,
                            )
                        }
                    }
                }
            }
            // Add an underline/bottom border if this task is clicked

        }


        // This has to be inside the parent box for dropdown menu to position correctly
        if (isShowContextMenu) {
            OptionsMenu(
                onViewClick = { stateChangeEvents.onShowTaskDetailsClick},
                onDismissRequest = { isShowContextMenu = false },
                offset = DpOffset(
                    x = with(density) { longPressOffset.x.toDp() + 10.dp },
                    y = with(density) { longPressOffset.y.toDp() - 112.dp }
                ),
                onEditClick = {
                    isShowContextMenu = false
                    stateChangeEvents.onShowFullEditClick
                },
                onDeleteClick = {
                    isShowContextMenu = false
                    dataOperationEvents.onDeleteTaskConfirmClick
                }
            )
        }
    }

    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(3.dp) // Thickness of the line
            .padding(horizontal = 0.dp) // Optional padding for the line
            .background(
                if (!isThisTaskClicked) Color.Transparent
                else MaterialTheme.colorScheme.primary
            ) // Color of the line
    )
}
