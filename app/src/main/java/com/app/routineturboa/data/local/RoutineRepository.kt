package com.app.routineturboa.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.app.routineturboa.data.model.Task
import com.app.routineturboa.utils.TimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoutineRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val db: SQLiteDatabase = dbHelper.readableDatabase

    companion object {
        private const val TAG = "RoutineRepository"
    }

    suspend fun getAllTasks(): List<Task> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Fetching all tasks from database")
        val tasks = mutableListOf<Task>()
        val cursor: Cursor = db.query(
            DatabaseHelper.DailyRoutine.TABLE_NAME,
            arrayOf(
                DatabaseHelper.DailyRoutine.COLUMN_NAME_ID,
                DatabaseHelper.DailyRoutine.COLUMN_NAME_START_TIME,
                DatabaseHelper.DailyRoutine.COLUMN_NAME_END_TIME,
                DatabaseHelper.DailyRoutine.COLUMN_NAME_DURATION,
                DatabaseHelper.DailyRoutine.COLUMN_NAME_TASK_NAME,
                DatabaseHelper.DailyRoutine.COLUMN_NAME_REMINDERS,
                DatabaseHelper.DailyRoutine.COLUMN_NAME_TYPE,
                DatabaseHelper.DailyRoutine.COLUMN_NAME_POSITION
            ),
            null,
            null,
            null,
            null,
            "${DatabaseHelper.DailyRoutine.COLUMN_NAME_POSITION} ASC"
        )

        with(cursor) {
            while (moveToNext()) {
                val startTime = getString(getColumnIndexOrThrow(DatabaseHelper.DailyRoutine.COLUMN_NAME_START_TIME))
                val endTime = getString(getColumnIndexOrThrow(DatabaseHelper.DailyRoutine.COLUMN_NAME_END_TIME))

                val formattedStartTime = TimeUtils.convertTo12HourFormat(startTime)
                val formattedEndTime = TimeUtils.convertTo12HourFormat(endTime)

                val task = Task(
                    getInt(getColumnIndexOrThrow(DatabaseHelper.DailyRoutine.COLUMN_NAME_ID)),
                    formattedStartTime,
                    formattedEndTime,
                    getInt(getColumnIndexOrThrow(DatabaseHelper.DailyRoutine.COLUMN_NAME_DURATION)),
                    getString(getColumnIndexOrThrow(DatabaseHelper.DailyRoutine.COLUMN_NAME_TASK_NAME)),
                    getString(getColumnIndexOrThrow(DatabaseHelper.DailyRoutine.COLUMN_NAME_REMINDERS)),
                    getString(getColumnIndexOrThrow(DatabaseHelper.DailyRoutine.COLUMN_NAME_TYPE)),
                    getInt(getColumnIndexOrThrow(DatabaseHelper.DailyRoutine.COLUMN_NAME_POSITION))
                )
                Log.d(TAG, "Fetched task: $task")
                tasks.add(task)
            }
        }
        cursor.close()
        Log.d(TAG, "Completed fetching all tasks")
        return@withContext tasks
    }

    suspend fun addTask(task: Task) = withContext(Dispatchers.IO) {
        Log.d(TAG, "Adding new task: $task")
        val values = ContentValues().apply {
            val currentDate = "2024-01-01"  // Example, you should replace it with the actual date
            put(DatabaseHelper.DailyRoutine.COLUMN_NAME_START_TIME, TimeUtils.formatToDatabaseTime(currentDate, TimeUtils.convertTo24HourFormat(task.startTime)))
            put(DatabaseHelper.DailyRoutine.COLUMN_NAME_END_TIME, TimeUtils.formatToDatabaseTime(currentDate, TimeUtils.convertTo24HourFormat(task.endTime)))
            put(DatabaseHelper.DailyRoutine.COLUMN_NAME_DURATION, task.duration)
            put(DatabaseHelper.DailyRoutine.COLUMN_NAME_TASK_NAME, task.taskName)
            put(DatabaseHelper.DailyRoutine.COLUMN_NAME_REMINDERS, task.reminders)
            put(DatabaseHelper.DailyRoutine.COLUMN_NAME_TYPE, task.type)
            put(DatabaseHelper.DailyRoutine.COLUMN_NAME_POSITION, task.position)
        }
        db.insert(DatabaseHelper.DailyRoutine.TABLE_NAME, null, values)
        Log.d(TAG, "Task added successfully: ${task.taskName}")
    }

    suspend fun updateTask(task: Task) = withContext(Dispatchers.IO) {
        Log.d(TAG, "Updating task: $task")
        val values = ContentValues().apply {
            val currentDate = "2024-01-01"  // Example, you should replace it with the actual date
            put(DatabaseHelper.DailyRoutine.COLUMN_NAME_START_TIME, TimeUtils.formatToDatabaseTime(currentDate, TimeUtils.convertTo24HourFormat(task.startTime)))
            put(DatabaseHelper.DailyRoutine.COLUMN_NAME_END_TIME, TimeUtils.formatToDatabaseTime(currentDate, TimeUtils.convertTo24HourFormat(task.endTime)))
            put(DatabaseHelper.DailyRoutine.COLUMN_NAME_DURATION, task.duration)
            put(DatabaseHelper.DailyRoutine.COLUMN_NAME_TASK_NAME, task.taskName)
            put(DatabaseHelper.DailyRoutine.COLUMN_NAME_REMINDERS, task.reminders)
            put(DatabaseHelper.DailyRoutine.COLUMN_NAME_TYPE, task.type)
            put(DatabaseHelper.DailyRoutine.COLUMN_NAME_POSITION, task.position)
        }
        val selection = "${DatabaseHelper.DailyRoutine.COLUMN_NAME_ID} = ?"
        val selectionArgs = arrayOf(task.id.toString())
        db.update(
            DatabaseHelper.DailyRoutine.TABLE_NAME,
            values,
            selection,
            selectionArgs
        )
        Log.d(TAG, "Task updated successfully: ${task.taskName}")
    }

    suspend fun updatePositions(startPosition: Int) = withContext(Dispatchers.IO) {
        Log.d(TAG, "Updating positions starting from: $startPosition")
        val updateQuery = "UPDATE ${DatabaseHelper.DailyRoutine.TABLE_NAME} SET ${DatabaseHelper.DailyRoutine.COLUMN_NAME_POSITION} = ${DatabaseHelper.DailyRoutine.COLUMN_NAME_POSITION} + 1 WHERE ${DatabaseHelper.DailyRoutine.COLUMN_NAME_POSITION} >= ?" // Added query to update positions
        db.execSQL(updateQuery, arrayOf(startPosition)) // Execute the query with the start position
        Log.d(TAG, "Positions updated successfully from: $startPosition")
    }
}
