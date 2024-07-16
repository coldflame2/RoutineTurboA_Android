package com.app.routineturboa

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import com.app.routineturboa.services.MsalAuthManager

class MyApplication : Application() {

    lateinit var msalAuthManager: MsalAuthManager

    companion object {
        lateinit var instance: MyApplication
            private set
    }

    override fun onCreate() {
        Log.d("MyApplication", "     ***** STARTING APPLICATION ****** ")

        super.onCreate()
        instance = this

        msalAuthManager = MsalAuthManager.getInstance(this)
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