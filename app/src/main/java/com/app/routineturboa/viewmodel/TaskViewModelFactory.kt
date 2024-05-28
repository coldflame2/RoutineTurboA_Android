import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.routineturboa.data.local.RoutineRepository
import com.app.routineturboa.viewmodel.TaskViewModel

class TaskViewModelFactory(private val repository: RoutineRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
