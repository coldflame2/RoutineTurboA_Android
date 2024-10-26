package com.app.routineturboa.ui.reusable.others

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.routineturboa.ui.reusable.animation.SmoothCircularProgressIndicator

@Composable
fun EmptyTaskCardPlaceholder (modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        // Main TaskCard and Start-End times
        Row {
            // Start-End times
            Column(
                modifier = Modifier
                    .height(110.dp)
                    .width(75.dp)
            ) {
                Text(
                    text = "..."
                )
                Text(
                    text = ""
                )
            }


            // Main Task Card With Details
            Card(
                modifier = modifier
                    .height(110.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(),
                shape = RoundedCornerShape(15.dp),
            ) {

                // Main content of the card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Task Name, Duration, Reminder
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        SmoothCircularProgressIndicator()
                    }
                }
            }
        }
    }
}




