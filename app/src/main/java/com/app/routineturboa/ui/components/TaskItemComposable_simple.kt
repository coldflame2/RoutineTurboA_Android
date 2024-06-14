package com.app.routineturboa.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.app.routineturboa.data.model.Task
import com.app.routineturboa.utils.TimeUtils.convertTo12HourFormat

@Composable
fun TaskItemComposable(task: Task) {
    Card{
        Column{
            Row{
                Text(text = task.taskName)
                Icon(imageVector = Icons.Default.Task,
                    contentDescription = null)
            }
            Row{
                Text(text = "${convertTo12HourFormat(task.startTime.split(" ")[1])} - ${convertTo12HourFormat(task.endTime.split(" ")[1])}")
                Text(text = "${task.duration} minutes")
            }
        }
    }
}

