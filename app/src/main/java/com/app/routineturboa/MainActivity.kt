package com.app.routineturboa

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.routineturboa.services.MSALAuthManager
import com.app.routineturboa.ui.MainScreen
import com.app.routineturboa.ui.theme.RoutineTurboATheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    private lateinit var msalAuthManager: MSALAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")

        msalAuthManager = MSALAuthManager.getInstance(this)
        Log.d("MainActivity", "MSALAuthManager initialized")

        setContent {
            RoutineTurboATheme {
                Surface {
                    Column{
                        Greeting()
                        Spacer(modifier = Modifier.height(2.dp)) // Optional spacer
                        MainScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting() {
    val currentDate = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date())
    Log.d("MainActivity", "Current date formatted: $currentDate")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = currentDate, style = MaterialTheme.typography.headlineLarge)
    }
}
