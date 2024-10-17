package com.app.routineturboa.data.repository

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.app.routineturboa.data.onedrive.downloadFromOneDrive
import com.app.routineturboa.data.room.AppDao
import com.app.routineturboa.data.room.TaskCompletionEntity
import com.app.routineturboa.data.room.TaskEntity
import com.app.routineturboa.data.room.TaskCompletionHistory
import com.app.routineturboa.utils.TaskTypes
import com.app.routineturboa.utils.TimeUtils.isoStrToDateTime
import com.app.routineturboa.utils.TimeUtils.strToDateTime
import com.microsoft.identity.client.IAuthenticationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val appDao: AppDao
) {
    private val tag = "AppRepository"

    val tasks: Flow<List<TaskEntity>> = appDao.getAllTasks()

    fun getTasksForDate(date: LocalDate): Flow<List<TaskEntity>> {
        Log.d(tag, "Fetching tasks for date: $date")
        return appDao.getTasksByDate(date)
    }


    fun getAllTasksInTaskDates(): Flow<List<TaskEntity>> {
        return appDao.allTasksInTaskDatesEntity()
    }

    private suspend fun isTaskLast(task: TaskEntity): Boolean {
        val lastTask = getLastTask().firstOrNull()
        return lastTask?.id == task.id
    }

    private fun getLastTask(): Flow<TaskEntity?> = flow {
        appDao.getTaskWithMaxPosition()
    }

    suspend fun getTaskName(taskId: Int): String? {
        return appDao.getTaskName(taskId)
    }

    // Begin new task operations
    suspend fun beginNewTaskOperations(
        clickedTask: TaskEntity,
        newTask: TaskEntity
    ): Result<Boolean> {
        Log.d(tag, "Starting operations for new task...")

        return try {
            runAsTransaction {

                val taskBelowToBeShifted = getNextTask(clickedTask)
                    ?: return@runAsTransaction Result.failure(Exception("Task below clicked task not found"))

                val taskBelowToBeShiftedDuration = taskBelowToBeShifted.duration

                if (newTask.duration >= taskBelowToBeShiftedDuration) {
                    return@runAsTransaction Result.failure(Exception("Invalid new task duration"))
                }

                incrementTasksPositionBelow(clickedTask)

//                insertTaskWithDate(newTask)

                val updatedDuration = taskBelowToBeShiftedDuration - newTask.duration
                val updatedStartTime = newTask.endTime

                val taskBelowNowShifted = taskBelowToBeShifted.copy()

                if (!isTaskLast(taskBelowNowShifted)) {
                    val taskBelowNowShiftedUpdated = taskBelowToBeShifted.copy(
                        duration = updatedDuration,
                        startTime = updatedStartTime,
                        position = newTask.position + 1
                    )
                    updateTaskAndAdjustNext(taskBelowNowShiftedUpdated)
                } else {
                    val taskBelowNowShiftedUpdated = taskBelowToBeShifted.copy(
                        duration = updatedDuration,
                        startTime = updatedStartTime
                    )
                    updateTaskAndAdjustNext(taskBelowNowShiftedUpdated)
                }

                Result.success(true)
            }
        } catch (e: Exception) {
            Log.e(tag, "Error in beginNewTaskOperations", e)
            Result.failure(e)
        }
    }

    // Increment tasks below reference task
    private suspend fun incrementTasksPositionBelow(referenceTask: TaskEntity?) {
        if (referenceTask == null) return

        try {
            // Check if there are tasks below the reference task and increment their positions
            appDao.incrementPositionsBelow(referenceTask.position)
        } catch (e: Exception) {
            Log.e(tag,
                "Error incrementing task positions below reference task", e)
        }
    }

    // Update task and adjust next
    suspend fun updateTaskAndAdjustNext(initialEditedTask: TaskEntity) {
        try {
            val taskBelow = getNextTask(initialEditedTask)
            if (taskBelow != null) {
                val startTimeTaskBelow = initialEditedTask.endTime
                val durationTaskBelow = Duration.between(startTimeTaskBelow, taskBelow.endTime)
                val durationTaskBelowInt = durationTaskBelow.toMinutes().toInt()


                val taskWithUpdatesForBelow = taskBelow.copy(
                    duration = durationTaskBelowInt,
                    startTime = startTimeTaskBelow
                )

                // Now update the task along with the necessary updates for task below
                runAsTransaction {
                    updateTask(initialEditedTask)
                    updateTask(taskWithUpdatesForBelow)
                }

            } else {
                Log.d(tag, "No task below found, updating task only")
                runAsTransaction {
                    updateTask(initialEditedTask)
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating task and adjusting next", e)
        }
    }

    // Fetch next task
    private suspend fun getNextTask(baseTask: TaskEntity): TaskEntity? {
        if (isTaskLast(baseTask)) return null

        // Collect the first emitted value of the Flow
        val currentTasks = appDao.getAllTasks().firstOrNull() ?: return null

        val baseTaskIndex: Int = currentTasks.indexOfFirst { it.id == baseTask.id }

        return if (baseTaskIndex != -1 && baseTaskIndex < currentTasks.size - 1) {
            currentTasks[baseTaskIndex + 1]
        } else {
            null
        }
    }

    suspend fun syncTasksFromOneDrive(authResult: IAuthenticationResult, context: Context){
        downloadFromOneDrive(authResult, context)

        val onedriveDestination = context.getDatabasePath("RoutineTurbo_PyQt6.db")
        val oneDriveDb = openDbFile(onedriveDestination.absolutePath)

        // Fetch tasks from the OneDrive DB
        val tasksFromOneDrive = fetchTasksFromOneDriveDb(oneDriveDb)

        // Insert tasks into the local Room database
        tasksFromOneDrive.forEach { task ->
            coroutineScope {
                Log.d(tag, "#TODO: Insert task from OneDrive into Room")
            }
        }

        // Close the OneDrive DB connection
        oneDriveDb.close()
    }

    private fun openDbFile(oneDriveDbFilePath: String): SQLiteDatabase {
        return SQLiteDatabase.openDatabase(
            oneDriveDbFilePath,
            null,
            SQLiteDatabase.OPEN_READONLY
        )
    }

    private fun fetchTasksFromOneDriveDb(db: SQLiteDatabase): List<TaskEntity> {
        val tasks = mutableListOf<TaskEntity>()

        val cursor = db.rawQuery("SELECT * FROM tasks_table", null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val notes = cursor.getString(cursor.getColumnIndexOrThrow("notes"))
            val duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"))
            val startTime = cursor.getString(cursor.getColumnIndexOrThrow("startTime"))
            val endTime = cursor.getString(cursor.getColumnIndexOrThrow("endTime"))
            val reminder = cursor.getString(cursor.getColumnIndexOrThrow("reminder"))
            val type = cursor.getString(cursor.getColumnIndexOrThrow("type"))
            val position = cursor.getInt(cursor.getColumnIndexOrThrow("position"))

            val task = TaskEntity(
                id = id,
                name = name,
                notes = notes,
                duration = duration,
                startTime = isoStrToDateTime(startTime),
                endTime = isoStrToDateTime(endTime),
                reminder = isoStrToDateTime(reminder),
                type = type,
                position = position
            )
            tasks.add(task)
        }
        cursor.close()

        return tasks
    }

    fun getTasksByType(type: String): Flow<List<TaskEntity>> {
        return appDao.getTasksByType(type)
    }

    suspend fun insertTaskWithDate(taskEntity: TaskEntity, taskDate: LocalDate): Long {
        return appDao.safeInsertTaskWithDate(taskEntity, taskDate)
    }


    private suspend fun updateTask(task: TaskEntity) {
        Log.d(tag, "Updating task: ${task.name} (id:${task.id})")
        appDao.updateTask(task)
    }

    private suspend fun <T> runAsTransaction(block: suspend () -> T): T {
        return withContext(Dispatchers.IO) {
            appDao.runTaskTransaction {
                block()
            }
            block()
        }
    }

    private suspend fun insertDefaultTasks(defaultTask: TaskEntity, selectedDate: LocalDate): Long {
        Log.d(tag, "Inserting default task.")
        return appDao.safeInsertTaskWithDate(defaultTask, selectedDate)
    }

    suspend fun deleteTask(task: TaskEntity) = appDao.deleteTask(task)

    suspend fun getTaskById(taskId: Int): TaskEntity? = appDao.getTaskById(taskId)

    suspend fun deleteAllTasks() {
        appDao.deleteAllTasks()
    }

    suspend fun initializeDefaultTasks(selectedDate: LocalDate) {
        val tasks = appDao.getAllTasks().first()

        if (tasks.isEmpty()) {
            val firstTask = TaskEntity(
                position = 1,
                name = "Start of Day",
                notes = "",
                duration = 359,
                startTime = strToDateTime("00:01 AM"),
                endTime = strToDateTime("06:00 AM"),
                reminder = strToDateTime("06:00 AM"),
                type = TaskTypes.BASICS
            )

            insertDefaultTasks(firstTask, selectedDate)

            val lastTask = TaskEntity(
                position = Int.MAX_VALUE,
                name = "End of Day",
                notes = "",
                duration = 1079,
                startTime = strToDateTime("06:00 AM"),
                endTime = strToDateTime("11:59 PM"),
                reminder = strToDateTime("06:00 AM"),
                type = TaskTypes.BASICS
            )

            insertDefaultTasks(lastTask, selectedDate)
        }
    }

    suspend fun markTaskAsCompleted(taskId: Int) {
        val today = LocalDate.now()

        // Create a new TaskCompletionEntity to mark the task as completed for today
        val taskCompletion = TaskCompletionEntity(
            taskId = taskId,
            date = today,
            isCompleted = true
        )

        // Insert the task completion into the database
        appDao.insertTaskCompletion(taskCompletion)
    }

    suspend fun upsertTaskCompletion(taskCompletion: TaskCompletionEntity) {
        appDao.upsertTaskCompletion(taskCompletion)
    }

    suspend fun getTaskCompletion(taskId: Int, date: LocalDate): TaskCompletionEntity? {
        return appDao.getTaskCompletion(taskId, date)
    }

    suspend fun getTasksWithCompletionStatus(): List<TaskCompletionHistory> {
        return appDao.getTasksWithCompletionStatus()
    }

    suspend fun getAllTaskCompletions(): List<TaskCompletionEntity> {
        return appDao.getAllTaskCompletions()
    }

    suspend fun getTaskCompletionsByTaskId(taskId: Int): List<TaskCompletionEntity> {
        return appDao.getTaskCompletionsByTaskId(taskId)
    }


}
