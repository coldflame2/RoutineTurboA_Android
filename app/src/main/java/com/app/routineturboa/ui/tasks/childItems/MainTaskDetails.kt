package com.app.routineturboa.ui.tasks.childItems

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.LibraryBooks
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.routineturboa.ui.reusable.AlphabetIcon


@Composable
fun MainTaskDetails (
    taskName: String,
    taskId: Int,
    taskDuration: Int,
    taskType: String,

    isThisTaskClicked: Boolean,
    onQuickEditClick: (Int) -> Unit,
    onShowTaskDetails: (Int) -> Unit,
    cardColor: Color
) {
    var taskTypeFirstLetter = 'X'
    if (taskType.isNotEmpty()) {
        taskTypeFirstLetter = taskType[0]
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Task Type Icon
        AlphabetIcon(
            letter = taskTypeFirstLetter,
            modifier = Modifier.padding(top = 2.dp),
            backgroundColor = cardColor,
        )

        // taskName
        Text(
            text = taskName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
            modifier = Modifier
                .weight(0.3f)
                .padding(start = 6.dp, top = 2.dp)
        )

        // Task Duration
        Text(
            text = "$taskDuration min",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            ),
            modifier = Modifier
                .weight(0.08f)
        )

        // Show Task Details Icon
        IconButton(
            onClick = {onShowTaskDetails(taskId)},
            modifier = Modifier
                .size(18.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Sharp.LibraryBooks,
                contentDescription = "Show Task Details",
                tint = if (isThisTaskClicked) MaterialTheme.colorScheme.surfaceTint.copy(alpha = 1f)
                else MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.4f)
            )
        }

        Spacer(modifier = Modifier.width(15.dp))

        // Edit Task Icon
        IconButton(
            onClick = {
                onQuickEditClick(taskId)
            },
            modifier = Modifier
                .alpha(if (taskType == "QuickTask") 0.5f else 0.7f)
                .size(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Task",
                tint = if (isThisTaskClicked) MaterialTheme.colorScheme.surfaceTint.copy(alpha = 1f)
                else MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.4f)
            )
        }
    }
}