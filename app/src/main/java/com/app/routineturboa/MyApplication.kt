package com.app.routineturboa

import android.app.Application
import com.app.routineturboa.services.MSALAuthManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MSALAuthManager.getInstance(this)
    }
}
