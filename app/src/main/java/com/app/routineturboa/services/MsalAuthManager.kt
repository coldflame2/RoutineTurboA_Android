package com.app.routineturboa.services

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.app.routineturboa.R
import com.microsoft.identity.client.AcquireTokenParameters
import com.microsoft.identity.client.AcquireTokenSilentParameters
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

class MsalAuthManager(context: Context) {

    private val appContext = context.applicationContext
    private val preferences: SharedPreferences = appContext.getSharedPreferences("msal_prefs", Context.MODE_PRIVATE)
    var singleAccountApp: ISingleAccountPublicClientApplication? = null
    var currentAccount: IAccount? = null

    companion object {
        @Volatile
        private var INSTANCE: MsalAuthManager? = null

        fun getInstance(context: Context): MsalAuthManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: MsalAuthManager(context).also { INSTANCE = it }
            }
    }

    init {
        Log.d("MSALAuthManager", "Initializing MSALAuthManager")
        createMsalApplication()
    }

    private fun createMsalApplication() {
        Log.d("MSALAuthManager", "Creating MSAL single account application")

        PublicClientApplication.createSingleAccountPublicClientApplication(
            appContext,
            R.raw.auth_config_single_account,
            getMsalClientListener()
        )
    }

    // Prepares a response handler for when the MSAL is set up.
    private fun getMsalClientListener(): IPublicClientApplication.ISingleAccountApplicationCreatedListener {
        return object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {

            override fun onCreated(application: ISingleAccountPublicClientApplication) {
                Log.d("MSALAuthManager", "MSAL client created successfully")
                singleAccountApp = application
                checkCurrentAccount()
            }

            override fun onError(exception: MsalException) {
                Log.e("MSALAuthManager", "MSAL client creation error: ${exception.message}")
            }
        }
    }


    private fun checkCurrentAccount() {
        Log.d("MSALAuthManager", "Checking current account")
        singleAccountApp?.getCurrentAccountAsync(
            object : ISingleAccountPublicClientApplication.CurrentAccountCallback {
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
                    Log.d("MSALAuthManager", "Account changed: ${currentAccount?.username}")
                    this@MsalAuthManager.currentAccount = currentAccount
                }

                override fun onError(exception: MsalException) {
                    Log.e("MSALAuthManager", "Error loading current account: ${exception.message}")
                }
            }
        )
    }

    fun getCurrentAccount(callback: (IAccount?) -> Unit) {
        singleAccountApp?.getCurrentAccountAsync(object : ISingleAccountPublicClientApplication.CurrentAccountCallback {
            override fun onAccountLoaded(activeAccount: IAccount?) {
                Log.d("MSALAuthManager", "Current account: ${activeAccount?.username}")
                callback(activeAccount)
            }

            override fun onAccountChanged(priorAccount: IAccount?, currentAccount: IAccount?) {
                Log.d("MSALAuthManager", "Account changed: ${currentAccount?.username}")
                callback(currentAccount)
            }

            override fun onError(exception: MsalException) {
                Log.e("MSALAuthManager", "Error getting current account", exception)
                callback(null)
            }
        })
    }

    fun signIn(activity: Activity, callback: AuthenticationCallback) {
        if (singleAccountApp != null) {
            Log.d("MSALAuthManager", "Attempting to sign in")
            val parameters = AcquireTokenParameters.Builder()
                .startAuthorizationFromActivity(activity)
                .withScopes(listOf("User.Read", "Files.Read"))
                .withCallback(callback)
                .build()

            singleAccountApp?.acquireToken(parameters)
        } else {
            Log.e("MSALAuthManager", "MSAL client is not initialized")
        }
    }

    fun signOut(callback: ISingleAccountPublicClientApplication.SignOutCallback) {
        Log.d("MSALAuthManager", "Attempting to sign out")
        singleAccountApp?.signOut(callback)
        clearAuthResult() // Clear saved authentication result
        Log.d("MSALAuthManager", "Signed out and cleared auth result")
    }

    suspend fun getProfileImageUrl(): String? {
        val account = currentAccount ?: return null
        Log.d("MSALAuthManager", "Fetching profile image URL for account: ${account.username}")

        return withContext(Dispatchers.IO) {
            try {
                val parameters = AcquireTokenSilentParameters.Builder()
                    .withScopes(listOf("User.Read"))
                    .forAccount(account)
                    .fromAuthority(account.authority)
                    .build()

                val result = singleAccountApp?.acquireTokenSilent(parameters)
                val token = result?.accessToken

                token?.let {
                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .url("https://graph.microsoft.com/v1.0/me/photo/\$value")
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                    val response = client.newCall(request).execute()
                    Log.d("MSALAuthManager", "Account authority: ${account.authority}")
                    if (response.isSuccessful) {
                        val byteArray = response.body?.bytes()
                        byteArray?.let {
                            // Save the image as a file and return the file path or base64 string
                            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                            val file = File(appContext.cacheDir, "profile_image.png")
                            FileOutputStream(file).use { out ->
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                            }
                            Log.d("MSALAuthManager", "Profile image saved at: ${file.absolutePath}")
                            file.absolutePath
                        }
                    } else {
                        Log.e("MSALAuthManager", "Failed to fetch profile image, response code: ${response.code}")
                        null
                    }
                }
            } catch (e: Exception) {
                Log.e("MSALAuthManager", "Error fetching profile image: ${e.message}")
                e.printStackTrace()
                null
            }
        }
    }

    fun saveAuthResult(authResult: IAuthenticationResult) {
        Log.d("MSALAuthManager", "Saving authentication result for account: ${authResult.account.username}")
        preferences.edit().putString("accessToken", authResult.accessToken).apply()
        preferences.edit().putString("idToken", authResult.account.idToken).apply()
        preferences.edit().putString("accountName", authResult.account.username).apply()
    }

    private fun loadAuthResult() {
        Log.d("MSALAuthManager", "Loading authentication result from preferences")
        val accessToken = preferences.getString("accessToken", null)
        val idToken = preferences.getString("idToken", null)
        val accountName = preferences.getString("accountName", null)

        if (accessToken != null && idToken != null && accountName != null) {
            // Using stored information to set current account
            Log.d("MSALAuthManager", "Loaded account: $accountName")
            currentAccount = object : IAccount {
                override fun getId(): String = "storedId"
                override fun getUsername(): String = accountName
                override fun getAuthority(): String = "storedAuthority"
                override fun getClaims(): MutableMap<String, *>? = null
                override fun getIdToken(): String = idToken
                override fun getTenantId(): String = "storedTenantId"
            }
        } else {
            Log.d("MSALAuthManager", "No stored authentication result found")
        }
    }

    private fun clearAuthResult() {
        Log.d("MSALAuthManager", "Clearing authentication result from preferences")
        preferences.edit().remove("accessToken").apply()
        preferences.edit().remove("idToken").apply()
        preferences.edit().remove("accountName").apply()
    }
}
