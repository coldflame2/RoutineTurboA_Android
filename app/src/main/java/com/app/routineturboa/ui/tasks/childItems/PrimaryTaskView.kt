package com.app.routineturboa.ui.tasks.childItems

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.routineturboa.data.room.TaskEntity
import com.app.routineturboa.shared.StateChangeEvents
import com.app.routineturboa.ui.reusable.TaskTypeLetterIcon
import com.app.routineturboa.ui.theme.LocalCustomColors
import kotlinx.coroutines.launch

@Composable
fun PrimaryTaskView(
    task: TaskEntity,
    isThisTaskClicked: Boolean,
    cardHeight: Dp,
    stateChangeEvents: StateChangeEvents,
    topPadding: Dp,
) {
    val tag = "PrimaryTaskView"
    val coRoutineScope = rememberCoroutineScope()
    val customColors = LocalCustomColors.current

    val minHeightForOptionalTimings = 60.dp

    val taskNameFontStyle = MaterialTheme.typography.titleMedium.copy(
        fontSize = 15.sp, fontWeight = FontWeight.Light, color = customColors.taskNameFontColor
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


    Column( // Primary Task View and Optional Task Timings if card height enough
        modifier = Modifier.padding(start = 8.dp, top = dynamicTopPadding)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // taskName
            Text(
                text = task.name,
                maxLines = maxLinesForTaskName,
                overflow = onTextOverflow,
                style = taskNameFontStyle,
                modifier = Modifier
                    .weight(0.3f)
                    .padding(end = 10.dp),
                onTextLayout = { textLayoutResult ->
                    // Check if the text did overflow
                    isTextOverflowing = textLayoutResult.hasVisualOverflow
                }
            )

            Text(
                text = "${task.duration} min",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = taskNameFontStyle.copy(fontSize = 10.sp),
                modifier = Modifier
                    .weight(0.09f)
            )


            Spacer(modifier = Modifier.width(2.dp))

            IconButton(
                onClick = {
                    coRoutineScope.launch {
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