package com.app.routineturboa.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun SmoothCircularProgressIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "Loading Infinite Transition")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    CircularProgressIndicator(
        progress = { angle / 360f },
        modifier = modifier
            .height(30.dp)
            .width(30.dp),
        color = MaterialTheme.colorScheme.primary,
        strokeWidth = 2.dp,
        trackColor = Color.LightGray,
        strokeCap = StrokeCap.Round
    )
}

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

@Composable
fun EmptyTasks() {
    Box(
        modifier = Modifier
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
                    .padding(end = 10.dp)
                    .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            ) {
                Text(
                    text = "...",
                    color = Color.Gray.copy(alpha = 0.7f)
                )
                Text(
                    text = "",
                    color = Color.Gray.copy(alpha = 0.7f)
                )
            }

            // Main Task Card With Details
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    Text(
                        text = "...",
                        color = Color.Gray.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "...",
                        color = Color.Gray.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "...",
                        color = Color.Gray.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}




//Box () {
//    Box () {
//        DottedLine ()
//    }
//
//    Row () {
//        Column () {
//            Text ()
//            Text ()
//        }
//
//        Card () {
//            Column () {
//                Row () {
//                    Text ()
//
//                    IconButton () {
//                        Icon ()
//                    }
//                    Spacer ()
//
//                    IconButton () {
//                        Icon ()
//                    }
//                }
//
//                Spacer ()
//
//                Row () {
//                    Icon ()
//
//                    Text ()
//                }
//
//                Spacer ()
//
//                Row () {
//                    Icon ()
//                    Text ()
//                }
//            }
//        }
//    }
//}
//
//DropdownMenu () {
//    DropdownMenuItem ()
//    DropdownMenuItem ()
//}
//
//AlertDialog () {
//    Text ()
//    Text ()
//    Button () {
//        Text ()
//    }
//}