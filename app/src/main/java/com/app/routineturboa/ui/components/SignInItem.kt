package com.app.routineturboa.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.routineturboa.MainActivity
import com.app.routineturboa.MyApplication
import com.app.routineturboa.services.MsalAuthManager
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAccount
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.exception.MsalException
import kotlinx.coroutines.launch

@Composable
fun SignInItem() {
    val context = LocalContext.current
    val msalAuthManager = MyApplication.instance.msalAuthManager
    var account by remember { mutableStateOf<IAccount?>(null) }
    var isSigningIn by remember { mutableStateOf(false) }
    var profilePicUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        msalAuthManager.getCurrentAccount { result ->
            account = result
            if (result != null) {
                Log.d("SignInItem", "This is LaunchedEffect.")
                launch {
                    profilePicUrl = msalAuthManager.getProfileImageUrl()
                }

            }
        }
    }

    val onSignInSuccess = { result: IAuthenticationResult ->
        msalAuthManager.saveAuthResult(result)
        account = result.account
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = !isSigningIn,
                onClick = {
                    if (account == null) {
                        isSigningIn = true
                        signIn(msalAuthManager, context as MainActivity) { result ->
                            onSignInSuccess(result)
                            isSigningIn = false
                        }
                    } else {
                        // Handle signed-in state click (e.g., show profile or sign out)
                    }
                }
            )
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = if (isSigningIn) "Signing in..." else account?.username ?: "Sign in",
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(16.dp))

        if (account != null && profilePicUrl != null) {
            Log.d("SignInItem", "Profile pic URL: $profilePicUrl")
            AsyncImage(
                model = profilePicUrl,
                contentDescription = "Profile Picture",
                modifier = Modifier.size(24.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Picture",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

private fun signIn(msalAuthManager: MsalAuthManager, activity: MainActivity, onSignInSuccess: (IAuthenticationResult) -> Unit) {
    msalAuthManager.signIn(
        activity,
        object : AuthenticationCallback {
            override fun onSuccess(result: IAuthenticationResult) {
                onSignInSuccess(result)
            }

            override fun onError(exception: MsalException) {
                Log.e("SignInItem", "Sign-in error: ${exception.message}")
                // Handle error (e.g., show error message to user)
            }

            override fun onCancel() {
                Log.d("SignInItem", "Sign-in canceled")
                // Handle cancellation (e.g., reset UI state)
            }
        }
    )
}