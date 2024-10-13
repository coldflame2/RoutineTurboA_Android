package com.app.routineturboa.ui.reusable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AlphabetIcon(
    letter: Char,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = Color.Black,
    size: TextUnit = 10.sp
) {
    val boxSize = with(LocalDensity.current) { size.toDp() * 2 }
    Spacer(modifier = Modifier.width(5.dp))
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(boxSize)
            .border(1.dp,
                MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f),
                CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color.Blue.copy(alpha = 0.1f),
                        backgroundColor.copy(alpha = 1f)
                    )
                ),
                shape = CircleShape
            )
    ) {
        Text(
            text = letter.toString(),
            textAlign = TextAlign.Center,
            color = textColor,
            fontSize = size, // Use the TextUnit directly for fontSize
            style = TextStyle(baselineShift = BaselineShift(0.1f))
        )
    }
}
