package com.app.routineturboa.data.local

import android.content.Context
import android.util.Log
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

    fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()

    // Function to get tasks by type
    fun getTasksByType(type: String): Flow<List<TaskEntity>> {
        return taskDao.getTasksByType(type)
    }

    suspend fun insertTask(task: TaskEntity): Long {
        Log.d(tag, "Inserting task with ID: ${task.id}. Task Name: ${task.name}")

        // Ensure the first and last tasks are correctly identified
        val firstTask = taskDao.getFirstTask()
        val lastTask = taskDao.getLastTask()

        if (firstTask == null || lastTask == null) {
            Log.e(tag, "First or last task not found. Please ensure default tasks are initialized.")
            throw IllegalStateException("First or last task not found.")
        }

        // Insert the new task with its correct properties (id will be auto-generated)
        return taskDao.insertTask(task)
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

    suspend fun initializeDefaultTasks() {
        val tasks = taskDao.getAllTasks().first()

        if (tasks.isEmpty()) {
            val firstTask = TaskEntity(
                id = -1,
                position = 1,
                name = "Start of Day",
                notes = "",
                duration = 359,
                startTime = strToDateTime("00:01 AM"),
                endTime = strToDateTime("06:00 AM"),
                reminder = strToDateTime("06:00 AM"),
                type = "default"
            )

            insertDefaultTasks(firstTask)

            val lastTask = TaskEntity(
                id = -2,
                position = Int.MAX_VALUE,
                name = "End of Day",
                notes = "",
                duration = 1079,
                startTime = strToDateTime("06:00 AM"),
                endTime = strToDateTime("11:59 PM"),
                reminder = strToDateTime("06:00 AM"),
                type = "default",
            )

            insertDefaultTasks(lastTask)
        }
    }

    suspend fun getDemoTasks(context: Context): List<TaskEntity> {
        val tasksInitialList = taskDao.getAllTasks().first() // first() collects the first emission from the flow

        return if (tasksInitialList.isNotEmpty()) {
            getDemoTasks(context) // Return the demo tasks if the list is not empty
        } else {
            tasksInitialList // Return the initial tasks if the list
        }
    }

}
