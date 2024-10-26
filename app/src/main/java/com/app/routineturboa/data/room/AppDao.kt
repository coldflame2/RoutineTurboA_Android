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
import com.app.routineturboa.data.dbutils.DbConstants
import com.app.routineturboa.data.room.entities.NonRecurringTaskEntity
import com.app.routineturboa.data.room.entities.TaskCompletionEntity
import com.app.routineturboa.data.room.entities.TaskCompletionHistory
import com.app.routineturboa.data.room.entities.TaskEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

const val tag = "AppDao"

@Dao
interface AppDao {

    @Query("SELECT * FROM ${DbConstants.TASKS_TABLE} ORDER BY position ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM ${DbConstants.TASKS_TABLE} ORDER BY position ASC")
    fun getAllTasksList(): List<TaskEntity>

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM ${DbConstants.TASK_DATES_TABLE}
            WHERE taskId = :taskId AND taskDate = :date
        )
    """)
    suspend fun isNonRecurringTaskOnThisDate(taskId: Int, date: LocalDate): Boolean

    @Query("SELECT name, position FROM ${DbConstants.TASKS_TABLE} WHERE position IS NOT NULL")
    fun getAllTaskNamesAndPositions(): List<TaskNameAndPosition>


    @Query("UPDATE ${DbConstants.TASKS_TABLE} SET position = position + 1 WHERE position >= :startingPosition")
    suspend fun incrementPositionsBelow(startingPosition: Int)

    @Query("SELECT * FROM ${DbConstants.TASKS_TABLE} WHERE id = :taskId LIMIT 1")
    suspend fun getTaskEntityById(taskId: Int): TaskEntity?

    @Query("SELECT name FROM ${DbConstants.TASKS_TABLE} WHERE id = :taskId LIMIT 1")
    suspend fun getTaskName(taskId: Int): String?

    // Query to get the count of tasks with a position greater than the specified one
    @Query("SELECT COUNT(*) FROM ${DbConstants.TASKS_TABLE}  WHERE position > :currentPosition")
    suspend fun getTasksCountWithPositionGreaterThan(currentPosition: Int): Int

    // Query to get the task at the specified position
    @Query("SELECT * FROM ${DbConstants.TASKS_TABLE}  WHERE position = :position LIMIT 1")
    suspend fun getTaskAtPosition(position: Int): TaskEntity?

    @Transaction
    suspend fun safeInsertTask(task: TaskEntity): Long {
        Log.d(tag, "Inserting new task: ${task.name}")
        return try {
            // Insert the task entity
            val taskId = insertTask(task)
            Log.d(tag, "Task inserted. New Task Id=$taskId and position:${task.position}")

            // If the task is non-recurring, insert it into TaskDatesEntity
            if (task.isRecurring != true) {
                val nonRecurringTaskEntity = NonRecurringTaskEntity(
                    taskId = taskId.toInt(),
                    taskDate = task.startDate ?: LocalDate.now(),
                )
                insertInNonRecurringTaskEntity(nonRecurringTaskEntity)
                Log.d(tag, "Inserted non-recurring task for date: ${nonRecurringTaskEntity.taskDate}")
            }

            taskId
        } catch (e: SQLiteConstraintException) {
            Log.e(tag, "Task insertion failed: ${e.message}")
            -1L  // Return -1 to indicate failure
        }
    }

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertInNonRecurringTaskEntity(nonRecurringTaskEntity: NonRecurringTaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM ${DbConstants.TASKS_TABLE}")
    suspend fun deleteAllTasks() // Deletes all tasks from the tasks table

    // region: ------- Methods related to task completion -----------

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

    // endregion

    @Transaction
    suspend fun runTaskTransaction(block: suspend () -> Unit) {
        return block()
    }
}

data class TaskNameAndPosition(
    val name: String,
    val position: Int
)
