package com.app.routineturboa.ui

import TaskViewModelFactory
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.routineturboa.data.local.RoutineRepository
import com.app.routineturboa.viewmodel.TaskViewModel

@Composable
fun MainScreen(taskViewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(RoutineRepository(LocalContext.current)))){
    val listItems = (1..100).map { "Item $it" } // Generate 100 random items
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(listItems) { item ->
                Text(text = item, modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
    Log.d("MainScreen", "MainScreen called")
}


//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            EmptyActivityTheme {
//                val items = (1..100).map { "Item $it" } // Generate 100 random items
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    LazyColumn(modifier = Modifier.padding(innerPadding)) {
//                        items(items) { item ->
//                            Text(text = item, modifier = Modifier.padding(vertical = 8.dp))
//                        }
//                    }
//                }
//            }
//        }
//    }
//}