package com.app.routineturboa

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import com.app.routineturboa.services.MsalAuthManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class RoutineTurboApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    lateinit var msalAuthManager: MsalAuthManager

    companion object {
        lateinit var instance: RoutineTurboApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()

        Log.d("MyApplication", "     ***** STARTING APPLICATION ****** ")

        instance = this

        msalAuthManager = MsalAuthManager.getInstance(this)

        applicationScope.launch {
            Log.d("MyApplication", "Calling MsalAuthManager.initialize from MyApplication")
            msalAuthManager.initialize()
        }

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channelId = "tasks_channel_id"
        val channelName = "Tasks Reminders"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(channelId, channelName, importance)
        channel.description = "Channel for tasks reminders"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}