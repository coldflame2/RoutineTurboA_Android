package com.app.routineturboa.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.app.routineturboa.data.repository.AppRepository
import com.app.routineturboa.reminders.receivers.ReminderReceiver
import kotlinx.coroutines.flow.first
import java.time.ZoneId
import javax.inject.Inject

class ReminderManager @Inject constructor(
    private val context: Context,
    private val appRepository: AppRepository
) {

    companion object {
        private const val TAG = "ReminderManager"
        const val CHANNEL_ID = "task_reminders_channel"
        private const val SNOOZE_DURATION_MINUTES = 10
    }

    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Schedules reminders for all tasks with a set reminder time.
     * @return True if all reminders are scheduled successfully, false if permission is missing.
     */
    @RequiresApi(Build.VERSION_CODES.S)
    suspend fun scheduleAllReminders(): Boolean {
        Log.d(TAG, "Scheduling reminders for all tasks...")

        val tasks = appRepository.tasks.first()
        Log.d(TAG, "Number of tasks to schedule: ${tasks.size}")

        var permissionMissing = false

        for (task in tasks) {
            // Simulate a delay to test timing behavior
            kotlinx.coroutines.delay(100) // Delay 500ms per task for testing

            val reminderTime = task.reminder
            try {
                val reminderTimeMillis = reminderTime
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

                if (reminderTimeMillis > System.currentTimeMillis()) {
                    val success = scheduleReminder(task.id, task.name, reminderTimeMillis)
                    if (!success) {
                        permissionMissing = true
                    }
                    Log.d(TAG, "Reminder scheduled for task ${task.id}.")

                } else {
                    cancelReminder(task.id)
                    Log.d(TAG, "Reminder for task ${task.id} canceled (past time).")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse reminder time for task ${task.id}: $reminderTime", e)
            }
        }
        return !permissionMissing
    }

    /**
     * Schedules a reminder for a single task.
     * @param taskId The ID of the task.
     * @param taskName The name of the task.
     * @param reminderTimeMillis The time in milliseconds when the reminder should trigger.
     * @return True if the reminder is scheduled successfully, false otherwise.
     */
    @RequiresApi(Build.VERSION_CODES.S)
    fun scheduleReminder(taskId: Int, taskName: String, reminderTimeMillis: Long): Boolean {
        if (!canScheduleExactAlarms()) {
            Log.e(TAG, "Cannot schedule exact alarms. Permission not granted.")
            return false
        }
        // Check if a PendingIntent already exists for this task
        val existingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            Intent(context, ReminderReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        // If the PendingIntent exists, we don't need to reschedule the alarm
        if (existingIntent != null) {
            Log.d(TAG, "Alarm already scheduled for task $taskId. Skipping rescheduling.")
            return true
        }

        // Create an intent that will be broadcast when the alarm triggers
        val reminderIntent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("TASK_ID", taskId)
            putExtra("TASK_NAME", taskName)
        }

        // Create a PendingIntent that will wrap the intent
        val reminderPendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            reminderIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule the alarm
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminderTimeMillis,
            reminderPendingIntent
        )

        Log.d(TAG, "Scheduled reminder for task $taskId at $reminderTimeMillis")
        return true
    }

    /**
     * Cancels a scheduled reminder.
     * @param taskId The ID of the task whose reminder should be canceled.
     */
    fun cancelReminder(taskId: Int) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
            Log.d(TAG, "Canceled reminder for task $taskId")
        }
    }

    /**
     * Checks if the app has permission to schedule exact alarms.
     * @return True if the app can schedule exact alarms, false otherwise.
     */
    fun canScheduleExactAlarms(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
    }

    /**
     * Manually triggers a reminder for testing purposes by sending the same broadcast
     * that would be triggered by the alarm.
     *
     * @param taskId The ID of the task to trigger.
     * @param taskName The name of the task to trigger.
     */
    fun manuallyTriggerReminder(taskId: Int, taskName: String) {
        // Create the same intent that would be used by the AlarmManager
        val reminderIntent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("TASK_ID", taskId)
            putExtra("TASK_NAME", taskName)
        }

        // Log the manual trigger event
        Log.d(TAG, "Manually triggering reminder for task $taskId")

        // Send the broadcast manually to trigger the receiver
        context.sendBroadcast(reminderIntent)
    }

}
