package com.app.routineturboa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.app.routineturboa.services.MSALAuthManager
import com.app.routineturboa.ui.MainScreen
import com.app.routineturboa.ui.theme.RoutineTurboATheme

class MainActivity : ComponentActivity() {
    private lateinit var msalAuthManager: MSALAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        msalAuthManager = MSALAuthManager.getInstance(this)

        setContent {
            RoutineTurboATheme {
                Surface {
                    Column(
                        modifier = Modifier.statusBarsPadding()
                    ) {
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
