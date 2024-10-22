package com.app.routineturboa.data.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.app.routineturboa.data.dbutils.DbConstants
import com.app.routineturboa.data.dbutils.Converters
import java.time.LocalDate

// Only for non-recurring tasks
@Entity(
    tableName = DbConstants.TASK_DATES_TABLE,
    foreignKeys = [ForeignKey(
        entity = TaskEntity::class,
        parentColumns = ["id"],
        childColumns = ["taskId"],
        onDelete = ForeignKey.CASCADE)]
)
@TypeConverters(Converters::class)
data class NonRecurringTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Default value set for auto-increment
    val taskId: Int, // Foreign key to TaskEntity
    val taskDate: LocalDate, // The specific date for this task
    val isException: Boolean = false // True if this date is an exception (e.g., skipped or extra occurrence)
)
