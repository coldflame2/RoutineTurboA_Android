package com.app.routineturboa.ui.reusable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.routineturboa.ui.theme.LocalCustomColors

@Composable
fun TaskTypeLetterIcon(
    taskTypeFirstLetter: Char,
    isThisTaskClicked: Boolean,
    iconBgColor: Color,
    taskPosition: Int? = null,
    topPadding: Dp,
) {

    val customColors = LocalCustomColors.current
    val iconBoxColor = if (isThisTaskClicked) {
        MaterialTheme.colorScheme.tertiary.copy(alpha=0.6f)
    } else {
        MaterialTheme.colorScheme.tertiary.copy(alpha=0.4f)
    }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(top = topPadding-5.dp, start = 5.dp)
                .size(20.dp)
                .background(color = iconBoxColor, shape = CircleShape)

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