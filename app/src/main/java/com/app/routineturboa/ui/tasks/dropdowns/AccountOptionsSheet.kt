package com.app.routineturboa.ui.tasks.dropdowns

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.app.routineturboa.core.models.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountOptionsSheet(
    uiState: UiState,
    onSyncClick: (Context) -> Unit,
    onSignOutClick: (Activity) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val activity = LocalContext.current as? Activity

    // Create a ModalBottomSheetState
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false // Allow partial expansion
    )

    // ModalBottomSheet composable
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState
    ) {
        // Content inside the bottom sheet
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp) // Set a fixed height
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Account Options",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Divider()

                if (uiState.msalAuthState.isSignedIn) {
                    ListItem(
                        headlineContent = { Text("Sync Tasks") },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Sync"
                            )
                        },
                        modifier = Modifier.clickable {
                            onSyncClick(context)
                            onDismiss()
                        }
                    )

                    ListItem(
                        headlineContent = { Text("Sign Out") },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Sign Out"
                            )
                        },
                        modifier = Modifier.clickable {
                            activity?.let { onSignOutClick(it) }
                            onDismiss()
                        }
                    )
                } else {
                    ListItem(
                        headlineContent = { Text("Sign In") },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Sign In"
                            )
                        },
                        modifier = Modifier.clickable {
                            // Add sign-in logic here
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}


