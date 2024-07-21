//
//import android.util.Log
//import android.widget.Toast
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.core.animateFloatAsState
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ExitToApp
//import androidx.compose.material.icons.filled.KeyboardArrowDown
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.rotate
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.lifecycleScope
//import coil.compose.AsyncImage
//import com.app.routineturboa.MainActivity
//import com.app.routineturboa.MyApplication
//import com.app.routineturboa.services.MsalAuthManager
//import com.microsoft.identity.client.AuthenticationCallback
//import com.microsoft.identity.client.IAccount
//import com.microsoft.identity.client.IAuthenticationResult
//import com.microsoft.identity.client.ISingleAccountPublicClientApplication
//import com.microsoft.identity.client.exception.MsalException
//import kotlinx.coroutines.launch
//
//@Composable
//fun SignInItemOriginal() {
//    val context = LocalContext.current
//
//    var signInButtonText by remember { mutableStateOf("Sign in") }
//    var isExpanded by remember { mutableStateOf(true) }
//
//    val msalAuthManager by remember { mutableStateOf(MyApplication.instance.msalAuthManager) }
//
//    var account by remember { mutableStateOf<IAccount?>(null) }
//    var isSigningIn by remember { mutableStateOf(false) }
//    var profilePicUrl by remember { mutableStateOf<String?>(null) }
//    val signInPhoto by remember { mutableStateOf(profilePicUrl) }
//    var isSignedIn by remember { mutableStateOf(false) }
//    var currentAccount by remember { mutableStateOf<IAccount?>(null) }
//
//    val coroutineScope = rememberCoroutineScope()
//
//    val rotationState by animateFloatAsState(
//        targetValue = if (isExpanded) 180f else 0f, label = ""
//    )
//
//    LaunchedEffect(Unit, isSignedIn, currentAccount) {
//        Log.d("SignedInAccount", "LaunchedEffect called")
//        msalAuthManager.onInitialized {
//            currentAccount = msalAuthManager.currentAccount
//            if (currentAccount != null) {
//                val username = currentAccount!!.username
//                signInButtonText = username.split("@").firstOrNull() ?: username
//
//                coroutineScope.launch {
//                    profilePicUrl = msalAuthManager.getProfileImageUrl()
//                    Log.d("SignInItem", "Profile pic URL: $profilePicUrl")
//                }
//            } else {
//                Log.d("SignedInAccount", "No account is signed in.")
//            }
//        }
//    }
//    val onSignInSuccess: suspend (IAuthenticationResult) -> Unit = { result ->
//        account = result.account
//        val username = result.account.username
//        signInButtonText = username.split("@").firstOrNull() ?: username
//
//        profilePicUrl = msalAuthManager.getProfileImageUrl()
//        Log.d("SignInItem", "Profile pic URL: $profilePicUrl")
//    }
//
//    Column {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier
//                .fillMaxWidth()
//                .clickable(
//                    enabled = !isSigningIn,
//                    onClick = {
//                        if (account == null) {
//                            isSigningIn = true
//                            signIn(msalAuthManager, context as MainActivity) { result ->
//                                onSignInSuccess(result)
//                                isSigningIn = false
//                                isSignedIn = true
//                            }
//                        } else {
//                            isExpanded = !isExpanded
//                        }
//                    }
//                )
//                .padding(16.dp)
//        ) {
//            Icon(
//                imageVector = Icons.Default.Person,
//                contentDescription = null,
//                modifier = Modifier.size(24.dp)
//            )
//            Spacer(modifier = Modifier.width(16.dp))
//
//            Text(
//                text = signInButtonText,
//                modifier = Modifier.weight(1f)
//            )
//
//            Spacer(modifier = Modifier.width(16.dp))
//
//
//            Icon(
//                imageVector = Icons.Default.KeyboardArrowDown,
//                contentDescription = if (isExpanded) "Collapse" else "Expand",
//                modifier = Modifier.rotate(rotationState)
//            )
//
//            AsyncImage(
//                model = signInPhoto,
//                contentDescription = "Profile Picture",
//                modifier = Modifier
//                    .size(30.dp)
//                    .clip(CircleShape),
//                contentScale = ContentScale.Crop
//            )
//
//        }
//
//        AnimatedVisibility(visible = isExpanded && account != null) {
//
//            Column(modifier = Modifier
//                .padding(horizontal = 56.dp, vertical = 6.dp)
//            ) {
//                Row(modifier = Modifier
//                    .clickable {
//                        signOut(msalAuthManager) {
//                            signInButtonText = "Sign in"
//                            isExpanded = false
//                            account = null
//                            profilePicUrl = null
//                            isSignedIn = false
//                        }
//                    }
//                ) {
//                    Icon(
//                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
//                        contentDescription = null,
//                        modifier = Modifier.size(24.dp),
//                        tint = MaterialTheme.colorScheme.onPrimaryContainer
//                    )
//
//                    Spacer(modifier = Modifier.width(10.dp))
//
//                    Text(text = "Sign out")
//                }
//            }
//        }
//    }
//}
//
//fun signInOriginal(
//    msalAuthManager: MsalAuthManager,
//    activity: MainActivity,
//    onSignInSuccess: suspend (IAuthenticationResult) -> Unit
//) {
//    msalAuthManager.signIn(
//        activity,
//        object : AuthenticationCallback {
//            override fun onSuccess(result: IAuthenticationResult) {
//                // Launch a coroutine to call the suspending onSignInSuccess function
//                (activity as MainActivity).lifecycleScope.launch {
//                    onSignInSuccess(result)
//                }
//            }
//
//            override fun onError(exception: MsalException) {
//                Log.e("SignInItem", "Sign-in error: ${exception.message}")
//                Toast.makeText(activity, "Sign-in error: ${exception.message}", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onCancel() {
//                Log.d("SignInItem", "Sign-in canceled")
//                // Handle cancellation (e.g., reset UI state)
//            }
//        }
//    )
//}
//
//private fun signOutOriginal(msalAuthManager: MsalAuthManager, onSignOutSuccess: () -> Unit) {
//    msalAuthManager.signOut(object : ISingleAccountPublicClientApplication.SignOutCallback {
//        override fun onSignOut() {
//            Log.d("SignInItem", "Signed out successfully")
//            onSignOutSuccess()
//        }
//
//        override fun onError(exception: MsalException) {
//            Log.e("SignInItem", "Sign-out error: ${exception.message}")
//            // Handle error (e.g., show error message to user)
//        }
//    })
//}
