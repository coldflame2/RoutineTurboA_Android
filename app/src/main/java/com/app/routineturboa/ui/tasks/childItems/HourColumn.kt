package com.app.routineturboa.ui.tasks.childItems

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Start
import androidx.compose.material.icons.sharp.Timer
import androidx.compose.material.icons.twotone.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.routineturboa.ui.theme.LocalCustomColors
import com.app.routineturboa.utils.SineEasing
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun HourColumn(
    isThisTaskClicked: Boolean,
    isCurrentTask: Boolean,
    height: Dp,
    startTime: LocalTime?,
    duration: Int?,
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
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = SineEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Animated Alpha"
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

    val durationFontStyle = MaterialTheme.typography.titleMedium.copy(
        fontSize = 12.sp, fontWeight = FontWeight.ExtraLight,
        color = customColors.taskNameFontColor.copy(alpha = 0.5f),
    )

    // endregion

    Card (
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RectangleShape
    ) {
        Column(
            modifier = Modifier
                .height(height)
                .width(80.dp)
                .padding(
                    start = 4.dp,
                    top = topPadding - 1.dp,
                )
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier.padding(start = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Start,
                    modifier = Modifier.size(12.dp).alpha(0.5f),
                    tint = customColors.gray400,
                    contentDescription = "StartTime"
                )

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

            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier.padding(start = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Sharp.Timer,
                    tint = MaterialTheme.colorScheme.surfaceTint,
                    modifier = Modifier.size(12.dp),
                    contentDescription = "Duration"
                )

                Text(
                    text = "$duration m",
                    style = durationFontStyle
                )
            }

        }

    }


}