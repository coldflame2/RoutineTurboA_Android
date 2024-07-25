package com.app.routineturboa.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TestingComposable() {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .height(100.dp)
    ) {
        Text(text = "Hello, Jetpack Compose!",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(bottom = 16.dp) // Adds padding below the text
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .align(Alignment.Center) // Aligns the Row in the center of the Box
                .fillMaxWidth() // Makes the Row take up the full width of the Box
        ) {
            Icon(imageVector = Icons.Default.Home, contentDescription = "Home Icon")
            Icon(imageVector = Icons.Default.Favorite, contentDescription = "Favorite Icon")
            Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings Icon")
        }
    }
}