package com.app.routineturboa

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import com.app.routineturboa.data.onedrive.MsalApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class RoutineTurboApp : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    lateinit var msalApp: MsalApp

    companion object {
        lateinit var instance: RoutineTurboApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MyApplication", "     ***** STARTING APPLICATION ****** ")

        instance = this

        msalApp = MsalApp.getInstance(this)

        applicationScope.launch {
            Log.d("MyApplication", "Calling MsalAuthManager.initialize from MyApplication")
            msalApp.initialize()
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
