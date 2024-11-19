package com.app.routineturboa.ui.reusable.others

import android.app.Activity
import android.content.Context
import android.util.Log
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.app.routineturboa.core.models.OnedriveSyncState
import com.app.routineturboa.core.models.SignInStatus
import com.app.routineturboa.core.models.UiState
import com.app.routineturboa.ui.reusable.animation.LoadingSpinner
import com.app.routineturboa.ui.theme.LocalCustomColors
import kotlinx.coroutines.launch

@Composable
fun SignInAndSyncButtons(
    onSyncClick: (Context) -> Unit,
    onSignInClick: (Activity) -> Unit,
    onSignOutClick: (Activity) -> Unit,
    uiState: UiState,
) {
    val tag = "SignInAndSyncButtons"
    val coroutineScope = rememberCoroutineScope()

    val customColors = LocalCustomColors.current

    val context = LocalContext.current
    val activity = LocalContext.current as? Activity

    var isExpanded by remember { mutableStateOf(false) }

    val msalAuthState = uiState.msalAuthState
    val isSignedIn = msalAuthState.isSignedIn
    val username = msalAuthState.username
    val profileImageUrl = msalAuthState.profileImageUrl
    val signInStatus = msalAuthState.signInStatus

    Column {
        // region: Username, UserProfile, and Expand/Collapse (or 'Sign In' button if not signed in)
        Row(
            modifier = Modifier
                .clickable {
                    if (isSignedIn) {
                        // Expand to show sync and sign-out options
                        isExpanded = !isExpanded
                    } else {
                        activity?.let { onSignInClick(activity) }
                    }
                }
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Show user image if signed in
            if (isSignedIn) {
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            // Show default icon if not signed in
            else {
                // Display a loading indicator during the signing-in process
                if (signInStatus == SignInStatus.SigningIn) {
                    LoadingSpinner(
                        height = 18.dp,
                        width = 18.dp,
                        strokeWidth = 3.dp,
                        color = customColors.successIndicatorColor,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                } else{
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Sign in",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(15.dp))

            // Display username or "Sign in" text
            Text(
                text = when (signInStatus) {
                    SignInStatus.SigningIn -> "Signing in..."
                    SignInStatus.Error -> "Sign in again."
                    else -> if (isSignedIn) username else "Sign in"
                },
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Show expand/collapse icon if signed in
            if (isSignedIn) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        // endregion

        // region: Loading Indicator
        // Display a loading indicator during the signing-in process
        if (signInStatus == SignInStatus.SigningIn) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
        // endregion

        // region: SignOut and Sync if SignedIn
        AnimatedVisibility(
            visible = isSignedIn && isExpanded
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 56.dp, end = 16.dp, bottom = 8.dp, top = 1.dp)
            ) {
                // SignOut Button (Icon and Text)
                Row(
                    modifier = Modifier
                        .clickable {
                            coroutineScope.launch {
                                activity?.let { onSignOutClick(activity) }
                            }
                        }
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
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
                    )
                }

                // Sync Button (Icon and Text)
                Row(
                    modifier = Modifier
                        .clickable {
                            Log.d(tag, "Sync Clicked")
                            onSyncClick(context)
                        }
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Sync",
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "Sync",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        // endregion
    }
}


