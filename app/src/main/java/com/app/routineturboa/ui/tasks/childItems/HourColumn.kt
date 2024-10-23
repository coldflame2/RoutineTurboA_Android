package com.app.routineturboa.ui.tasks.childItems

import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.routineturboa.ui.reusable.AnimatedAlphaUtils
import com.app.routineturboa.ui.theme.LocalCustomColors
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun HourColumn(
    isThisTaskClicked: Boolean,
    isCurrentTask: Boolean,
    cardHeight: Dp,
    startTime: LocalTime?,
    topPadding: Dp
) {
    val formattedStartTime = startTime?.format(DateTimeFormatter.ofPattern("hh:mm a"))
    val startTimeHourString = formattedStartTime?.substring(0, 2)
    val startTimeMinuteString = formattedStartTime?.substring(3, 5)
    val startTimeAinAm = formattedStartTime?.substring(6, 7)
    val startTimeMinAm = formattedStartTime?.substring(7, 8)

    val customColors = LocalCustomColors.current

    // region: Card background color and alpha, and fontWeight

    val fontWeight = when {
        isThisTaskClicked -> MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 11.sp, fontWeight = FontWeight.Thin)

        else -> MaterialTheme.typography.bodyLarge.copy(
                fontSize = 11.sp, fontWeight = FontWeight.Thin)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "BorderAnimation")

    val animatedAlpha = AnimatedAlphaUtils.animatedAlpha(
        transition = infiniteTransition,
        initialValue = 0f,
        targetValue = 1f,
        duration = 1000,
    )

    val backgroundColor = when {
        isCurrentTask -> {
            MaterialTheme.colorScheme.primary.copy(alpha = animatedAlpha)
        }
        isThisTaskClicked -> {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        }
        else -> {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        }
    }

    // endregion

    Card (
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RectangleShape
    ) {
        Row(
            modifier = Modifier
                .height(cardHeight)
                .width(62.dp)
                .padding(
                    start = 4.dp,
                    top = topPadding-1.dp,
                )
        ) {
            // Hour Strings (eg. "06:00")
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy((-1).dp),
            ) {
                Text(
                    text = startTimeHourString ?: "--",
                    style = fontWeight,
                )

                Text(
                    text = ":",
                    style = fontWeight,
                )

                Text(
                    text = startTimeMinuteString ?: "--",
                    style = fontWeight,
                )
            }

            Spacer(modifier = Modifier.width(1.dp))

            // AM/PM Strings
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy((-1).dp),
            ) {
                Text(
                    text = startTimeAinAm ?: "--", // A or P
                    style = fontWeight,
                )
                Text(
                    text = startTimeMinAm ?: "--", // M
                    style = fontWeight,
                )
            }
        }
    }


}