package com.app.routineturboa

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.app.routineturboa.services.MSALAuthManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MSALAuthManager.getInstance(this)

        val channelId = "tasks channel ID"
        val channelName = "Tasks Reminders"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(channelId, channelName, importance)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

    }
}
