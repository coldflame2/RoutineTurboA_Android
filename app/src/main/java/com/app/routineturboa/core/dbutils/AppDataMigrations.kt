package com.app.routineturboa.core.dbutils

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Step 1: Add the new column
        db.execSQL("ALTER TABLE TaskEntity ADD COLUMN linkedMainIfHelper INTEGER")

        // Step 2: Copy data from old column to new one
        db.execSQL("UPDATE TaskEntity SET linkedMainIfHelper = mainTaskId")

        // Optional: Drop the old column if desired, but Room doesn't support dropping columns directly
    }
}
