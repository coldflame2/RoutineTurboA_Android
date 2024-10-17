package com.app.routineturboa.data.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.app.routineturboa.data.DbConstants
import com.app.routineturboa.utils.Converters
import java.time.LocalDate

@Entity(
    tableName = DbConstants.TASK_DATES_TABLE,
    foreignKeys = [ForeignKey(
        entity = TaskEntity::class,
        parentColumns = ["id"],
        childColumns = ["taskId"],
        onDelete = ForeignKey.CASCADE)]
)
@TypeConverters(Converters::class)
data class TaskDatesEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Default value set for auto-increment
    val taskId: Int, // Foreign key to TaskEntity
    val taskDate: LocalDate // The specific date for this task
)
