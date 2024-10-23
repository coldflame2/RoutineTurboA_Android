package com.app.routineturboa.ui.reusable

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SimpleToast(message: String) {
    val tag = "SimpleToast"
    Log.d(tag, "SimpleToast")
    var showMessage by remember { mutableStateOf(true) }

    if (showMessage) {
        LaunchedEffect(Unit) {
            // Automatically hide the message after 2 seconds
            kotlinx.coroutines.delay(2000)
            showMessage = false
        }

        // Display the message
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
