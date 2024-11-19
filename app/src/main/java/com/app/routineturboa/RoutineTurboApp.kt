package com.app.routineturboa

import android.app.Application
import android.util.Log
import com.app.routineturboa.data.onedrive.MsalApp
import com.app.routineturboa.data.repository.AppRepository
import com.app.routineturboa.reminders.NotificationHelper
import com.app.routineturboa.reminders.ReminderManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class RoutineTurboApp : Application() {
    @Inject lateinit var msalApp: MsalApp

    companion object {
        lateinit var instance: RoutineTurboApp
            private set
    }

    @Inject lateinit var reminderManager: ReminderManager
    @Inject lateinit var appRepository: AppRepository

    override fun onCreate() {

        super.onCreate()
        Log.d("MyApplication", "     ***** STARTING APPLICATION ****** ")


        instance = this

        // Set up notification channel (remaining one-time setup)
        NotificationHelper.createNotificationChannel(this)
    }
}

