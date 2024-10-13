
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.routineturboa.data.repository.AppRepository
import com.app.routineturboa.viewmodel.TasksViewModel

class TaskViewModelFactory(
    private val repository: AppRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TasksViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
