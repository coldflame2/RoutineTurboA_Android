package com.app.routineturboa.data.onedrive

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.app.routineturboa.R
import com.microsoft.identity.client.*
import com.microsoft.identity.client.exception.MsalClientException
import com.microsoft.identity.client.exception.MsalException
import com.microsoft.identity.client.exception.MsalUiRequiredException
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MsalApp(context: Context) {
    private val tag = "MsalAuthManager"

    // Track initialization state with StateFlow
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> get() = _isInitialized

    private val appContext = context.applicationContext  // Store as application context
    private var singleAccountApp: ISingleAccountPublicClientApplication? = null
    var currentAccount: IAccount? = null

    init {
        createMsalApplication()
    }

    private fun createMsalApplication() {
        Log.d(tag, "Creating MSAL single account application")

        // Run MSAL client creation on a background thread
        CoroutineScope(Dispatchers.IO).launch {
            PublicClientApplication.createSingleAccountPublicClientApplication(
                appContext,
                R.raw.auth_config_single_account,
                getMsalClientListener()
            )
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

    /**
     * This function provides a listener that handles the success or failure of the MSAL client creation.
     */
    private fun getMsalClientListener():
            IPublicClientApplication.ISingleAccountApplicationCreatedListener {

        Log.d(tag, "Creating MSAL client listener")

        return object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {
            override fun onCreated(msalApplication: ISingleAccountPublicClientApplication) {
                Log.d(tag, "MSAL client created successfully")
                singleAccountApp = msalApplication
                _isInitialized.value = true // Update initialization state
            }

            override fun onError(exception: MsalException) {
                Log.e(tag, "MSAL client listener creation error: ${exception.message}")
                _isInitialized.value = false // Ensure initialization state is false on error
            }
        }
    }

    suspend fun waitForInitialization() {
        Log.d(tag, "Waiting for MSAL initialization...")
        isInitialized.first { initialized ->
            if (initialized) {
                Log.d(tag, "MSAL initialized successfully.")
                true // Breaks out of the `first` function when `initialized` is true
            } else {
                false
            }
        }
    }

    suspend fun getCurrentAccount(): IAccount? = suspendCancellableCoroutine { continuation ->
        Log.d(tag, "Getting current account...")

        singleAccountApp?.getCurrentAccountAsync(
            object : ISingleAccountPublicClientApplication.CurrentAccountCallback {
                override fun onAccountLoaded(activeAccount: IAccount?) {
                    Log.d(tag, "Current account loaded: ${activeAccount?.username}")
                    currentAccount = activeAccount
                    continuation.resume(activeAccount)
                }

                override fun onAccountChanged(priorAccount: IAccount?, newAccount: IAccount?) {
                    Log.d(tag, "Account changed: ${newAccount?.username}")
                    currentAccount = newAccount
                    continuation.resume(newAccount)
                }

                override fun onError(exception: MsalException) {
                    Log.e(tag, "Error getting current account", exception)
                    currentAccount = null
                    continuation.resumeWithException(exception)
                }
            }
        ) ?: run {
            // If singleAccountApp is null, resume with exception
            continuation.resumeWithException(
                IllegalStateException("MSAL client is not initialized.")
            )
        }
    }

    suspend fun signIn(activity: Activity): IAuthenticationResult {
        // Ensure MSAL is initialized
        waitForInitialization()

        // First, get the current account (call outside of suspendCoroutine)
        val account = getCurrentAccount()

        return suspendCoroutine { continuation ->
            if (account != null) {
                Log.d(tag, "Attempting silent token acquisition for account: ${account.username}")
                // Proceed with silent token acquisition
                acquireAuthenticationResultSilently(account, activity, continuation)
            } else {
                Log.d(tag, "No account available, proceeding with interactive login")
                signInAttempt(activity, continuation)
            }
        }
    }


    private fun signInAttempt(
        activity: Activity,
        continuation: Continuation<IAuthenticationResult>
    ) {
        val parameters = AcquireTokenParameters.Builder()
            .startAuthorizationFromActivity(activity)
            .withScopes(listOf("User.Read", "Files.Read"))
            .withCallback(object : AuthenticationCallback {
                override fun onSuccess(result: IAuthenticationResult) {
                    Log.d(tag, "Interactive login successful")
                    currentAccount = result.account
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

        singleAccountApp?.acquireToken(parameters) ?: run {
            continuation.resumeWithException(
                IllegalStateException("MSAL client is not initialized.")
            )
        }
    }

    private fun acquireAuthenticationResultSilently(
        account: IAccount,
        activity: Activity,
        continuation: Continuation<IAuthenticationResult>
    ) {
        val silentParameters = AcquireTokenSilentParameters.Builder()
            .forAccount(account)
            .withScopes(listOf("User.Read", "Files.Read"))
            .fromAuthority(account.authority)
            .withCallback(object : SilentAuthenticationCallback {
                override fun onSuccess(result: IAuthenticationResult) {
                    Log.d(tag, "Silent token acquisition successful")
                    currentAccount = result.account
                    continuation.resume(result)
                }

                override fun onError(exception: MsalException) {
                    Log.e(tag, "Silent token acquisition error: ${exception.message}")
                    handleSilentTokenError(exception, continuation, activity)
                }
            })
            .build()

        singleAccountApp?.acquireTokenSilentAsync(silentParameters) ?: run {
            continuation.resumeWithException(
                IllegalStateException("MSAL client is not initialized.")
            )
        }
    }

    private fun handleSilentTokenError(
        exception: MsalException,
        continuation: Continuation<IAuthenticationResult>,
        activity: Activity
    ) {
        when (exception) {
            is MsalUiRequiredException -> {
                Log.d(tag, "UI required exception, proceeding with interactive login")
                signInAttempt(activity, continuation)
            }
            is MsalClientException -> {
                if (exception.errorCode == MsalClientException.NO_CURRENT_ACCOUNT) {
                    Log.d(tag, "No current account, proceeding with interactive login")
                    currentAccount = null
                    signInAttempt(activity, continuation)
                } else {
                    Log.e(tag, "MsalClientException occurred: ${exception.errorCode}")
                    signOut {
                        signInAttempt(activity, continuation)
                    }
                }
            }
            else -> {
                Log.e(tag, "Unhandled exception during silent token acquisition: ${exception.message}")
                continuation.resumeWithException(exception)
            }
        }
    }

    fun signOut(onComplete: () -> Unit) {
        if (currentAccount == null) {
            Log.d(tag, "No account to sign out.")
            onComplete()
            return
        }

        singleAccountApp?.signOut(object : ISingleAccountPublicClientApplication.SignOutCallback {
            override fun onSignOut() {
                Log.d(tag, "Signed out successfully")
                currentAccount = null
                onComplete()
            }

            override fun onError(exception: MsalException) {
                Log.e(tag, "Sign out error: ${exception.message}")
                currentAccount = null
                onComplete()
            }
        }) ?: run {
            Log.e(tag, "MSAL client is not initialized.")
            onComplete()
        }
    }

    /**
     * This function performs a silent token acquisition for fetching the profile image.
     * If a valid access token is found in the cache, it is returned.
     * Otherwise, it attempts to use a refresh token to obtain a new access token.
     *
     * @param account The account for which the token is being acquired.
     * @return The access token, or null if an error occurs.
     */
    private suspend fun acquireTokenSilently(account: IAccount): String? = suspendCoroutine { continuation ->
        Log.d(tag, "Acquiring tokens silently for profile image...")
        val silentParameters = AcquireTokenSilentParameters.Builder()
            .forAccount(account)
            .withScopes(listOf("User.Read", "Files.Read"))
            .fromAuthority(account.authority)
            .withCallback(object : SilentAuthenticationCallback {
                override fun onSuccess(result: IAuthenticationResult) {
                    continuation.resume(result.accessToken)
                }

                override fun onError(exception: MsalException) {
                    Log.e(tag, "Error acquiring token silently: ${exception.message}. Account: $account")
                    continuation.resumeWithException(exception)
                }
            })
            .build()

        singleAccountApp?.acquireTokenSilentAsync(silentParameters) ?: run {
            continuation.resumeWithException(
                IllegalStateException("MSAL client is not initialized.")
            )
        }
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
        val account = getCurrentAccount() ?: return null

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
}
