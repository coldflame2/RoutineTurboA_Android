package com.app.routineturboa.data.local

import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.app.routineturboa.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow

const val tag = "TaskDao"

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks_table ORDER BY position ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Transaction
    suspend fun updateTasksWithNewPositions(tasks: List<TaskEntity>) {
        tasks.forEach { task ->
            Log.d(tag, "Updating task '${task.taskName}'. task ID: ${task.id}" )
            updateTaskPosition(task.id, task.position)
        }
    }

    @Transaction
    suspend fun updateTasksWithNewIds(tasks: List<TaskEntity>) {
        tasks.forEach { task ->
            Log.d(tag, "Updating task '${task.taskName}'. task ID: ${task.id}" )
            updateTaskId(task.id)
        }
    }

    @Query("UPDATE tasks_table SET id = :newId")
    suspend fun updateTaskId(newId: Int)

    @Query("UPDATE tasks_table SET position = :newPosition WHERE id = :taskId")
    suspend fun updateTaskPosition(taskId: Int, newPosition: Int)

    @Update
    suspend fun updateAllTasks(tasks: List<TaskEntity>)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT * FROM tasks_table WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): TaskEntity?

    @Query("SELECT * FROM tasks_table WHERE id = (SELECT MIN(id) FROM tasks_table)")
    suspend fun getFirstTask(): TaskEntity?

    @Query("SELECT * FROM tasks_table WHERE id = (SELECT MAX(id) FROM tasks_table)")
    suspend fun getLastTask(): TaskEntity?

}