package com.app.routineturboa.data.onedrive

import android.app.Activity
import android.content.Context
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class MsalApp(context: Context) {
    var tag = "MsalAuthManager"

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> get() = _isInitialized

    private val appContext = context.applicationContext
    var singleAccountApp: ISingleAccountPublicClientApplication? = null
    var currentAccount: IAccount? = null

    init {
        Log.d(tag, "init: Creating Msal Application.")
        createMsalApplication()
    }

    suspend fun initialize() {
        Log.d(tag, "MsalAuthManager initialize function...")

        // Try to load the current account (token cache)
        try {
            currentAccount = getCurrentAccountSuspend()
            if (currentAccount != null) {
                Log.d(tag, "MsalAuthManager: User is already logged in: ${currentAccount?.username}")
            } else {
                Log.d(tag, "MsalAuthManager: No user is logged in.")
            }
        } catch (e: Exception) {
            Log.e(tag, "MsalAuthManager: Error getting current account", e)
        }

        _isInitialized.value = true
    }

    private fun onInitialized(callback: () -> Unit) {
        Log.d(tag, "MsalAuthManager onInitialized")
    }

    private fun createMsalApplication() {
        Log.d(tag, "Creating MSAL single account application")

        PublicClientApplication.createSingleAccountPublicClientApplication(
            appContext,
            R.raw.auth_config_single_account,
            getMsalClientListener()
        )
    }

    /**
     * This function provides a listener that handles the success or failure of the MSAL client creation.
     */
    private fun getMsalClientListener(): IPublicClientApplication.ISingleAccountApplicationCreatedListener {
        Log.d(tag, "Creating MSAL client listener")

        return object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {
            override fun onCreated(msalApplication: ISingleAccountPublicClientApplication) {
                Log.d(tag, "MSAL client created successfully")
                singleAccountApp = msalApplication
                _isInitialized.value = true
            }

            override fun onError(exception: MsalException) {
                Log.e(tag, "MSAL client creation error: ${exception.message}")
            }
        }
    }

    /**
     * This is a suspending function that wraps getCurrentAccountAsync in a coroutine.
     */
    private suspend fun getCurrentAccountSuspend(): IAccount? = suspendCoroutine { continuation ->
        Log.d(tag, "Getting current account using suspend function.")

        singleAccountApp?.getCurrentAccountAsync(
            object : ISingleAccountPublicClientApplication.CurrentAccountCallback {
                override fun onAccountLoaded(activeAccount: IAccount?) {
                    Log.d(tag, "Current account loaded: ${activeAccount?.username}")
                    continuation.resume(activeAccount)
                }

                override fun onAccountChanged(priorAccount: IAccount?, currentAccount: IAccount?) {
                    Log.d(tag, "Account changed: ${currentAccount?.username}")
                    continuation.resume(currentAccount)
                }

                override fun onError(exception: MsalException) {
                    Log.e(tag, "Error getting current account", exception)
                    continuation.resumeWithException(exception)
                }
            }
        )
    }

    fun getCurrentAccount(callback: ((IAccount?) -> Unit)?) {
        singleAccountApp?.getCurrentAccountAsync(
            object : ISingleAccountPublicClientApplication.CurrentAccountCallback {
                override fun onAccountLoaded(activeAccount: IAccount?) {
                    if (activeAccount != null) {
                        Log.d(tag, "User is logged in: ${activeAccount.username}")
                        currentAccount = activeAccount
                        callback?.invoke(activeAccount)
                    } else {
                        Log.d(tag, "No user is logged in.")
                        callback?.invoke(null)
                    }
                }

                override fun onAccountChanged(priorAccount: IAccount?, currentAccount: IAccount?) {
                    Log.d(tag, "Account changed.")
                    this@MsalApp.currentAccount = currentAccount
                    callback?.invoke(currentAccount)
                }

                override fun onError(exception: MsalException) {
                    Log.e(tag, "Error getting current account", exception)
                    callback?.invoke(null)
                }
            }
        )
    }

    private fun checkCurrentAccount() {
        Log.d(tag, "Checking current account...")

        singleAccountApp?.getCurrentAccountAsync(
            object : ISingleAccountPublicClientApplication.CurrentAccountCallback {
                override fun onAccountLoaded(activeAccount: IAccount?) {
                    if (activeAccount != null) {
                        Log.d(tag, "Current Account Found: ${activeAccount.username}")
                        currentAccount = activeAccount
                    } else {
                        Log.d(tag, "No account is signed in.")
                    }
                }

                override fun onAccountChanged(priorAccount: IAccount?, currentAccount: IAccount?) {
                    Log.e(tag, "Account changed: ${currentAccount?.username}")
                    this@MsalApp.currentAccount = currentAccount
                }

                override fun onError(exception: MsalException) {
                    Log.e(tag, "Error loading current account: ${exception.message}")
                }
            }
        )
    }

    suspend fun signIn(activity: Activity): IAuthenticationResult {
        return suspendCoroutine { continuation ->
            fun signInAttempt() {
                val parameters = AcquireTokenParameters.Builder()
                    .startAuthorizationFromActivity(activity)
                    .withScopes(listOf("User.Read", "Files.Read"))
                    .withCallback(object : AuthenticationCallback {
                        override fun onSuccess(result: IAuthenticationResult) {
                            Log.d(tag, "Interactive login successful")
                            continuation.resume(result)
                        }

                        override fun onError(exception: MsalException) {
                            Log.e(tag, "Interactive login error: ${exception.message}")
                            continuation.resumeWithException(exception)
                        }

                        override fun onCancel() {
                            Log.d(tag, "Interactive login canceled")
                            continuation.resumeWithException(Exception("Login canceled"))
                        }
                    })
                    .build()

                singleAccountApp?.acquireToken(parameters)
            }

            // Try to acquire token silently first
            val account = currentAccount
            if (account != null) {
                val silentParameters = AcquireTokenSilentParameters.Builder()
                    .forAccount(account)
                    .withScopes(listOf("User.Read", "Files.Read"))
                    .fromAuthority(account.authority)
                    .withCallback(object : AuthenticationCallback {
                        override fun onSuccess(result: IAuthenticationResult) {
                            Log.d(tag, "Silent token acquisition successful")
                            continuation.resume(result)
                        }

                        override fun onError(exception: MsalException) {
                            Log.e(tag, "Silent token acquisition error, falling back to interactive login: ${exception.message}")
                            // If silent token acquisition fails, attempt interactive login
                            signInAttempt()
                        }

                        override fun onCancel() {
                            Log.e(tag, "Silent token acquisition canceled")
                            continuation.resumeWithException(Exception("Silent token acquisition canceled"))
                        }
                    })
                    .build()

                singleAccountApp?.acquireTokenSilentAsync(silentParameters)
            } else {
                // No account available, proceed with interactive login
                signInAttempt()
            }
        }
    }

    fun signOut(onComplete: () -> Unit) {
        singleAccountApp?.signOut(object : ISingleAccountPublicClientApplication.SignOutCallback {
            override fun onSignOut() {
                Log.d(tag, "Signed out successfully")
                onComplete()
            }

            override fun onError(exception: MsalException) {
                Log.e(tag, "Sign out error: ${exception.message}")
                onComplete()
            }
        })
    }

    // New suspend function that wraps the regular signOut
    suspend fun signOutSuspend(): Boolean = suspendCoroutine { continuation ->
        signOut {
            continuation.resume(true)
        }
    }

    /**
     * This function performs a silent token acquisition. If a valid access token is found in the cache, it is returned.
     * Otherwise, it attempts to use a refresh token to obtain a new access token.
     *
     * @param account The account for which the token is being acquired.
     * @return The access token, or null if an error occurs.
     */
    suspend fun acquireTokenSilently(account: IAccount): String? = suspendCoroutine { continuation ->
        Log.d(tag, "acquiring tokens silently...")
        val parameters = AcquireTokenSilentParameters.Builder()
            .forAccount(account)
            .fromAuthority(account.authority)
            .withScopes(listOf("User.Read", "Files.Read"))
            .withCallback(object : AuthenticationCallback {
                override fun onSuccess(result: IAuthenticationResult) {
                    continuation.resume(result.accessToken)
                }

                override fun onError(exception: MsalException) {
                    Log.e(tag, "Error acquiring token silently $exception. LocalAccountID: $account")
                    continuation.resumeWithException(exception)
                }

                override fun onCancel() {
                    Log.e(tag, "Token acquisition cancelled")
                    continuation.resumeWithException(Exception("Token acquisition cancelled"))
                }
            })
            .build()

        singleAccountApp?.acquireTokenSilentAsync(parameters)
    }


    private fun saveImageToFile(byteArray: ByteArray): String {
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        val file = File(appContext.cacheDir, "profile_image.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file.absolutePath
    }

    suspend fun getProfileImageUrl(): String? {
        Log.d(tag, "Getting profile image URL...")
        val account = currentAccount ?: return null

        return withContext(Dispatchers.IO) {
            try {
                val token = acquireTokenSilently(account) ?: return@withContext null
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://graph.microsoft.com/v1.0/me/photo/\$value")
                    .addHeader("Authorization", "Bearer $token")
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val byteArray = response.body?.bytes()
                    byteArray?.let {
                        val localProfileImagePath = saveImageToFile(it)
                        Log.d(tag, "Local profile image path: $localProfileImagePath")
                        localProfileImagePath  // this is the value that the function returns
                    }
                } else {
                    Log.e(tag, "Failed to fetch profile image, response code: ${response.code}")
                    null
                }

            } catch (e: Exception) {
                Log.e(tag, "Error fetching profile image: ${e.message}")
                e.printStackTrace()
                null
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: MsalApp? = null

        // Returns the single instance of this class, creating it if necessary.
        fun getInstance(context: Context): MsalApp =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: MsalApp(context).also { INSTANCE = it }
            }
    }

}
