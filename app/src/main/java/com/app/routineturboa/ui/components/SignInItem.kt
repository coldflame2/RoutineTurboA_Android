package com.app.routineturboa.ui.components

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.routineturboa.MainActivity
import com.app.routineturboa.RoutineTurboApplication
import kotlinx.coroutines.launch

@Composable
fun SignInItem() {
    val tag = "SignInItem"
    val context = LocalContext.current
    val msalAuthManager = remember { RoutineTurboApplication.instance.msalAuthManager }
    var isSignedIn by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val isMsalInitialized by msalAuthManager.isInitialized.collectAsState()

    LaunchedEffect(isMsalInitialized, isSignedIn) {
        if (isMsalInitialized) {
            Log.d(
                "SignInItem",
                "LaunchedEffect: msalAuthManager is initialized. Getting current account and profile image..."
            )

            msalAuthManager.getCurrentAccount { account ->
                isSignedIn = account != null
                username = account?.username ?: ""

                //strip username of @ if present
                if (username.contains("@")) {
                    username = username.substringBefore("@")
                }

                Log.d(tag, "isSignedIn:$isSignedIn and username:$username")

                if (account != null) {
                    Log.d(tag, "LaunchedEffect: account is not null. Getting profile image URL.")
                    coroutineScope.launch {
                        profileImageUrl = msalAuthManager.getProfileImageUrl()
                    }
                } else {
                    Log.d(tag, "LaunchedEffect: account is null. No profile image URL.")
                }
            }
        }

        else {
            Log.d(tag, "LaunchedEffect: msalAuthManager is not initialized yet.")
        }
    }

    fun handleSignIn() {
        coroutineScope.launch {
            try {
                val result = msalAuthManager.signIn(context as MainActivity)
                isSignedIn = true
                username = result.account.username
                profileImageUrl = msalAuthManager.getProfileImageUrl()
            } catch (e: Exception) {
                Log.e("SignInItem", "Sign-in error", e)
                Toast.makeText(context, "Sign-in error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun handleSignOut() {
        coroutineScope.launch {
            try {
                val success = msalAuthManager.signOutSuspend()
                if (success) {
                    isSignedIn = false
                    username = ""
                    profileImageUrl = null
                    isExpanded = false
                }
                // Always show a message, success or failure
                Toast.makeText(context, if (success) "Signed out successfully" else "Sign-out failed", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("SignInItem", "Sign-out error", e)
                Toast.makeText(context, "Sign-out error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (isSignedIn) {
                        isExpanded = !isExpanded
                    } else {
                        Log.d(tag, "Sign in button clicked")
                        coroutineScope.launch {
                            handleSignIn()
                        }
                    }
                }
                .padding(16.dp)
        ) {
            if (isSignedIn) {
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(15.dp))

            Text(
                text = if (isSignedIn) username else "Sign in",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (isSignedIn) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier.size(24.dp)
                )
            }


        }

        AnimatedVisibility(visible = isSignedIn && isExpanded) {
            Column(
                modifier = Modifier
                    .padding(start = 56.dp, end = 16.dp, bottom = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            coroutineScope.launch {
                                handleSignOut()
                                isSignedIn = false
                                username = ""
                                profileImageUrl = null
                                isExpanded = false
                            }
                        }
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Sign out",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Sign out",
                        style = MaterialTheme.typography.bodyLarge
                    ) // Text
                } // Row

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { Log.d("SignInItem", "Sync Clicked")
                        }
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Sign out",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Sync",
                        style = MaterialTheme.typography.bodyLarge
                    ) // Text
                } // Row

            } // Column



        } // animated visibility4
    }
}
