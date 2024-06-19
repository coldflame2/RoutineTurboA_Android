package com.app.routineturboa.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "RoutineTurbo.db"
        private const val TAG = "DatabaseHelper"
    }

    object DailyRoutine {
        const val TABLE_NAME = "daily_routine"
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_START_TIME = "start_time"
        const val COLUMN_NAME_END_TIME = "end_time"
        const val COLUMN_NAME_DURATION = "duration"
        const val COLUMN_NAME_TASK_NAME = "task_name"
        const val COLUMN_NAME_REMINDERS = "reminders"
        const val COLUMN_NAME_TYPE = "type"
        const val COLUMN_NAME_POSITION = "position"
    }

    private val createEntries = """
        CREATE TABLE IF NOT EXISTS ${DailyRoutine.TABLE_NAME} (
            ${DailyRoutine.COLUMN_NAME_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${DailyRoutine.COLUMN_NAME_START_TIME} DATETIME,
            ${DailyRoutine.COLUMN_NAME_END_TIME} DATETIME,
            ${DailyRoutine.COLUMN_NAME_DURATION} INTEGER,
            ${DailyRoutine.COLUMN_NAME_TASK_NAME} TEXT,
            ${DailyRoutine.COLUMN_NAME_REMINDERS} DATETIME,
            ${DailyRoutine.COLUMN_NAME_TYPE} TEXT,
            ${DailyRoutine.COLUMN_NAME_POSITION} INTEGER)
    """

    private val deleteEntries = "DROP TABLE IF EXISTS ${DailyRoutine.TABLE_NAME}"

    override fun onCreate(db: SQLiteDatabase) {
        Log.d(TAG, "Creating database with the following query: $createEntries")
        db.execSQL(createEntries)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "Upgrading database from version $oldVersion to $newVersion")
        db.execSQL(deleteEntries)
        onCreate(db)
    }

    override fun getReadableDatabase(): SQLiteDatabase {
        Log.d(TAG, "Getting readable database")
        copyDatabase()
        return super.getReadableDatabase()
    }

    override fun getWritableDatabase(): SQLiteDatabase {
        Log.d(TAG, "Getting writable database")
        copyDatabase()
        return super.getWritableDatabase()
    }

    private fun copyDatabase() {
        val dbPath: String = context.getDatabasePath(DATABASE_NAME).absolutePath
        val dbFile = File(dbPath)
        if (!dbFile.exists()) {
            Log.d(TAG, "Database does not exist, copying from external storage.")
            val externalDbPath = Environment.getExternalStorageDirectory().absolutePath + "/$DATABASE_NAME"
            val externalDbFile = File(externalDbPath)

            try {
                if (externalDbFile.exists()) {
                    Log.d(TAG, "External database exists, copying from external storage.")
                    FileInputStream(externalDbFile).use { inputStream ->
                        FileOutputStream(dbPath).use { outputStream ->
                            Log.d(TAG, "Copying database from $externalDbPath to $dbPath")
                            copyStream(inputStream, outputStream)
                        }
                    }
                } else {
                    Log.d(TAG, "External database does not exist, copying from assets.")
                    context.assets.open(DATABASE_NAME).use { inputStream ->
                        FileOutputStream(dbPath).use { outputStream ->
                            Log.d(TAG, "Copying database from assets to $dbPath")
                            copyStream(inputStream, outputStream)
                        }
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error copying database: ${e.message}", e)
            }
        } else {
            Log.d(TAG, "Database exists, skipping copy.")
        }
    }

    @Throws(IOException::class)
    private fun copyStream(inputStream: InputStream, outputStream: OutputStream) {
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }
        Log.d(TAG, "Finished copying stream")
    }
}
