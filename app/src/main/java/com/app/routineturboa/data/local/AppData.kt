package com.app.routineturboa.data.local

import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.routineturboa.RoutineTurboApp
import com.app.routineturboa.utils.Converters

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppData : RoomDatabase() {

    abstract fun taskDao(): AppDao

    companion object {
        private const val TAG = "RoutineDatabase"

        @Volatile
        private var INSTANCE: AppData? = null

        fun getDatabase(): AppData {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    RoutineTurboApp.instance.applicationContext,
                    AppData::class.java,
                    "routine_database"
                )
                .build()
                INSTANCE = instance
                Log.d(TAG, "Database initialized.")
                instance
            }
        }
    }
}