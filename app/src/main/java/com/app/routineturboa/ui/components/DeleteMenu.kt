
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.routineturboa.data.model.Task

@Composable
fun DeleteTaskMenu(
    selectedTaskForDisplay: Task?,
    tasks: List<Task>,
    onTaskDelete: (Task) -> Unit
) {
    Log.d("DeleteTaskMenu", "Displaying delete menu for task: ${selectedTaskForDisplay?.taskName}")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Delete Task: ${selectedTaskForDisplay?.taskName}",
                color = Color.Red
            )
            Button(
                onClick = {
                    selectedTaskForDisplay?.let { task ->
                        onTaskDelete(task)
                    }
                }
            ) {
                Text("Delete")
            }
            Button(
                onClick = {
                    // Cancel or dismiss the delete menu
                }
            ) {
                Text("Cancel")
            }
        }
    }
}
