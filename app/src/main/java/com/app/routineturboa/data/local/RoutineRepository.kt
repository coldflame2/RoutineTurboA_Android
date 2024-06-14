package com.app.routineturboa.data.local

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.app.routineturboa.data.model.Task
import com.app.routineturboa.utils.TimeUtils

class RoutineRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val db: SQLiteDatabase = dbHelper.readableDatabase

    fun getAllTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        val cursor: Cursor = db.query(
            DatabaseHelper.DailyRoutine.TABLE_NAME,
            null,
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

                // Convert to 12-hour format
                val formattedStartTime = TimeUtils.convertTo12HourFormat(startTime.split(" ")[1])
                val formattedEndTime = TimeUtils.convertTo12HourFormat(endTime.split(" ")[1])

                val task = Task(
                    getInt(getColumnIndexOrThrow(BaseColumns._ID)),
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
        return tasks
    }
}
