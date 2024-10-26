package com.app.routineturboa.ui.tasks.childItems

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.routineturboa.data.dbutils.Converters.timeToUiString
import com.app.routineturboa.data.room.entities.TaskEntity
import com.app.routineturboa.shared.StateChangeEvents
import com.app.routineturboa.ui.theme.LocalCustomColors
import kotlinx.coroutines.launch

@Composable
fun PrimaryTaskView(
    task: TaskEntity,
    isThisTaskClicked: Boolean,
    cardHeight: Dp,
    stateChangeEvents: StateChangeEvents,
    topPadding: Dp,
    forReferenceView: Boolean = false,
    bgColor: Color = Color.LightGray,
) {
    val tag = "PrimaryTaskView"
    val coRoutineScope = rememberCoroutineScope()
    val customColors = LocalCustomColors.current

    var taskTypeFirstLetter = ' ' // Initialize Empty
    if (task.type?.isNotEmpty() == true) {
        taskTypeFirstLetter = task.type[0].uppercaseChar() // Get first letter of task type
    }

    val minHeightForOptionalTimings = 60.dp

    val taskNameFontStyle = MaterialTheme.typography.titleMedium.copy(
        fontSize = 15.sp, fontWeight = FontWeight.Normal, color = customColors.taskNameFontColor
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

    val taskTypeLetterIconBgColor = if (isThisTaskClicked) {
        MaterialTheme.colorScheme.tertiary.copy(alpha=0.6f)
    } else {
        MaterialTheme.colorScheme.tertiary.copy(alpha=0.4f)
    }

    val endTime = timeToUiString(task.endTime)
    val endTimeText = if (forReferenceView) "EndTime: $endTime" else endTime


    Column( // Primary Task View and Optional Task Timings if card height enough
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row ( modifier = Modifier.weight(0.3f)) {
                // taskName
                Text(
                    text = task.name,
                    maxLines = maxLinesForTaskName,
                    overflow = onTextOverflow,
                    style = taskNameFontStyle,
                    modifier = Modifier.padding(end = 1.dp, top=topPadding),
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
            }

            // Right-most area
            Column(
                verticalArrangement = Arrangement.SpaceBetween, // Pushes the children to the top and bottom
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 8.dp)
            ) {
                IconButton(
                    onClick = {
                        if (!forReferenceView) {  // not when in new task creation screen
                            coRoutineScope.launch {
                                stateChangeEvents.onShowQuickEditClick(task)
                            }
                        }
                    },
                    modifier = Modifier
                        .alpha(if (forReferenceView) 0f else 1f)
                        .size(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Task",
                    )
                }

                // Task End Time
                Box(
                    modifier = Modifier
                        .background(
                            color = bgColor.copy(alpha=1f),
                            shape = RoundedCornerShape(5.dp),
                        )
                        .padding(1.dp)
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

        // OptionalTaskTimings (visible only if enough card height)
        if (cardHeight > minHeightForOptionalTimings) {
            Spacer(modifier = Modifier.height(10.dp))
            OptionalTaskTimings(
                startTime = task.startTime,
                endTime = task.endTime,
                duration = task.duration ?: 0,
            )
        }
    }
}