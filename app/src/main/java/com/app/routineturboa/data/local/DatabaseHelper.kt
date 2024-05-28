package com.app.routineturboa.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "RoutineTurbo.db"
    }

    object DailyRoutine : BaseColumns {
        const val TABLE_NAME = "daily_routine"
        const val COLUMN_NAME_START_TIME = "start_time"
        const val COLUMN_NAME_END_TIME = "end_time"
        const val COLUMN_NAME_DURATION = "duration"
        const val COLUMN_NAME_TASK_NAME = "task_name"
        const val COLUMN_NAME_REMINDERS = "reminders"
        const val COLUMN_NAME_TYPE = "type"
        const val COLUMN_NAME_POSITION = "position"
    }

    private val createEntries =

        """CREATE TABLE IF NOT EXISTS ${DailyRoutine.TABLE_NAME} (
            ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${DailyRoutine.COLUMN_NAME_START_TIME} DATETIME,
            ${DailyRoutine.COLUMN_NAME_END_TIME} DATETIME,
            ${DailyRoutine.COLUMN_NAME_DURATION} INTEGER,
            ${DailyRoutine.COLUMN_NAME_TASK_NAME} TEXT,
            ${DailyRoutine.COLUMN_NAME_REMINDERS} DATETIME,
            ${DailyRoutine.COLUMN_NAME_TYPE} TEXT,
            ${DailyRoutine.COLUMN_NAME_POSITION} INTEGER)"""

    private val deleteEntries = "DROP TABLE IF EXISTS ${DailyRoutine.TABLE_NAME}"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createEntries)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(deleteEntries)
        onCreate(db)
    }

    override fun getReadableDatabase(): SQLiteDatabase {
        copyDatabase()
        return super.getReadableDatabase()
    }

    override fun getWritableDatabase(): SQLiteDatabase {
        copyDatabase()
        return super.getWritableDatabase()
    }

    private fun copyDatabase() {
        val dbPath: String = context.getDatabasePath(DATABASE_NAME).absolutePath
        val dbFile = File(dbPath)
        if (!dbFile.exists()) {
            context.assets.open(DATABASE_NAME).use { inputStream: InputStream ->
                FileOutputStream(dbPath).use { outputStream: OutputStream ->
                    copyStream(inputStream, outputStream)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun copyStream(inputStream: InputStream, outputStream: OutputStream) {
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }
    }
}
