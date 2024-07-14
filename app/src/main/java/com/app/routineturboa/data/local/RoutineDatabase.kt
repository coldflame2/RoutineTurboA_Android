package com.app.routineturboa.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app.routineturboa.R
import com.app.routineturboa.data.model.TaskEntity

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class RoutineDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: RoutineDatabase? = null

        fun getDatabase(context: Context): RoutineDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoutineDatabase::class.java,
                    context.getString(R.string.database_name)
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}