package com.app.routineturboa.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.app.routineturboa.data.local.RoutineRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra("TASK_ID", -1)
        if (taskId != -1) {
            CoroutineScope(Dispatchers.IO).launch {
                val dbRepository = RoutineRepository(context)
                val reminderManager = ReminderManager(context)
                val task = dbRepository.getTaskById(taskId)
                task?.let {
                    reminderManager.showNotification(it.id, it.type, it.taskName)
                }
            }
        }
    }

}