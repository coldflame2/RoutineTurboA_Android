package com.app.routineturboa.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.routineturboa.utils.Converters

@Database(
    entities = [
        TaskEntity::class,
        TaskCompletionEntity::class,
        TaskDatesEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppData : RoomDatabase() {
    abstract fun appDao(): AppDao
}
