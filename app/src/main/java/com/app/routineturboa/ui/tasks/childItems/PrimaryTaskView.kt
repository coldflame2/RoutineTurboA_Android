package com.app.routineturboa.ui.tasks.childItems

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.routineturboa.core.dbutils.Converters.timeToUiString
import com.app.routineturboa.core.models.DataOperationEvents
import com.app.routineturboa.data.room.entities.TaskEntity
import com.app.routineturboa.core.models.StateChangeEvents
import com.app.routineturboa.ui.tasks.dropdowns.TaskOptionsMenu
import com.app.routineturboa.ui.theme.LocalCustomColors

@Composable
fun PrimaryTaskView(
    task: TaskEntity,
    isThisTaskClicked: Boolean = false,
    isThisTaskLongPressMenu: Boolean = false,
    isThisLatestTask: Boolean = false,

    stateChangeEvents: StateChangeEvents,
    dataOperationEvents: DataOperationEvents,

    cardHeight: Dp = 55.dp,
    topPadding: Dp = 10.dp,
    bgColor: Color = Color.LightGray,

    forReferenceView: Boolean = false, // Used for reference view in AddNewTask Screen
    taskNameFontSize: TextUnit = 15.sp,
) {
    // region: variables
    val tag = "PrimaryTaskView"
    val coroutineScope = rememberCoroutineScope()
    val customColors = LocalCustomColors.current

    var taskTypeFirstLetter = ' ' // Initialize Empty
    if (task.type?.isNotEmpty() == true) {
        taskTypeFirstLetter = task.type[0].uppercaseChar() // Get first letter of task type
    }

    val minHeightForOptionalTimings = 30.dp

    val taskNameFontStyle = MaterialTheme.typography.titleMedium.copy(
        fontSize = taskNameFontSize, fontWeight = FontWeight.Normal, color = customColors.taskNameFontColor
    )

    // State variable to track whether the text overflows
    var isTextOverflowing by remember { mutableStateOf(false) }

    val dynamicTopPadding = when {
        isTextOverflowing -> topPadding-5.dp
        else -> topPadding
    }

    val maxLinesForTaskName = when {
        cardHeight > minHeightForOptionalTimings -> 3
        else -> 2
    }

    val onTextOverflow = when {
        isTextOverflowing -> TextOverflow.Ellipsis
        else -> TextOverflow.Visible
    }

    val taskTypeLetterIconBgColor = when {
        isThisLatestTask -> Color.Magenta.copy(alpha = 0.5f) // Latest task color prioritized
        else -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
    }

    val endTime = timeToUiString(task.endTime)
    val endTimeText = if (forReferenceView) "EndTime: $endTime" else endTime

    // endregion

    // Primary Task View and Optional Task Timings if card height enough
    Column (
        modifier = Modifier.padding(0.dp).fillMaxWidth()
            .animateContentSize() // Smoothly animates any height changes
    ) {
        // upperRow (Row-TaskName+AlphabetIcon) + (Column-DropdownIcon+EndTime)
        Row(
            modifier = Modifier.padding(0.dp)
        ) {
            // Name and Alphabet-TaskType Icon
            Row (
                modifier = Modifier.padding().weight(1f)
            ) {
                // taskName
                Text(
                    text = task.name,
                    maxLines = maxLinesForTaskName,
                    overflow = onTextOverflow,
                    style = taskNameFontStyle,
                    modifier = Modifier.padding(start = 5.dp, end = 1.dp, top=topPadding),
                    onTextLayout = { textLayoutResult ->
                        // Check if the text did overflow
                        isTextOverflowing = textLayoutResult.hasVisualOverflow
                    }
                )

                // Alphabet Icon with Position
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(start = 3.dp, top = 4.dp)
                        .size(18.dp)
                        .background(color = taskTypeLetterIconBgColor, shape = CircleShape)
                ) {
                    Text(
                        text = taskTypeFirstLetter.toString(),
                        textAlign = TextAlign.Center,
                        fontSize = 8.sp,
                        modifier = Modifier
                            .offset(x = 0.dp, y = (-2).dp)
                    )
                }

                // Duration
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(start = 3.dp, top = 4.dp)
                        .size(18.dp)
                        .background(color = taskTypeLetterIconBgColor, shape = CircleShape)
                ) {
                    Text(
                        text = task.duration.toString(),
                        textAlign = TextAlign.Center,
                        fontSize = 8.sp,
                        modifier = Modifier
                            .offset(x = 0.dp, y = (-2).dp)
                    )
                }

            }

            // Right-most area (endTime, DropDown context menu)
            Column(
                verticalArrangement = Arrangement.SpaceBetween, // Pushes the children to the top and bottom
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .padding(top = 2.dp, end = 10.dp)
            ) {
                // DropDown Menu
                if(!forReferenceView) {  // Not when used as reference View
                    TaskOptionsMenu(
                        task = task,
                        bgColor = bgColor,
                        coroutineScope = coroutineScope,
                        stateChangeEvents = stateChangeEvents,
                        dataOperationEvents = dataOperationEvents,
                        isThisTaskLongPressMenu = isThisTaskLongPressMenu,
                    )
                }

                // Task End Time
                Box(
                    modifier = Modifier.padding(bottom=5.dp)
                ) {
                    Text(
                        text = endTimeText ?: "-",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = taskNameFontStyle.copy(fontSize = 10.sp),
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(1.dp))

        // OptionalTaskTimings (visible only if enough card height)
        if (cardHeight > 85.dp) {
            Row {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = "optionalTimings",
                    modifier = Modifier.size(15.dp)
                )

                DetailedTimingsView(
                    startTime = task.startTime,
                    endTime = task.endTime,
                    duration = task.duration ?: 0,
                )
            }
        }

    }
}