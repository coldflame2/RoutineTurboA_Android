package com.app.routineturboa.ui.components

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.app.routineturboa.MainActivity
import com.app.routineturboa.services.MSALAuthManager
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAccount
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.exception.MsalException

@Composable
fun SignInItem() {
    val context = LocalContext.current
    val msalAuthManager = remember { MSALAuthManager.getInstance(context) }
    var account by remember { mutableStateOf<IAccount?>(null) }

    var authenticationResult by remember { mutableStateOf<IAuthenticationResult?>(null) }

    val onSignInSuccess = { result: IAuthenticationResult ->
        authenticationResult = result
        msalAuthManager.saveAuthResult(result)
    }

    val coroutineScope = rememberCoroutineScope()
    var isSigningIn by remember { mutableStateOf(false) }
    var profilePicUrl by remember { mutableStateOf<String?>(null) }

    val preferences: SharedPreferences = context.getSharedPreferences("msal_prefs", Context.MODE_PRIVATE)
    val accountName = preferences.getString("accountName", null)
    Log.d("SignInItem", "Current account: $accountName")

    LaunchedEffect(Unit) {
        msalAuthManager.getCurrentAccount { result ->
            account = result
        }
    }

    val isClickable = authenticationResult == null && !isSigningIn

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {})
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = account?.username ?: "Sign-in",

            modifier = if (isClickable) {
                Modifier.clickable {

                    if (!isSigningIn) {
                        Toast.makeText(context, "Signing in", Toast.LENGTH_LONG).show()
                        Log.d("SignInItem", "Sign-in button clicked")

                        isSigningIn = true

                        msalAuthManager.singleAccountApp?.getCurrentAccountAsync(object :
                            ISingleAccountPublicClientApplication.CurrentAccountCallback {
                            override fun onAccountLoaded(activeAccount: IAccount?) {
                                if (activeAccount != null) {
                                    Log.d("SignInItem", "An account is already signed in.")

                                    msalAuthManager.signOut(object :
                                        ISingleAccountPublicClientApplication.SignOutCallback {

                                        override fun onSignOut() {
                                            Log.d(
                                                "SignInItem",
                                                "Signed out successfully, now signing in again."
                                            )
                                            signIn(
                                                msalAuthManager,
                                                context as MainActivity,
                                                onSignInSuccess
                                            )
                                        }

                                        override fun onError(exception: MsalException) {
                                            Log.e(
                                                "SignInItem",
                                                "Sign-out error: ${exception.message}"
                                            )
                                            isSigningIn = false
                                        }
                                    })

                                } else {
                                    Log.d("SignInItem", "No account is signed in.")
                                    signIn(
                                        msalAuthManager,
                                        context as MainActivity,
                                        onSignInSuccess
                                    )
                                }
                            }

                            override fun onAccountChanged(
                                priorAccount: IAccount?,
                                currentAccount: IAccount?
                            ) {
                                Log.d("SignInItem", "Account changed: ${currentAccount?.username}")
                            }

                            override fun onError(exception: MsalException) {
                                Log.e(
                                    "SignInItem",
                                    "Error loading current account: ${exception.message}"
                                )
                                isSigningIn = false
                            }
                        })
                    }
                }

                // If no auth result
            } else {
                Modifier
            }
        )

        Spacer(modifier = Modifier.width(16.dp))

        if (accountName != null && profilePicUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(profilePicUrl),
                contentDescription = "Profile Picture",
                modifier = Modifier.size(10.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.AddAPhoto,
                contentDescription = "Profile Picture",
                modifier = Modifier.size(10.dp)
            )
        }
    }
}

fun signIn(msalAuthManager: MSALAuthManager, activity: MainActivity, onSignInSuccess: (IAuthenticationResult) -> Unit) {

    Log.d("SignInItem", "Starting sign-in")

    msalAuthManager.signIn(
        activity, object : AuthenticationCallback {

        override fun onSuccess(result: IAuthenticationResult) {
            Log.d("SignInItem", "Sign-in successful")
            Toast.makeText(activity, "Sign-in successful", Toast.LENGTH_SHORT).show()
            onSignInSuccess(result)
        }

        override fun onError(exception: MsalException) {
            Log.e("SignInItem", "Sign-in error: ${exception.message}")
        }

        override fun onCancel() {
            Log.d("SignInItem", "Sign-in canceled")
        }
    })
}
