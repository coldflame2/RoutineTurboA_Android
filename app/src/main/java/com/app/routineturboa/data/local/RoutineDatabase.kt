package com.app.routineturboa.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.routineturboa.R
import com.app.routineturboa.utils.Converters

@Database(entities = [TaskEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RoutineDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao


    companion object {
        @Volatile
        private var INSTANCE: RoutineDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Adding the new column to the existing table
                database.execSQL("ALTER TABLE tasks_table ADD COLUMN mainTaskId INTEGER DEFAULT NULL")
            }
        }

        fun getDatabase(context: Context): RoutineDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoutineDatabase::class.java,
                    context.getString(R.string.database_name)
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}