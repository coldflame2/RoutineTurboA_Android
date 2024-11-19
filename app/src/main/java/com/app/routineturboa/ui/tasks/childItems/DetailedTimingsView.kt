package com.app.routineturboa.ui.tasks.childItems

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@Composable
fun DetailedTimingsView (
    startTime: LocalTime?,
    endTime: LocalTime?,
    duration: Long,
) {
    Text(
        text = buildAnnotatedString {
            append("")

            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                append(startTime?.format(DateTimeFormatter.ofPattern("hh:mm a")) ?: "--")
            }

            append(" to ")

            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                append(endTime?.format(DateTimeFormatter.ofPattern("hh:mm a"))) ?: "--"
            }

            append(" | ")

            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                append(duration.toString())
            }

            append(" mins ")
        },
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.3f),
        modifier = Modifier.padding(start = 4.dp)
    )

}