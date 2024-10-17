package com.app.routineturboa.data.room

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.app.routineturboa.data.DbConstants
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

const val tag = "TaskDao"

@Dao
interface AppDao {

    @Transaction
    suspend fun runTaskTransaction(block: suspend () -> Unit) {
        return block()
    }

    /**
     * @ Query:
     *
     * Select tasks based on the specific date,
     * By joining the TaskEntity with TaskDateEntity using the foreign key taskId.
     */
    @Query("""
    SELECT ${DbConstants.TASKS_TABLE}.* 
    FROM ${DbConstants.TASKS_TABLE} 
    INNER JOIN task_dates ON ${DbConstants.TASKS_TABLE}.id = task_dates.taskId
    WHERE task_dates.taskDate = :date
    ORDER BY ${DbConstants.TASKS_TABLE}.position ASC
""")
    fun getTasksByDate(date: LocalDate): Flow<List<TaskEntity>>


    @Query("""
    SELECT * FROM ${DbConstants.TASKS_TABLE} ORDER BY ${DbConstants.TASKS_TABLE}.position ASC;
    """)
    fun allTasksInTaskDatesEntity(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM ${DbConstants.TASKS_TABLE} ORDER BY position ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM ${DbConstants.TASKS_TABLE} WHERE position = (SELECT MAX(position) FROM ${DbConstants.TASKS_TABLE})")
    suspend fun getTaskWithMaxPosition(): TaskEntity?

    // Query to get tasks by type
    @Query("SELECT * FROM ${DbConstants.TASKS_TABLE} WHERE type = :type")
    fun getTasksByType(type: String): Flow<List<TaskEntity>>

    @Query("SELECT COUNT(*) FROM ${DbConstants.TASKS_TABLE}")
    suspend fun getTasksCount(): Int

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Transaction
    suspend fun updateTasksWithNewPositions(tasks: List<TaskEntity>) {
        tasks.forEach { task ->
            Log.d(tag, "Updating task '${task.name}'. task ID: ${task.id}, position: ${task.position}" )
            updateTaskPosition(task.id, task.position)
        }
    }

    @Transaction
    suspend fun updateTasksWithNewIds(tasks: List<TaskEntity>) {
        tasks.forEach { task ->
            Log.d(tag, "Updating task '${task.name}'. task ID: ${task.id}" )
            updateTaskId(task.id)
        }
    }

    @Query("UPDATE ${DbConstants.TASKS_TABLE} SET id = :newId")
    suspend fun updateTaskId(newId: Int)

    @Query("SELECT name FROM ${DbConstants.TASKS_TABLE} WHERE id = :taskId LIMIT 1")
    suspend fun getTaskName(taskId: Int): String?

    @Query("UPDATE ${DbConstants.TASKS_TABLE} SET position = :newPosition WHERE id = :taskId")
    suspend fun updateTaskPosition(taskId: Int, newPosition: Int)

    @Update
    suspend fun updateAllTasks(tasks: List<TaskEntity>)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT * FROM ${DbConstants.TASKS_TABLE} WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): TaskEntity?

    @Query("SELECT * FROM ${DbConstants.TASKS_TABLE} WHERE id = (SELECT MIN(id) FROM ${DbConstants.TASKS_TABLE})")
    suspend fun getFirstTask(): TaskEntity?

    @Query("SELECT * FROM ${DbConstants.TASKS_TABLE} WHERE id = (SELECT MAX(id) FROM ${DbConstants.TASKS_TABLE})")
    suspend fun getLastTask(): TaskEntity?

    @Transaction
    suspend fun safeInsertTaskWithDate(task: TaskEntity, taskDate: LocalDate): Long {
        return try {
            // Insert task entity
            val taskId = insertTask(task)

            // Insert task date entity if task insertion is successful
            val taskDatesEntity = TaskDatesEntity(taskId = taskId.toInt(), taskDate = taskDate)
            insertTaskDate(taskDatesEntity)

            // Return the taskId as confirmation of success
            taskId
        } catch (e: SQLiteConstraintException) {
            Log.e("AppDao", "Task insertion failed: ${e.message}")
            -1L  // Return -1 to indicate failure
        }
    }

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTask(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTaskDate(taskDatesEntity: TaskDatesEntity)


    @Query("DELETE FROM ${DbConstants.TASKS_TABLE}")
    suspend fun deleteAllTasks() // Deletes all tasks from the tasks table

    @Query("UPDATE ${DbConstants.TASKS_TABLE} SET position = position + 1 WHERE position >= :position")
    suspend fun incrementPositionsBelow(position: Int)

    // Region: Methods related to task completion

    // Insert or update completion status
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTaskCompletion(taskCompletion: TaskCompletionEntity)

    // Get completion status for a task on a specific date
    @Query("SELECT * FROM ${DbConstants.TASK_COMPLETIONS_TABLE} WHERE taskId = :taskId AND date = :date")
    suspend fun getTaskCompletion(taskId: Int, date: LocalDate): TaskCompletionEntity?

    // Get all completion statuses for a task
    @Query("SELECT * FROM ${DbConstants.TASK_COMPLETIONS_TABLE} WHERE taskId = :taskId")
    suspend fun getAllTaskCompletions(taskId: Int): List<TaskCompletionEntity>

    // Optional: Get tasks with their completion status for today
    @Transaction
    @Query("""
        SELECT * FROM ${DbConstants.TASKS_TABLE}
    """)
    suspend fun getTasksWithCompletionStatus(): List<TaskCompletionHistory>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTaskCompletion(taskCompletion: TaskCompletionEntity)

    @Query("SELECT * FROM ${DbConstants.TASK_COMPLETIONS_TABLE}")
    suspend fun getAllTaskCompletions(): List<TaskCompletionEntity>

    @Query("SELECT * FROM ${DbConstants.TASK_COMPLETIONS_TABLE} WHERE taskId = :taskId")
    suspend fun getTaskCompletionsByTaskId(taskId: Int): List<TaskCompletionEntity>
}
