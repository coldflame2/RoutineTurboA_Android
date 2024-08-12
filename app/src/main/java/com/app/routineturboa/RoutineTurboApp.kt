package com.app.routineturboa

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import com.app.routineturboa.data.onedrive.MsalApp
import com.app.routineturboa.data.repository.AppRepository
import com.app.routineturboa.reminders.ReminderManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class RoutineTurboApp : Application() {
    companion object {
        lateinit var instance: RoutineTurboApp
            private set
    }

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    lateinit var msalApp: MsalApp

    @Inject lateinit var reminderManager: ReminderManager  // Singleton provided by Hilt
    @Inject lateinit var appRepository: AppRepository

    override fun onCreate() {
        super.onCreate()
        Log.d("MyApplication", "     ***** STARTING APPLICATION ****** ")

        instance = this
        msalApp = MsalApp.getInstance(this)
        applicationScope.launch {
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
