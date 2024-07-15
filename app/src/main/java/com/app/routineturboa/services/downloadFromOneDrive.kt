package com.app.routineturboa.services

import android.content.Context
import android.util.Log
import com.app.routineturboa.viewmodel.TaskViewModel
import com.microsoft.identity.client.IAuthenticationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun downloadFromOneDrive(authResult: IAuthenticationResult, context: Context, taskViewModel: TaskViewModel) {
    Log.d("MainScreen", "Downloading from OneDrive")
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

    taskViewModel.tasks

    Log.d("MainScreen", "Finished downloading from OneDrive")
}