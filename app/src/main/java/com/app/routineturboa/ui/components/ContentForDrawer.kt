package com.app.routineturboa.ui.components

import android.content.Context
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.app.routineturboa.R
import com.app.routineturboa.reminders.ReminderManager
import kotlinx.coroutines.launch

@Composable
fun RowItem(text: String, icon: ImageVector, onItemClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
            .padding(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null, // Handle accessibility if needed
            modifier = Modifier.size(24.dp) // Adjust size as necessary
        )
        Spacer(modifier = Modifier.width(16.dp)) // Adjust spacing between icon and text
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun DrawerTopItem(appName: String) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 10.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.routineturbo),
            contentDescription = "App Icon",
            modifier = Modifier.size(50.dp).clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = appName,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun ContentForDrawer(reminderManager: ReminderManager, onItemClicked: () -> Unit) {
    val context = LocalContext.current
    val appName = context.getString(R.string.app_name)
    val coroutineScope = rememberCoroutineScope()


    DrawerTopItem(appName)
    SignInItem()
    RowItem("Sync", Icons.Default.Sync) { onItemClicked() }
    RowItem("Settings", Icons.Default.Settings) { onItemClicked() }
    RowItem("Sign Out", Icons.Default.ExitToApp) { onItemClicked() }

    // Define the observeAndScheduleReminders lambda
    val observeAndSchedule = {
        Log.d("ContentForDrawer", "observeAndSchedule called")
        coroutineScope.launch {
            reminderManager.observeAndScheduleReminders(context)
        }
    }

    RowItem("Test", Icons.Default.Build) { observeAndSchedule() }

}

fun startSigningIn(context: Context, message: String) {
    Toast.makeText(context, "Test: $message", Toast.LENGTH_LONG).show()
}

