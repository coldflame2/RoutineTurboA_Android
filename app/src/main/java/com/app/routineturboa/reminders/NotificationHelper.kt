package com.app.routineturboa.reminders

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.app.routineturboa.R
import com.app.routineturboa.reminders.receivers.SnoozeReceiver
import com.app.routineturboa.reminders.receivers.TaskCompletionReceiver

class NotificationHelper(private val context: Context) {

    companion object {
        private const val TAG = "NotificationHelper"
        const val CHANNEL_ID = "task_reminders_channel"
        const val CHANNEL_NAME = "Tasks Reminders"
        const val CHANNEL_DESCRIPTION = "Channel for tasks reminders"
        private const val SNOOZE_DURATION_MINUTES = 10

        fun createNotificationChannel(context: Context){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelID = CHANNEL_ID
                val channelName = CHANNEL_NAME
                val channelDescription = CHANNEL_DESCRIPTION
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(channelID, channelName, importance)

                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)

            }
        }
    }




    /**
     * Shows a notification for the task.
     * @param taskId The ID of the task.
     * @param taskName The name of the task.
     */
    fun showNotification(taskId: Int, taskName: String) {

        // Check for notification permission
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
            putExtra("TASK_NAME", taskName)
        }

        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent for TaskCompletion
        val taskCompletionIntent = Intent(context, TaskCompletionReceiver::class.java).apply {
            putExtra("TASK_ID", taskId)
            putExtra("TASK_NAME", taskName) // Pass task name to the receiver
        }


        val taskCompletionPendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            taskCompletionIntent,
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
            .addAction(R.drawable.routineturbo, "Complete", taskCompletionPendingIntent)

        // Show the notification
        with(NotificationManagerCompat.from(context)) {
            notify(taskId, notificationBuilder.build())
        }

        Log.d(TAG, "Displayed notification for task $taskId")
    }
}
