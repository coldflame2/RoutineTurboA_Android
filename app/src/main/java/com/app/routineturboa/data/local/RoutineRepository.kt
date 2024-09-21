package com.app.routineturboa.data.local

import android.content.Context
import android.util.Log
import com.app.routineturboa.R

import androidx.room.withTransaction
import com.app.routineturboa.utils.TimeUtils.strToDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext


class RoutineRepository(val context: Context) {
    val tag = "RoutineRepository"
    private var routineDatabase = RoutineDatabase.getDatabase(context)
    private var taskDao: TaskDao = routineDatabase.taskDao()

    fun getAllTasks(): Flow<List<TaskEntity>>{
        Log.d(tag, "Getting all tasks.")
        return taskDao.getAllTasks()
    }

    fun getTasksByType(type: String): Flow<List<TaskEntity>> {
        return taskDao.getTasksByType(type)
    }

    suspend fun insertTask(task: TaskEntity): Long {
        Log.d(tag, "Inserting task '${task.name}' [id: ${task.id}")
        val result = taskDao.safeInsertTask(task)

        if (result == -1L) { // -1 means failure
            Log.i("TaskRepository",
                "Task insertion failed due to either unique constraint or other factors.")
        }
        // 'result' if successful is new rowID
        return result
    }

    suspend fun updateTask(task: TaskEntity) {
        Log.d(tag, "Updating task: ${task.name} and id:${task.id}")
        taskDao.updateTask(task)
    }

    suspend fun <T> runAsTransaction(block: suspend () -> T): T {
        return withContext(Dispatchers.IO) {
            routineDatabase.withTransaction {
                block()
            }
        }
    }

    private suspend fun insertDefaultTasks(defaultTask: TaskEntity): Long {
        Log.d(tag, "Inserting default task.")
        return taskDao.insertTask(defaultTask)
    }

    suspend fun updateTasksWithNewPositions(tasks: List<TaskEntity>) {
        taskDao.updateTasksWithNewPositions(tasks)
    }

    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)

    suspend fun getTaskById(taskId: Int): TaskEntity? = taskDao.getTaskById(taskId)

    fun closeDatabase() {
        routineDatabase.close()
    }

    fun reopenDatabase() {
        routineDatabase = RoutineDatabase.getDatabase(context)
        taskDao = routineDatabase.taskDao()
    }

    suspend fun deleteAllTasks() {
        taskDao.deleteAllTasks()
    }

    suspend fun initializeDefaultTasks() {
        val tasks = taskDao.getAllTasks().first()

        if (tasks.isEmpty()) {
            val firstTask = TaskEntity(
                position = 1,
                name = "Start of Day",
                notes = "",
                duration = 359,
                startTime = strToDateTime("00:01 AM"),
                endTime = strToDateTime("06:00 AM"),
                reminder = strToDateTime("06:00 AM"),
                type = context.getString(R.string.task_type_default)
            )

            insertDefaultTasks(firstTask)

            val lastTask = TaskEntity(
                position = Int.MAX_VALUE,
                name = "End of Day",
                notes = "",
                duration = 1079,
                startTime = strToDateTime("06:00 AM"),
                endTime = strToDateTime("11:59 PM"),
                reminder = strToDateTime("06:00 AM"),
                type = context.getString(R.string.task_type_default)
            )

            insertDefaultTasks(lastTask)
        }
    }
}
