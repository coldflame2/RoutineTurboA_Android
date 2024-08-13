package com.app.routineturboa.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun AlphabetIcon(
    letter: Char,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = Color.White,
    size: TextUnit = 10.sp
) {
    val boxSize = with(LocalDensity.current) { size.toDp() * 2 }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(boxSize)
            .background(backgroundColor, shape = CircleShape)
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
