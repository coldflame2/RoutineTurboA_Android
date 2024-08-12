package com.app.routineturboa.reminders.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.app.routineturboa.RoutineTurboApp

class SnoozeReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onReceive(context: Context, intent: Intent) {

        val taskId = intent.getIntExtra("TASK_ID", -1)
        val taskName = intent.getStringExtra("TASK_NAME")
        val isTaskValid = taskId != -1 && !taskName.isNullOrEmpty()

        if (isTaskValid){
            // Handle the snooze action by rescheduling the task for 10 minutes later
            Log.d("SnoozeReceiver", "Snooze action received for task $taskId")

            // Access ReminderManager from the Application context via RoutineTurboApp
            val app = context.applicationContext as RoutineTurboApp
            val reminderManager = app.reminderManager
            reminderManager.cancelReminder(taskId)

            val snoozeTimeMillis = System.currentTimeMillis() + 10 * 60 * 1000  // 10
            if (taskName != null) {
                reminderManager.scheduleReminder(taskId, taskName, snoozeTimeMillis)
            }

            // Cancel the notification after snoozing
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(taskId)  // Cancels the current notification for the task


        } else {
            Log.e("SnoozeReceiver", "Invalid taskId received in SnoozeReceiver")
        }
    }
}
