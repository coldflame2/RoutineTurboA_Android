package com.app.routineturboa.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.app.routineturboa.data.model.Task
import com.app.routineturboa.utils.TimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoutineRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val db: SQLiteDatabase = dbHelper.readableDatabase

    suspend fun getAllTasks(): List<Task> = withContext(Dispatchers.IO) {
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
                tasks.add(task)
            }
        }
        cursor.close()
        return@withContext tasks
    }

    suspend fun updateTask(task: Task) = withContext(Dispatchers.IO) {
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
    }
}
