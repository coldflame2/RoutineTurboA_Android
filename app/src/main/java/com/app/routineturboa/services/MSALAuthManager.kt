package com.app.routineturboa.services

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.app.routineturboa.R
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAccount
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.exception.MsalException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

class MSALAuthManager(context: Context) {

    private val appContext = context.applicationContext
    private val preferences: SharedPreferences = appContext.getSharedPreferences("msal_prefs", Context.MODE_PRIVATE)
    var singleAccountApp: ISingleAccountPublicClientApplication? = null
    var currentAccount: IAccount? = null

    init {
        PublicClientApplication.createSingleAccountPublicClientApplication(
            appContext,
            R.raw.auth_config_single_account,
            object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {
                override fun onCreated(application: ISingleAccountPublicClientApplication) {
                    Log.d("MSALAuthManager", "MSAL client created")
                    singleAccountApp = application
                    checkCurrentAccount()
                }

                override fun onError(exception: MsalException) {
                    Log.e("MSALAuthManager", "MSAL client creation error: ${exception.message}")
                }
            })
    }

    private fun checkCurrentAccount() {
        singleAccountApp?.getCurrentAccountAsync(object : ISingleAccountPublicClientApplication.CurrentAccountCallback {
            override fun onAccountLoaded(activeAccount: IAccount?) {
                currentAccount = activeAccount
                if (activeAccount != null) {
                    Log.d("MSALAuthManager", "An account is already signed in.")
                    loadAuthResult() // Load authentication result from preferences
                } else {
                    Log.d("MSALAuthManager", "No account is signed in.")
                }
            }

            override fun onAccountChanged(priorAccount: IAccount?, currentAccount: IAccount?) {
                this@MSALAuthManager.currentAccount = currentAccount
            }

            override fun onError(exception: MsalException) {
                Log.e("MSALAuthManager", "Error loading current account: ${exception.message}")
            }
        })
    }

    companion object {
        @Volatile
        private var INSTANCE: MSALAuthManager? = null

        fun getInstance(context: Context): MSALAuthManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: MSALAuthManager(context).also { INSTANCE = it }
            }
    }

    fun signIn(activity: Activity, callback: AuthenticationCallback) {
        if (singleAccountApp != null) {
            Log.d("MSALAuthManager", "Attempting to sign in")
            singleAccountApp?.signIn(activity, null, arrayOf("User.Read", "Files.Read"), callback)
        } else {
            Log.e("MSALAuthManager", "MSAL client is not initialized")
        }
    }

    fun signOut(callback: ISingleAccountPublicClientApplication.SignOutCallback) {
        singleAccountApp?.signOut(callback)
        clearAuthResult() // Clear saved authentication result
    }

    suspend fun getProfileImageUrl(): String? {
        val account = currentAccount ?: return null
        return withContext(Dispatchers.IO) {
            try {
                val token = singleAccountApp?.acquireTokenSilent(arrayOf("User.Read"), account.authority)?.accessToken
                token?.let {
                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .url("https://graph.microsoft.com/v1.0/me/photo/\$value")
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        val byteArray = response.body?.bytes()
                        byteArray?.let {
                            // Save the image as a file and return the file path or base64 string
                            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                            val file = File(appContext.cacheDir, "profile_image.png")
                            FileOutputStream(file).use { out ->
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                            }
                            file.absolutePath
                        }
                    } else {
                        null
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun saveAuthResult(authResult: IAuthenticationResult) {
        preferences.edit().putString("accessToken", authResult.accessToken).apply()
        preferences.edit().putString("idToken", authResult.account?.idToken).apply()
        preferences.edit().putString("accountName", authResult.account?.username).apply()
    }

    private fun loadAuthResult() {
        val accessToken = preferences.getString("accessToken", null)
        val idToken = preferences.getString("idToken", null)
        val accountName = preferences.getString("accountName", null)
        if (accessToken != null && idToken != null && accountName != null) {
            // You can create a mock IAuthenticationResult and set it to currentAccount
            Log.d("MSALAuthManager", "Authentication result loaded from preferences")
        }
    }

    private fun clearAuthResult() {
        preferences.edit().remove("accessToken").apply()
        preferences.edit().remove("idToken").apply()
        preferences.edit().remove("accountName").apply()
    }
}
