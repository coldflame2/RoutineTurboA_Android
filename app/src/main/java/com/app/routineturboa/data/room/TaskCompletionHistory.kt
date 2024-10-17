package com.app.routineturboa.data.room

import androidx.room.Embedded
import androidx.room.Relation

data class TaskCompletionHistory(
    // @ Embedded annotation means that TaskEntity is embedded inside TasksWithCompletion. So, the task field contains all the columns from the TaskEntity (like task name, duration, start time, etc.).
    @Embedded val task: TaskEntity,  // // A single task

    @Relation(
        parentColumn = "id", // This refers to the primary key id from the TaskEntity (i.e., the tasks_table).
        entityColumn = "taskId"  // This refers to the taskId in the task_completion table. It’s how Room knows which completions are linked to which task.
    )
    val completions: List<TaskCompletionEntity>  // All completion records for this task
)

/**
 * Unlike TaskEntity and TaskCompletionEntity, TasksWithCompletion is not a table in the database.
 * It’s just a data class used to combine data from the tasks_table and task_completion table.
 */