package com.app.routineturboa.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.launch

@Composable
fun SignInButton(
    msalAuthManager: MSALAuthManager,
    authenticationResult: IAuthenticationResult?,
    onSignInSuccess: (IAuthenticationResult) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isSigningIn by remember { mutableStateOf(false) }
    var profilePicUrl by remember { mutableStateOf<String?>(null) }
    var currentAuthResult by remember { mutableStateOf(authenticationResult) }

    // Fetch profile image URL when authentication result changes
    LaunchedEffect(currentAuthResult) {
        currentAuthResult?.let {
            coroutineScope.launch {
                profilePicUrl = msalAuthManager.getProfileImageUrl()
                Log.d("SignInButton", "Profile image URL fetched: $profilePicUrl")
            }
        }
    }

    Button(
        onClick = {
            Log.d("SignInButton", "Sign-in button clicked")
            isSigningIn = true
            msalAuthManager.singleAccountApp?.getCurrentAccountAsync(object : ISingleAccountPublicClientApplication.CurrentAccountCallback {
                override fun onAccountLoaded(activeAccount: IAccount?) {
                    if (activeAccount != null) {
                        msalAuthManager.signOut(object : ISingleAccountPublicClientApplication.SignOutCallback {
                            override fun onSignOut() {
                                Log.d("SignInButton", "Signed out successfully, now signing in again.")
                                signIn(msalAuthManager, context as MainActivity, onSignInSuccess)
                            }

                            override fun onError(exception: MsalException) {
                                Log.e("SignInButton", "Sign-out error: ${exception.message}")
                                isSigningIn = false
                            }
                        })
                    } else {
                        signIn(msalAuthManager, context as MainActivity, onSignInSuccess)
                    }
                }

                override fun onAccountChanged(priorAccount: IAccount?, currentAccount: IAccount?) {
                    Log.d("SignInButton", "Account changed: ${currentAccount?.username}")
                }

                override fun onError(exception: MsalException) {
                    Log.e("SignInButton", "Error loading current account: ${exception.message}")
                    isSigningIn = false
                }
            })
        },
        enabled = !isSigningIn // Disable button during sign-in process
    ) {
        Row {
            if (currentAuthResult != null && profilePicUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(profilePicUrl),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(24.dp)
                )
                Text(text = "Signed In") // Show "Signed In" when signed in
            } else {
                Text(text = "Sign In") // Show "Sign In" when not signed in
            }
        }
    }
}

fun signIn(msalAuthManager: MSALAuthManager, activity: MainActivity, onSignInSuccess: (IAuthenticationResult) -> Unit) {
    msalAuthManager.signIn(activity, object : AuthenticationCallback {

        override fun onSuccess(result: IAuthenticationResult) {
            Log.d("SignInButton", "Sign-in successful")
            onSignInSuccess(result)
        }

        override fun onError(exception: MsalException) {
            Log.e("SignInButton", "Sign-in error: ${exception.message}")
        }

        override fun onCancel() {
            Log.d("SignInButton", "Sign-in canceled")
        }
    })
}
