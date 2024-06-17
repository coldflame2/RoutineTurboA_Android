com.app.routineturboa

├── MainActivity.kt
│
├── data
│   ├── local
│   │   ├── com.app.routineturboa.data.local.DatabaseHelper.kt
│   │   ├── RoutineRepository.kt
│   └── model
│       ├── Task.kt
│
├── ui
│   ├── MainScreen.kt 
│   ├── components
│   │   ├── Greeting.kt
│   │   ├── TaskItem.kt
│
├── viewmodel
│   ├── TaskViewModel.kt
│   ├── TaskViewModelFactory.kt
│
├── util
│   ├── TimeUtils.kt
│   ├── copyDatabase.kt



Explanation of Structure
•	MainScreen.kt: Main screen composable.

•	data
    •	local
        •	com.app.routineturboa.data.local.DatabaseHelper.kt: Contains the database helper class.
        •	RoutineRepository.kt: Repository for data operations.
    •	model
        •	Task.kt: Data model representing a task.

•	ui
    •	MainActivity.kt: Main activity of the application.
    •	components
        •	Greeting.kt: Greeting composable function.
        •	TskItem.kt: Task item composable function.

•	viewmodel
    •	TaskViewModel.kt: ViewModel for managing UI-related data.
    •	TaskViewModelFactory.kt: Factory for creating ViewModel instances.

•	util
    •	TimeUtils.kt: Utility functions, such as time conversion.

