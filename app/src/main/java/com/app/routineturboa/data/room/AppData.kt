package com.app.routineturboa.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.routineturboa.core.dbutils.Converters
import com.app.routineturboa.data.room.entities.NonRecurringTaskEntity
import com.app.routineturboa.data.room.entities.TaskCompletionEntity
import com.app.routineturboa.data.room.entities.TaskEntity

@Database(
    entities = [
        TaskEntity::class,
        TaskCompletionEntity::class,
        NonRecurringTaskEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppData : RoomDatabase() {
    abstract fun appDao(): AppDao
}