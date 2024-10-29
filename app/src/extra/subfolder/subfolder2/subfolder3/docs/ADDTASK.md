# Step 3
- In MainScreen, this function taskOperationEvents.onNewTaskConfirmClick is linked to the function in TasksViewModel

# step 1
- TasksLazyColumn passes the function to AddTaskDialog:
    
- onNewTaskConfirmClick = { newTaskFormData, clickedTask ->
        taskOperationEvents.onNewTaskConfirmClick(newTaskFormData, clickedTask)
  }

# Step 2
- AddTaskDialog collects input data in TaskFormData and then passes it down to TasksLazyColumn:
    
- onNewTaskConfirmClick(newTaskFormData, clickedTask)

# Step 4: 
- AddTaskDialog passes to TasksLazyColumn, which calls the function with the arguments. 

- MainScreen maps it to function on ViewModel, which starts the process of adding a new task.

# Step 5:
- In ViewModel:
  - onNewTaskConfirmClick is called with NewTaskFormData and clickedTask. 
  - Then beginNewTaskOperations is called. 
  - clickedTask is retrieved again from TasksUiState to confirm it's the same as the one passed from AddTaskDialog.
  - NewTaskFormData is converted to TaskEntity
  - Finally, repository method repository.beginNewTaskOperations is called with clickedTask and newTask

# Step 6:
- In Repository:
  - Get Task below
  - Increment positions of all tasks below it
  - Update task below startTime and duration
  - inset the new task
  - if new task is not recurring, insert it in TaskDatesEntity Too.

# Detailed in Repository and Dao:
 - Get taskBelow
   - check if _isTaskLast_
   - if not, get _taskBelow_
   - Call _incrementTasksPositionBelow(taskBelow)_
   - Call _onAddNewUpdateNext(taskBelow, newTask)_ to update the task below
   - insert new task and save the generated id _[val newTaskId = insertTask(newTask)]_
   - if new task _insertTaskWithDate_ with the generated Id and new task startDate


    
    
