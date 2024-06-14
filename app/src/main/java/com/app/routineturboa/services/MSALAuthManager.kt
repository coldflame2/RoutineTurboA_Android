package com.app.routineturboa.services

import android.app.Activity
import android.content.Context
import android.util.Log
import com.app.routineturboa.R
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.exception.MsalException

class MSALAuthManager(context: Context) {

    private val appContext = context.applicationContext
    private var singleAccountApp: ISingleAccountPublicClientApplication? = null

    init {
        PublicClientApplication.createSingleAccountPublicClientApplication(
            appContext,
            R.raw.auth_config_single_account,
            object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {
                override fun onCreated(application: ISingleAccountPublicClientApplication) {
                    Log.d("MSALAuthManager", "MSAL client created")
                    singleAccountApp = application
                }

                override fun onError(exception: MsalException) {
                    Log.e("MSALAuthManager", "MSAL client creation error: ${exception.message}")
                }
            })
    }

    fun signIn(activity: Activity, callback: AuthenticationCallback) {
        Log.d("MSALAuthManager", "Attempting to sign in")
        singleAccountApp?.signIn(activity, null, arrayOf("User.Read", "Files.Read"), callback)
            ?: Log.e("MSALAuthManager", "MSAL client is not initialized")
    }

    fun signOut(callback: ISingleAccountPublicClientApplication.SignOutCallback) {
        singleAccountApp?.signOut(callback)
    }
}
