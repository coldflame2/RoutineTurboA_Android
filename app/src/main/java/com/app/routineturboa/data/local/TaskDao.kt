package com.app.routineturboa.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.routineturboa.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks_table ORDER BY position")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT * FROM tasks_table WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): TaskEntity?

    @Query("SELECT * FROM tasks_table ORDER BY startTime ASC LIMIT 1")
    suspend fun getFirstTask(): TaskEntity?

    @Query("SELECT * FROM tasks_table ORDER BY startTime DESC LIMIT 1")
    suspend fun getLastTask(): TaskEntity?

    @Query("UPDATE tasks_table SET position = position + 1 WHERE position >= :fromPosition AND position < :maxPosition")
    suspend fun shiftPositionsDown(fromPosition: Int, maxPosition: Int)

    @Query("SELECT MAX(position) FROM tasks_table WHERE position < :maxPosition")
    suspend fun getMaxPosition(maxPosition: Int): Int?
}