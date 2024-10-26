package com.app.routineturboa.ui.tasks.childItems

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.app.routineturboa.data.room.entities.TaskEntity
import com.app.routineturboa.ui.models.TaskFormData
import com.app.routineturboa.data.dbutils.Converters.timeToString
import com.app.routineturboa.ui.reusable.fields.QuickEditInputTextField
import com.app.routineturboa.ui.theme.LocalCustomColors

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun InLineQuickEdit(
    mainTasks: List<TaskEntity>?,
    task: TaskEntity,
    isFullEditing: Boolean,
    onShowFullEditClick: (Int) -> Unit,
    onUpdateTaskConfirmClick: (TaskFormData) -> Unit,
    onCancelClick: () -> Unit,
) {
    val tag = "QuickEditScreen"
    val context = LocalContext.current

    val customColors = LocalCustomColors.current

    // State variables to hold the edited values
    var editedName by remember { mutableStateOf(task.name) }
    var durationString by remember { mutableStateOf(task.duration.toString()) }
    val endTime by remember { mutableStateOf(task.endTime) }
    var endTimeString by remember { mutableStateOf(timeToString(endTime)) }

    fun onSave() {
        try {
            val newDuration = durationString.toInt()
            val newEndTime = task.startTime?.plusMinutes(newDuration.toLong())

            val updatedTaskFormData = TaskFormData(
                id = task.id,
                name = editedName,
                startTime = task.startTime,
                endTime = newEndTime,
                notes = task.notes,
                taskType = task.type,
                position = task.position,
                duration = newDuration,
                reminder = task.reminder,
                mainTaskId = task.mainTaskId,
                startDate = task.startDate,
                isRecurring = task.isRecurring ?: false,
                recurrenceType = task.recurrenceType,
                recurrenceInterval = task.recurrenceInterval,
                recurrenceEndDate = task.recurrenceEndDate
            )
            onUpdateTaskConfirmClick(updatedTaskFormData)
        }

        catch (e: Exception) {
            Log.e(tag, "Error: $e")
            Toast.makeText(
                context,
                "Error saving task values: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Column(modifier = Modifier.padding(5.dp)) {
        // region: name and duration input fields
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            // Task Name input
            Box(
                modifier = Modifier
                    .weight(2f)
                    .height(50.dp)
                    .background(
                        color = customColors.gray200.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small
                    ),
            ) {
                QuickEditInputTextField(
                    value = editedName,
                    onValueChange = { newName -> editedName = newName },
                    placeholder = "Task's Name..."
                )
            }

            // Duration input
            Box(
                modifier = Modifier
                    .weight(0.7f)
                    .height(50.dp)
                    .background(
                        color = customColors.gray200.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small
                    ),
            ) {
                QuickEditInputTextField(
                    value = durationString,
                    onValueChange = { newDuration -> durationString = newDuration },
                    placeholder = "duration.."
                )
            }

        }

        // endregion

        Spacer(modifier = Modifier.height(18.dp))

        // region: Save and full-edit Buttons Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(35.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Full Edit Button
            QuickEditActionsButtons(
                label = "Full Edit",
                containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha=0.5f),
                contentColor = MaterialTheme.colorScheme.onTertiary.copy(alpha=0.7f),
                width = 80.dp,
                onClick = { onShowFullEditClick(task.id) }
            )

            // Cancel Button
            QuickEditActionsButtons (
                label = "Update",
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                width = 120.dp,
                onClick = { onSave() }
            )
        }
        // endregion
    }
}

@Composable
fun QuickEditActionsButtons(
    label: String = "Button",
    containerColor: Color = MaterialTheme.colorScheme.tertiary,
    contentColor: Color = MaterialTheme.colorScheme.onTertiary,
    width: Dp = 120.dp,
    onClick: () -> Unit
){
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxHeight().width(width),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(15.dp),
        contentPadding = PaddingValues(5.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge
        )
    }
}