package com.app.routineturboa.data.local

import android.content.Context
import com.app.routineturboa.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow

class RoutineRepository(context: Context) {
    private val taskDao: TaskDao = RoutineDatabase.getDatabase(context).taskDao()

    fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()

    suspend fun insertTask(task: TaskEntity): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)

    suspend fun getTaskById(taskId: Int): TaskEntity? = taskDao.getTaskById(taskId)

    suspend fun isTaskFirst(task: TaskEntity): Boolean {
        val firstTask = taskDao.getFirstTask()
        return firstTask?.id == task.id
    }

    suspend fun isTaskLast(task: TaskEntity): Boolean {
        val lastTask = taskDao.getLastTask()
        return lastTask?.id == task.id
    }
}