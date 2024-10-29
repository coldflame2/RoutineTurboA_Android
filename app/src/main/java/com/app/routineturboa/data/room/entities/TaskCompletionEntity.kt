package com.app.routineturboa.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import com.app.routineturboa.core.dbutils.DbConstants
import java.time.LocalDate

@Entity(
    tableName = DbConstants.TASK_COMPLETIONS_TABLE,
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["taskId", "date"], unique = true)] // Add unique constraint on taskId and date

)
data class TaskCompletionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val taskId: Int,  // Task ID this completion refers to (linked to ID col in tasks_table).
    val date: LocalDate,
    val isCompleted: Boolean
)
