package com.app.routineturboa.ui.tasks.childItems

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun TaskTimings (
    startTime: LocalDateTime,
    endTime: LocalDateTime,
    duration: Int,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AccessTime,
            contentDescription = "End Time",
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            modifier = Modifier.size(16.dp)
        )

        Text(
            text = buildAnnotatedString {
                append("")

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(startTime.format(DateTimeFormatter.ofPattern("hh:mm a")))
                }

                append(" to ")

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(endTime.format(DateTimeFormatter.ofPattern("hh:mm a")))
                }

                append(" | ")

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(duration.toString())
                }

                append(" mins ")
            },
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.3f),
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}