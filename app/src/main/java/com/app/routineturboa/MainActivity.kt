package com.app.routineturboa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.app.routineturboa.data.local.DatabaseHelper
import com.app.routineturboa.ui.MainScreen
import com.app.routineturboa.ui.theme.RoutineTurboATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize DatabaseHelper to ensure database is copied and set up
        DatabaseHelper(this).readableDatabase

        setContent {
            RoutineTurboATheme {
                Surface{
                    Column {
                        Greeting()
                        MainScreen()
                    }
                }
            }

        }
    }
}

@Composable
fun Greeting(){
    Text(text = "Routine", style = MaterialTheme.typography.headlineLarge)
}
