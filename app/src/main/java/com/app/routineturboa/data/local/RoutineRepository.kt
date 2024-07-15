package com.app.routineturboa.data.local

import android.content.Context
import com.app.routineturboa.data.model.TaskEntity
import com.app.routineturboa.utils.TimeUtils.strToDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class RoutineRepository(private val context: Context) {
    private var routineDatabase = RoutineDatabase.getDatabase(context)
    private var taskDao: TaskDao = routineDatabase.taskDao()

    fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()

    suspend fun insertTask(task: TaskEntity): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)

    suspend fun getTaskById(taskId: Int): TaskEntity? = taskDao.getTaskById(taskId)

    fun closeDatabase() {
        routineDatabase.close()
    }

    fun reopenDatabase() {
        routineDatabase = RoutineDatabase.getDatabase(context)
        taskDao = routineDatabase.taskDao()
    }

    suspend fun isTaskFirst(task: TaskEntity): Boolean {
        val firstTask = taskDao.getFirstTask()
        return firstTask?.id == task.id
    }

    suspend fun isTaskLast(task: TaskEntity): Boolean {
        val lastTask = taskDao.getLastTask()
        return lastTask?.id == task.id
    }

    suspend fun initializeDefaultTasks() {
        val tasks = taskDao.getAllTasks().first()

        if (tasks.isEmpty()) {
            val firstTask = TaskEntity(
                taskName = "Start of Day",
                duration = 359,
                startTime = strToDateTime("00:01 AM"),
                endTime = strToDateTime("06:00 AM"),
                reminder = strToDateTime("06:00 AM"),
                type = "default",
                position = 0
            )
            val lastTask = TaskEntity(
                taskName = "End of Day",
                duration = 1079,
                startTime = strToDateTime("06:00 AM"),
                endTime = strToDateTime("11:59 PM"),
                reminder = strToDateTime("06:00 AM"),
                type = "default",
                position = Int.MAX_VALUE
            )
            insertTask(firstTask)
            insertTask(lastTask)
        }
    }
}