package com.app.routineturboa

import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import com.app.routineturboa.services.MSALAuthManager
import com.app.routineturboa.ui.MainScreen
import com.app.routineturboa.ui.theme.RoutineTurboATheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    private lateinit var msalAuthManager: MSALAuthManager
    private val currentDate: String = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date())


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        requestWindowFeature(Window.FEATURE_CONTEXT_MENU)

        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")

        msalAuthManager = MSALAuthManager.getInstance(this)
        Log.d("MainActivity", "MSALAuthManager initialized")



        setContent {
            RoutineTurboATheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            ),

                            title = {Text(text = "OKay")},

                            navigationIcon = {Text("...")},
                            actions = {Text("...")}
                        )
                    },

                    content = { innerPadding ->
                        Column(modifier = Modifier.padding(innerPadding)) {
                            MainScreen()
                        }
                    }
                )
            }
        }
    }
}
