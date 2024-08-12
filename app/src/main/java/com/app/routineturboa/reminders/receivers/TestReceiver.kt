package com.app.routineturboa.reminders.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

class TestReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onReceive(context: Context, intent: Intent) {
        // Log that the receiver has been triggered
        Log.d("TestReceiver", "Receiver triggered")

        // Log the taskId from the intent extras for testing purposes
        val taskId = intent.getIntExtra("TASK_ID", -1)
        val taskName = intent.getStringExtra("TASK_NAME")
        if (taskId != -1) {
            Log.d("TestReceiver", "Received taskName: $taskName")
        } else {
            Log.e("TestReceiver", "No valid taskId received")
        }
    }
}
