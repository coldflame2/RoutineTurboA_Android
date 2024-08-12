package com.app.routineturboa.ui.tasks

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.app.routineturboa.data.room.TaskEntity
import com.app.routineturboa.ui.models.TaskEventsToFunctions
import com.app.routineturboa.ui.tasks.childItems.ExtraTaskDetails
import com.app.routineturboa.ui.tasks.childItems.HourColumn
import com.app.routineturboa.ui.tasks.childItems.MainTaskDetails
import com.app.routineturboa.ui.tasks.childItems.InLineQuickEdit
import com.app.routineturboa.ui.tasks.controls.OptionsMenu
import com.app.routineturboa.ui.tasks.dialogs.TaskDetailsDialog
import com.app.routineturboa.ui.theme.LocalCustomColorsPalette
import com.app.routineturboa.ui.models.TasksUiState
import com.app.routineturboa.utils.TaskTypes
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.S)
@Composable
// Usage: TasksLazyColumn
fun ParentTaskItem(
    task: TaskEntity,
    mainTasks: List<TaskEntity>,
    tasksUiState: TasksUiState,
    taskEventsToFunctions: TaskEventsToFunctions,
) {
    val density = LocalDensity.current
    var isDropDownExpanded by remember { mutableStateOf(false) }
    var longPressOffset by remember { mutableStateOf(Offset.Zero) }

    val isTaskWithinCurrentTimeRange = remember { mutableStateOf(false) }  // Set in Launched Effect

    val isThisTaskClicked = (tasksUiState.clickedTaskId == task.id)
    val isThisTaskQuickEditing = tasksUiState.inEditTaskId == task.id
    val isThisTaskFullEditing = tasksUiState.isFullEditing && tasksUiState.inEditTaskId == task.id
    val isThisTaskShowDetails = (tasksUiState.isShowingDetails) && (tasksUiState.showingDetailsTaskId == task.id)

    val isAnotherTaskEditing = tasksUiState.isQuickEditing && tasksUiState.inEditTaskId != task.id

    val cardBgColor = when {
        isAnotherTaskEditing -> LocalCustomColorsPalette.current.gray400.copy(alpha = 0.1f)
        task.type == TaskTypes.MAIN -> LocalCustomColorsPalette.current.mainTaskColor.copy(alpha = 0.5f)
        task.type == TaskTypes.BASICS -> LocalCustomColorsPalette.current.basicsTaskColor.copy(alpha = 0.5f)
        task.type == TaskTypes.HELPER -> LocalCustomColorsPalette.current.helperTaskColor.copy(alpha = 0.5f)
        task.type == TaskTypes.QUICK -> LocalCustomColorsPalette.current.quickTaskColor.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.surface.copy(alpha = 1f)
    }

    val cardHeight: Dp by animateDpAsState(
        targetValue = if (isThisTaskQuickEditing) {
            120.dp
        } else {
            when (task.type) {
                TaskTypes.MAIN -> (1.dp * task.duration).coerceAtLeast(35.dp)
                TaskTypes.QUICK, TaskTypes.HELPER, TaskTypes.BASICS -> 35.dp
                else -> 30.dp
            }
        },
        animationSpec = spring(
            stiffness = Spring.StiffnessMedium,  // Adjust stiffness for slower or faster animations
            dampingRatio = Spring.DampingRatioLowBouncy  // Control how bouncy or smooth it is
        ),
        label = "",  // Customize animation spec if necessary
    )

    val cardBorder = when {
        isTaskWithinCurrentTimeRange.value -> null
        isThisTaskQuickEditing -> BorderStroke(1.dp, MaterialTheme.colorScheme.secondaryContainer)
        isThisTaskClicked -> null
        else -> null
    }

    val cardPadding = when {
        // Space outside the card
        isThisTaskQuickEditing -> PaddingValues(0.dp, 10.dp, 0.dp, 10.dp)
        task.type == TaskTypes.MAIN -> PaddingValues(0.dp, 0.dp, 0.dp, 0.dp)
        task.type == TaskTypes.QUICK -> PaddingValues(0.dp, 0.dp, 0.dp, 0.dp)
        task.type == TaskTypes.HELPER -> PaddingValues(0.dp, 0.dp, 0.dp, 0.dp)
        task.type == TaskTypes.BASICS -> PaddingValues(0.dp, 0.dp, 0.dp, 0.dp)
        else -> PaddingValues(0.dp, 0.dp, 0.dp, 0.dp)
    }

    LaunchedEffect (Unit) {
        val currentTime = LocalDateTime.now()
        isTaskWithinCurrentTimeRange.value = (task.startTime <= currentTime && currentTime < task.endTime)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { offset ->
                        longPressOffset = offset
                        isDropDownExpanded = true
                        taskEventsToFunctions.onAnyTaskLongPress(task.id)
                    },
                    onTap = { taskEventsToFunctions.onAnyTaskClick(task.id) }
                )
            }
    ) {
        Card(
            modifier = Modifier
                .padding(cardPadding)
                .alpha(if (isAnotherTaskEditing) 0.3f else 1f)
                .height(cardHeight), // Use animated height
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

            // between HourColumn and Main Task Card
            Spacer(modifier = Modifier.width(3.dp))


                Column {
                    if (isThisTaskQuickEditing) {
                        InLineQuickEdit(
                            mainTasks = mainTasks,
                            task = task,
                            isFullEditing = isThisTaskFullEditing,
                            onFullEditClick = {id->taskEventsToFunctions.onFullEditClick(id)},
                            onConfirmEdit = {id, editedFormData->
                                taskEventsToFunctions.onConfirmEdit(id, editedFormData)
                            },
                            onCancel = taskEventsToFunctions.onCancelClick
                        )
                        
                    } else {
                        MainTaskDetails(
                            taskName = task.name,
                            taskId = task.id,
                            taskDuration = task.duration,
                            taskType = task.type,
                            isThisTaskClicked = isThisTaskClicked,
                            onQuickEditClick = {id->taskEventsToFunctions.onQuickEditClick(id)},
                            onShowTaskDetails = {id->taskEventsToFunctions.onShowTaskDetails(id)},
                            cardColor = cardBgColor,
                        )

                        if (task.type != TaskTypes.QUICK) {

                            Spacer(modifier = Modifier.height(5.dp))

                            /**
                             * This is shown below MainTaskDisplay and is based on the height of the card.
                             */
                            ExtraTaskDetails(
                                startTime = task.startTime,
                                endTime = task.endTime,
                                duration = task.duration,
                            )

                            if (isThisTaskShowDetails) {
                                TaskDetailsDialog(
                                    task = task,
                                    onDismiss = taskEventsToFunctions.onCancelClick
                                )
                            }
                        }
                    }
                }
            }
        }

        // This has to be inside the parent box for dropdown menu to position correctly
        if (isDropDownExpanded) {
            OptionsMenu(
                onViewClick = { taskEventsToFunctions.onShowTaskDetails},
                onDismissRequest = { isDropDownExpanded = false },
                offset = DpOffset(
                    x = with(density) { longPressOffset.x.toDp() + 10.dp },
                    y = with(density) { longPressOffset.y.toDp() - 112.dp }
                ),
                onEditClick = {
                    isDropDownExpanded = false
                    taskEventsToFunctions.onFullEditClick
                },
                onDeleteClick = {
                    isDropDownExpanded = false
                    taskEventsToFunctions.onDeleteClick

                }
            )
        }
    }
}
