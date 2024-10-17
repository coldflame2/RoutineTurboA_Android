package com.app.routineturboa.reminders.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.app.routineturboa.RoutineTurboApp
import com.app.routineturboa.data.repository.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class TaskCompletionReceiver : BroadcastReceiver() {
    val tag = "TaskCompletionReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        val app = context.applicationContext as RoutineTurboApp
        val repository = app.appRepository

        // Log that the receiver has been triggered
        Log.d(tag, "TaskCompletionReceiver triggered")

        // Log the taskId from the intent extras for testing purposes
        val taskId = intent.getIntExtra("TASK_ID", -1)
        val taskName = intent.getStringExtra("TASK_NAME")

        val reminderManager = app.reminderManager
        reminderManager.cancelReminder(taskId)

        if (taskId != -1) {
            // Mark the task as complete
            CoroutineScope(Dispatchers.IO).launch {
                repository.markTaskAsCompleted(taskId)
            }
            Log.d("TaskCompletionReceiver", "Marked task '$taskName' as completed.")
        } else {
            Log.e("TaskCompletionReceiver", "No valid taskId received.")
        }

        // Cancel the notification after snoozing
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(taskId)  // Cancels the current notification for the task


    }


}
