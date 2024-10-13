package com.app.routineturboa.reminders

import androidx.compose.runtime.staticCompositionLocalOf

val LocalReminderManager = staticCompositionLocalOf<ReminderManager> {
    error("No ReminderManager provided")
}
