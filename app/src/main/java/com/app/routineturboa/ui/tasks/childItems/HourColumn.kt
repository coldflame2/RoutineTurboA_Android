package com.app.routineturboa.ui.tasks.childItems

import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.routineturboa.ui.reusable.AnimatedAlphaUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HourColumn(
    isThisTaskClicked: Boolean,
    isCurrentTask: Boolean,
    cardHeight: Dp,
    startTime: LocalDateTime
) {
    val formattedStartTime = startTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
    val startTimeHourString = formattedStartTime.substring(0, 2)
    val startTimeMinuteString = formattedStartTime.substring(3, 5)
    val startTimeAinAm = formattedStartTime.substring(6, 7)
    val startTimeMinAm = formattedStartTime.substring(7, 8)

    val infiniteTransition = rememberInfiniteTransition(label = "BorderAnimation")
    val animatedAlpha = AnimatedAlphaUtils.animatedAlpha(
        transition = infiniteTransition,
        initialValue = 0.2f,
        targetValue = 0.9f,
        duration = 400,
    )

    val backgroundColor = when {
        isThisTaskClicked -> {
            MaterialTheme.colorScheme.primary.copy(alpha = 1f)
        }
        isCurrentTask -> {
            MaterialTheme.colorScheme.primary.copy(alpha = animatedAlpha)
        }
        else -> {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        }
    }


    Card (
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .height(cardHeight)
                .width(50.dp)
                .padding(
                    start = 15.dp,
                    end = 13.dp,
                    top = 0.dp,
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy((-12).dp),
            ) {
                Text(
                    text = startTimeHourString,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 11.sp),
                )

                Text(
                    text = startTimeMinuteString,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 11.sp),
                )
            }

            Spacer(modifier = Modifier.width(1.dp))

            // AM/PM Strings
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy((-15).dp), // Adjust to control space between A/P and M
            ) {
                Text(
                    text = startTimeAinAm, // A or P
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSecondary
                    ),
                )
                Text(
                    text = startTimeMinAm, // M
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSecondary
                    ),
                )
            }
        }
    }
}