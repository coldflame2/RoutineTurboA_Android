package com.app.routineturboa.data.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.routineturboa.R
import com.app.routineturboa.utils.Converters

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RoutineDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao


    companion object {
        private const val TAG = "RoutineDatabase"

        @Volatile
        private var INSTANCE: RoutineDatabase? = null

        fun closeDatabase() {
            INSTANCE?.close()
            INSTANCE = null
            Log.d(TAG, "Database closed.")
        }

        fun getDatabase(context: Context): RoutineDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoutineDatabase::class.java,

                    context.getString(R.string.database_name)
                )
                .setJournalMode(JournalMode.TRUNCATE) // Disable WAL mode
                .build()

                INSTANCE = instance
                Log.d(TAG, "Database initialized.")

                instance
            }
        }
    }
}