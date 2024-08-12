package com.app.routineturboa.reminders

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.app.routineturboa.R
import com.app.routineturboa.reminders.receivers.SnoozeReceiver

class NotificationHelper(private val context: Context) {

    companion object {
        private const val TAG = "NotificationHelper"
        const val CHANNEL_ID = "task_reminders_channel"
        private const val SNOOZE_DURATION_MINUTES = 10
    }

    /**
     * Shows a notification for the task.
     * @param taskId The ID of the task.
     * @param taskName The name of the task.
     */
    fun showNotification(taskId: Int, taskName: String) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "Notification permission not granted.")
            return
        }

        // Intent for snoozing the reminder
        val snoozeIntent = Intent(context, SnoozeReceiver::class.java).apply {
            putExtra("TASK_ID", taskId)
        }

        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.routineturbo)
            .setContentTitle(taskName)
            .setContentText("It's time for your task!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(R.drawable.routineturbo, "Snooze", snoozePendingIntent)

        // Show the notification
        with(NotificationManagerCompat.from(context)) {
            notify(taskId, notificationBuilder.build())
        }

        Log.d(TAG, "Displayed notification for task $taskId")
    }
}
