package com.app.routineturboa.data.repository

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.app.routineturboa.data.onedrive.downloadFromOneDrive
import com.app.routineturboa.data.room.AppDao
import com.app.routineturboa.data.room.TaskCompletionEntity
import com.app.routineturboa.data.room.TaskEntity
import com.app.routineturboa.data.room.TaskCompletionHistory
import com.app.routineturboa.data.room.NonRecurringTaskEntity
import com.app.routineturboa.utils.Converters.stringToTime
import com.microsoft.identity.client.IAuthenticationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val appDao: AppDao
) {
    private val tag = "AppRepository"

    // Flow to observe all tasks
    val allTasks: Flow<List<TaskEntity>> = appDao.getAllTasks()

    // region:-------------------- Retrieval of Tasks by date  -------------------

     // Get tasks filtered by the selected date
     fun getTasksForDate(date: LocalDate): Flow<List<TaskEntity>> {
         return allTasks
             .onEach { taskList ->
                 Log.d(tag, "All tasks emitted: ${taskList.size} tasks")
             }
             .map { taskList ->
                 taskList.filter { task ->
                     val isOnDate = isTaskOnThisDate(date, task)
                     isOnDate
                 }
             }
             .onEach { filteredTasks ->
                 Log.d(tag, "Filtered tasks for date $date: ${filteredTasks.size} tasks")
             }
     }

    // Check if a task occurs on a given date based on recurrence or specific date
    private suspend fun isTaskOnThisDate(date: LocalDate, taskEntity: TaskEntity): Boolean {
        // Handle non-recurring tasks by checking the TaskDatesEntity
        if (taskEntity.isRecurring == false) {
            Log.d(tag, "Task is not recurring, checking TaskDatesEntity")
            return isNonRecurringTaskOnThisDate(taskEntity.id, date)
        }

        // Handle recurring tasks
        val startDate = getStartDate(taskEntity)
        val endDate = taskEntity.recurrenceEndDate ?: date.plusYears(100)

        // Ensure the selected date falls within the recurrence range
        if (date.isBefore(startDate) || date.isAfter(endDate)) {
            return false
        }

        val recurrenceInterval = taskEntity.recurrenceInterval ?: 1
        val periodBetween = when (taskEntity.recurrenceType) {
            "DAILY" -> ChronoUnit.DAYS.between(startDate, date)
            "WEEKLY" -> ChronoUnit.WEEKS.between(startDate, date)
            "MONTHLY" -> ChronoUnit.MONTHS.between(startDate, date)
            else -> return false
        }

        // Check if the task occurs on the given date based on the recurrence interval
        return periodBetween % recurrenceInterval == 0L
    }

     // Get the start date of the task.
    private fun getStartDate(task: TaskEntity): LocalDate {
        return task.startDate ?: LocalDate.now()
    }

    // Check if a non-recurring task exists for the given date in TaskDatesEntity.
    private suspend fun isNonRecurringTaskOnThisDate(taskId: Int, date: LocalDate): Boolean {
        Log.d(tag, "Checking non-recurring. taskID: $taskId. Date: $date")

        val doesTaskExist = appDao.isNonRecurringTaskOnThisDate(taskId, date)
        Log.d(tag, "this task exists on this date? $doesTaskExist...")

        return doesTaskExist
    }

    // endregion


    // region: ----------------- Database Operations (Add, update, Delete) --------------- //

    suspend fun beginNewTaskOperations(
        clickedTask: TaskEntity, newTask: TaskEntity
    ): Result<TaskOperationResult> {
        Log.d(tag, "Starting the transaction for new Task operations in Repository...")

        return try {
            runAsTransaction {
                // log every original detail, clickedTask: ID and name and position. New task: name and position.
                // Task below: name and ID and position.
                Log.d(tag, "Original clickedTask: ID=${clickedTask.id}, Name=${clickedTask.name}, Position=${clickedTask.position}")
                Log.d(tag, "Original newTask: Name=${newTask.name}, Position=${newTask.position}")

                val positionOfTaskBelowClickedTask = clickedTask.position?.plus(1)
                val taskBelowClickedTask = positionOfTaskBelowClickedTask?.let { getTaskAtPosition(it) }

                Log.d(tag, "Original task Below Clicked Task: Name: ${taskBelowClickedTask?.name}, ID: ${taskBelowClickedTask?.id}, Position: ${taskBelowClickedTask?.position}")

                // Step 1: Insert the new task
                Log.d(tag, "Inserting the new task with position ${newTask.position}")
                val newTaskId = appDao.safeInsertTask(newTask)
                Log.d(tag, "Inserted new task with ID $newTaskId")

                // Step 2: Update shifted task (initially below clickedTask, now below newTask)
                Log.d(tag, "Updating the task below the new task")
                if (taskBelowClickedTask != null) {
                    updateShiftedTaskAfterNewTask(taskBelowClickedTask, newTask)
                }


                // Step 4: Increment positions of tasks starting from position after the updated task
                if (positionOfTaskBelowClickedTask != null) {
                    Log.d(tag, "Incrementing positions of tasks starting from position $positionOfTaskBelowClickedTask")
                    incrementTasksPositionBelow(positionOfTaskBelowClickedTask)
                    Log.d(tag, "Incremented positions of tasks below position $positionOfTaskBelowClickedTask")
                }

                Log.d(tag, "Transaction Complete: Committing transaction")

                // Return the custom data class wrapped in Result
                Result.success(
                    TaskOperationResult(success = true, newTaskId = newTaskId)
                )
            }
        } catch (e: Exception) {
            Log.e(tag, "Error in beginNewTaskOperations", e)
            Result.failure(e)
        }
    }

    // Increment the position of all tasks below the reference task.
    private suspend fun incrementTasksPositionBelow(startingPosition: Int?) {
        try {
            Log.d(tag, "Calling Dao's incrementPositionBelow method.")
            if (startingPosition != null) {
                appDao.incrementPositionsBelowWithLogging(startingPosition)
            }
        } catch (e: Exception) {
            Log.e(tag, "Error incrementing task positions below reference task", e)
        }
    }

    // Update the taskBelow duration and update its startTime with newTask's endTime and
    private suspend fun updateShiftedTaskAfterNewTask(taskBelow: TaskEntity, newTask: TaskEntity) {
        Log.d(tag, "Updating the Shifted Task position, startTime, and duration.")

        // Update startTime of taskBelow to be the endTime of the new task
        val updatedStartTime = newTask.endTime

        // Calculate new duration for taskBelow
        val originalDuration = taskBelow.duration
        val newDuration = originalDuration?.let { duration ->
            val durationDifference = updatedStartTime?.let {
                taskBelow.endTime?.until(it, ChronoUnit.MINUTES)
            }
            durationDifference?.let { duration - it }
        }
            ?.toInt()

        if (newDuration == null || newDuration <= 0) {
            Log.e(tag, "Invalid duration calculated for task below.")
            throw Exception("Invalid duration calculated for task below.")
        }

        // Update the taskBelow entity without changing its position
        val updatedTaskBelow = taskBelow.copy(
            startTime = updatedStartTime,
            duration = newDuration,
            endTime = taskBelow.endTime // EndTime remains the same unless recalculated
        )

        Log.d(tag, "Updated taskBelow: startTime=${updatedTaskBelow.startTime}, duration=${updatedTaskBelow.duration}, position=${updatedTaskBelow.position}")

        // Update taskBelow in the database
        appDao.updateTask(updatedTaskBelow)
    }

    // Update a task and adjust the next task.
    suspend fun onEditUpdateTaskCurrentAndBelow(taskToEdit: TaskEntity) {
        try {
            // If the current task is the last one, update only taskToEdit
            if (isTaskLast(taskToEdit)) {
                Log.d(tag, "Current task is the last one, updating the task only.")
                updateTask(taskToEdit)
                return
            }

            // Get the task below the current task based on position
            val taskBelow = taskToEdit.position?.let { getNextTask(it) }

            // If taskBelow is null, update only taskToEdit
            if (taskBelow == null) {
                Log.d(tag, "No task below found, updating the task only.")
                updateTask(taskToEdit)
            } else {
                // Calculate the duration between taskToEdit's end time and taskBelow end time
                val durationBetween = Duration.between(taskToEdit.endTime, taskBelow.endTime).toMinutes().toInt()
                val updatedTaskBelow = taskBelow.copy(duration = durationBetween, startTime = taskToEdit.endTime)

                // Update both tasks within a transaction
                runAsTransaction {
                    updateTask(taskToEdit)
                    updateTask(updatedTaskBelow)
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating task and adjusting next", e)
        }
    }

    suspend fun resetWithDemoOrDefaultTasks(tasks: List<TaskEntity>): Result<Boolean> {
        return try {
            // Run the following as a single transaction
            runAsTransaction {
                // Step 1: Delete existing tasks
                appDao.deleteAllTasks()

                // Step 2: Insert demo tasks
                tasks.forEach { task ->
                    val result = appDao.safeInsertTask(task)
                    if (result == -1L) {
                        throw Exception("Failed to insert task: ${task.name}")
                    }
                }
            }
            Result.success(true) // Return success if the transaction completes without exceptions
        } catch (e: Exception) {
            Log.e(tag, "Error inserting demo tasks in transaction: ${e.message}")
            Result.failure(e) // Return failure if an exception occurs
        }
    }

    // Insert task on Task dates
    private suspend fun insertInNonRecurringTaskEntity(taskId: Int, date: LocalDate) {
        val nonRecurringTaskEntity = NonRecurringTaskEntity(
            taskId = taskId,
            taskDate = date
        )

        appDao.insertInNonRecurringTaskEntity(nonRecurringTaskEntity)
    }

    // endregion


    // region:------------------------- Task Retrieval and filtering -------------------------

    // function to get the task at the specified position
    suspend fun getTaskAtPosition(position: Int): TaskEntity? {
        return appDao.getTaskAtPosition(position)
    }

    // Function to get the next task, also perform the `isTaskLast` check
    private suspend fun getNextTask(referenceTaskPosition: Int): TaskEntity? {
        val nextTask = appDao.getTaskAtPosition(referenceTaskPosition + 1)
        return nextTask
    }

    // Function to check if a task is the last in the list
    private suspend fun isTaskLast(task: TaskEntity): Boolean {
        // Use the DAO to check if there's any task with a position greater than the current task
        return task.position?.let { appDao.getTasksCountWithPositionGreaterThan(it) } == 0
    }

    suspend fun getTaskName(taskId: Int): String? {
        return appDao.getTaskName(taskId)
    }

    // endregion


    // region:------------------------- Sync Operations -------------------------

     // Sync tasks from OneDrive.
    suspend fun syncTasksFromOneDrive(authResult: IAuthenticationResult, context: Context) {
        downloadFromOneDrive(authResult, context)
        val dbPath = context.getDatabasePath("RoutineTurbo_PyQt6.db")
        val oneDriveDb = openDbFile(dbPath.absolutePath)

        // Fetch and insert tasks from OneDrive
        val tasksFromOneDrive = fetchTasksFromOneDriveDb(oneDriveDb)
        tasksFromOneDrive.forEach { task ->
            Log.d(tag, "Inserting OneDrive task into Room DB...")
            // TODO: Insert task into the local Room database
        }

        oneDriveDb.close()
    }

    private fun openDbFile(path: String): SQLiteDatabase {
        return SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY)
    }

    private fun fetchTasksFromOneDriveDb(db: SQLiteDatabase): List<TaskEntity> {
        val tasks = mutableListOf<TaskEntity>()
        val cursor = db.rawQuery("SELECT * FROM tasks_table", null)
        while (cursor.moveToNext()) {
            tasks.add(TaskEntity(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                notes = cursor.getString(cursor.getColumnIndexOrThrow("notes")),
                duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration")),
                startTime = stringToTime(cursor.getString(cursor.getColumnIndexOrThrow("startTime")))!!,
                endTime = stringToTime(cursor.getString(cursor.getColumnIndexOrThrow("endTime")))!!,
                reminder = stringToTime(cursor.getString(cursor.getColumnIndexOrThrow("reminder")))!!,
                type = cursor.getString(cursor.getColumnIndexOrThrow("type")),
                position = cursor.getInt(cursor.getColumnIndexOrThrow("position"))
            ))
        }
        cursor.close()
        return tasks
    }

    // endregion


    // region: ------------------------- Task Utilities -------------------------

    private suspend fun updateTask(task: TaskEntity) {
        Log.d(tag, "Updating task: ${task.name} (id:${task.id})")
        appDao.updateTask(task)
    }

     // Insert task in TaskEntity
    suspend fun insertTask(taskEntity: TaskEntity): Long {
        Log.d(tag, "Calling Dao's safeInsertTask now...")
        return appDao.safeInsertTask(taskEntity)
    }

     // Delete a task.
    suspend fun deleteTask(task: TaskEntity) {
        appDao.deleteTask(task)
    }

     // Delete all tasks.
    suspend fun deleteAllTasks() {
        appDao.deleteAllTasks()
    }

    // endregion



    // region:---------------------- Task Completion and Others ---------------------

     // Mark a task as completed for today.
    suspend fun markTaskAsCompleted(taskId: Int) {
        val today = LocalDate.now()
        val taskCompletion = TaskCompletionEntity(taskId = taskId, date = today, isCompleted = true)
        appDao.insertTaskCompletion(taskCompletion)
    }

     // Get tasks with their completion status.
    suspend fun getTasksWithCompletionStatus(): List<TaskCompletionHistory> {
        return appDao.getTasksWithCompletionStatus()
    }


    // ------------------------- Database Helpers -------------------------

    // Run a block of operations as a transaction.
    private suspend fun <T> runAsTransaction(block: suspend () -> T): T {
        return withContext(Dispatchers.IO) {
            var result: T? = null
            appDao.runTaskTransaction {
                result = block() // Execute the block and assign the result
            }
            result ?: throw IllegalStateException("Transaction block did not return a result")
        }
    }

    // endregion
}