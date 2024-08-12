package com.app.routineturboa.reminders.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.app.routineturboa.reminders.NotificationHelper

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "ReminderReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra("TASK_ID", -1)
        val taskName = intent.getStringExtra("TASK_NAME") ?: "Task"

        if (taskId != -1) {
            Log.d(TAG, "Received alarm for task $taskId")
            // Show the notification
            val notificationHelper = NotificationHelper(context)
            notificationHelper.showNotification(taskId, taskName)
        } else {
            Log.e(TAG, "Invalid task ID received")
        }
    }
}
