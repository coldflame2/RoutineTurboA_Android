package com.app.routineturboa.ui

import TaskViewModelFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.routineturboa.data.local.RoutineRepository
import com.app.routineturboa.ui.components.TaskItem
import com.app.routineturboa.viewmodel.TaskViewModel

@Composable
fun MainScreen(taskViewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(
    RoutineRepository(LocalContext.current)
)
)) {
    val tasks by taskViewModel.tasks.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFBBDEFB),
                            Color(0xFFE3F2FD),
                            Color(0xFFFFFFFF)
                        )
                    )
                )
        ) {
            LazyColumn(
                contentPadding = PaddingValues(5.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks) { task ->
                    TaskItem(task)
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}
