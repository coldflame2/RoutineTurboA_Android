package com.app.routineturbo_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize DatabaseHelper to ensure database is copied and set up
        DatabaseHelper(this).readableDatabase

        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    Column {
                        Greeting("Welcome to Routine Turbo!")
                        MainScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(message: String) {
    Text(text = message, style = MaterialTheme.typography.bodyLarge)
}
