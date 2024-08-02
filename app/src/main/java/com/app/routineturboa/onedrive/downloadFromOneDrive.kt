package com.app.routineturboa.onedrive

import android.content.Context
import android.util.Log
import com.app.routineturboa.viewmodel.TasksViewModel
import com.microsoft.identity.client.IAuthenticationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun downloadFromOneDrive(authResult: IAuthenticationResult, context: Context, tasksViewModel: TasksViewModel) {
    val tag = "downloadFromOneDrive"
    Log.d(tag, "Downloading from OneDrive")
    val authProvider = OneDriveManager.MsalAuthProvider(authResult)
    val oneDriveManager = OneDriveManager(authProvider)

    val files = withContext(Dispatchers.IO) {
        oneDriveManager.listFiles()
    }

    val routineTurboDir = files.find { it.name == "RoutineTurbo" && it.folder != null }

    routineTurboDir?.let { dir ->
        val dirFiles = dir.id?.let { dirId ->
            withContext(Dispatchers.IO) {
                oneDriveManager.listFiles(dirId)
            }
        }

        val dbFile = dirFiles?.find { it.name == "RoutineTurbo.db" }

        dbFile?.let { driveItem ->
            driveItem.id?.let { driveItemId ->
                val localDbFile = context.getDatabasePath("RoutineTurbo.db")
                withContext(Dispatchers.IO) {
                    oneDriveManager.downloadFile(driveItemId, localDbFile)
                }
            }
        }
    }

    tasksViewModel.tasks

    Log.d(tag, "Finished downloading from OneDrive")
}

suspend fun uploadToOneDrive(authResult: IAuthenticationResult, context: Context) {
    val tag = "uploadToOneDrive"
    Log.d(tag, "Uploading to OneDrive")
    val authProvider = OneDriveManager.MsalAuthProvider(authResult)
    val oneDriveManager = OneDriveManager(authProvider)

    val files = withContext(Dispatchers.IO) {
        oneDriveManager.listFiles()
    }

    var routineTurboDir = files.find { it.name == "RoutineTurbo" && it.folder != null }

    if (routineTurboDir == null) {
        // Create RoutineTurbo folder if it doesn't exist
        routineTurboDir = withContext(Dispatchers.IO) {
            oneDriveManager.createFolder("RoutineTurbo")
        }
    }

    if (routineTurboDir != null) {
        routineTurboDir.id?.let { dirId ->
            val localDbFile = context.getDatabasePath("RoutineTurbo.db")
            withContext(Dispatchers.IO) {
                oneDriveManager.uploadFile(dirId, "RoutineTurbo.db", localDbFile)
            }
        }
    }

    Log.d(tag, "Finished uploading to OneDrive")
}