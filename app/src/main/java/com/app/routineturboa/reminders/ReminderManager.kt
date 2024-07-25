package com.app.routineturboa.reminders

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.app.routineturboa.R
import com.app.routineturboa.data.local.RoutineRepository
import com.app.routineturboa.utils.TimeUtils.dateTimeToString
import java.time.ZoneId

class ReminderManager(private val context: Context) {
    private val notificationManager: NotificationManager = context.getSystemService(
        Context.NOTIFICATION_SERVICE) as NotificationManager
    private val alarmManager: AlarmManager = context.getSystemService(
        Context.ALARM_SERVICE) as AlarmManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Task Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for task reminders"
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun showNotification(taskId: Int, title: String, content: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.routineturbo)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(taskId, notification)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun scheduleReminder(taskId: Int, reminderTime: Long) {
        Log.d("ReminderManager", "Scheduling reminder for task $taskId at $reminderTime")

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("TASK_ID", taskId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminderTime,
            pendingIntent
        )

        alarmManager.canScheduleExactAlarms(
        )

        Log.d("ReminderManager", "Reminder scheduled for task $taskId at $reminderTime")
    }

    @RequiresApi(Build.VERSION_CODES.S)
    suspend fun observeAndScheduleReminders(context: Context) {
        Log.d("ReminderManager", "Observing and scheduling reminders")
        val dbRepository = RoutineRepository(context)

        dbRepository.getAllTasks().collect { tasks ->
            tasks.forEach { task ->
                task.reminder.let { reminderTime ->
                    try {
                        val taskName = task.taskName
                        val reminderTimeFormatted = dateTimeToString(reminderTime)
                        val reminderTimeZoned = reminderTime.atZone(ZoneId.systemDefault())
                        val reminderTimeInMilli = reminderTimeZoned.toInstant().toEpochMilli()

                        if (reminderTimeInMilli > System.currentTimeMillis()) {
                            scheduleReminder(task.id, reminderTimeInMilli)
                            Log.d("ReminderManager", "Task: $taskName. Reminder time in system zone: $reminderTimeZoned")
                            Log.d("ReminderManager", "Scheduling Reminder: $reminderTime")

                        } else {
                            cancelReminder(task.id)
                        }

                    } catch (e: Exception) {
                        Log.e("ReminderManager", "Failed to parse reminder time ${task.id}: $reminderTime", e)
                    }
                }
            }
        }
    }


    private fun cancelReminder(taskId: Int) {
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
        }
    }

    fun triggerReminder(taskId: Int) {
        Log.d("ReminderManager", "Triggering reminder for task $taskId")
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("TASK_ID", taskId)
        }
        context.sendBroadcast(intent)
    }

    companion object {
        const val CHANNEL_ID = "task_reminders_channel"
    }
}