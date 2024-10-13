package com.app.routineturboa.data.onedrive

import android.content.Context
import android.util.Log
import com.app.routineturboa.viewmodel.TasksViewModel
import com.microsoft.identity.client.IAuthenticationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun downloadFromOneDrive(
    authResult: IAuthenticationResult,
    context: Context
) {
    val tag = "downloadFromOneDrive"
    Log.d(tag, "Downloading from OneDrive")
    val authProvider = OneDriveManager.MsalAuthProvider(authResult)
    val oneDriveManager = OneDriveManager(authProvider)

    // Get all the items in OneDrive account
    val itemsInOneDrive = withContext(Dispatchers.IO) {
        oneDriveManager.listFiles()
    }

    // Log all those items' names
    itemsInOneDrive.forEach { onedriveItem ->
        Log.d(tag, "File name: ${onedriveItem.name}")
    }

    // Find the folder named "RoutineTurbo" in all those items
    val routineTurboFolder = itemsInOneDrive.find { item ->
        item.name == "RoutineTurbo" && item.folder != null // Check if it's a folder
    }

    // Inside the folder
    if (routineTurboFolder != null) {
        Log.d(tag, "Found RoutineTurbo folder. Folder ID: ${routineTurboFolder.id}")

        // List the files inside the "RoutineTurbo" folder
        val filesInsideRoutineFolder = withContext(Dispatchers.IO) {
            oneDriveManager.listFiles(routineTurboFolder.id)
        }

        // Log the files inside the folder
        filesInsideRoutineFolder.forEach { file ->
            Log.d(tag, "File in RoutineTurbo folder: ${file.name}")
        }

        // Find the RoutineTurbo.db file in the folder
        val routineTurboDbFile = filesInsideRoutineFolder.find { file ->
            file.name == "RoutineTurbo.db" && file.file != null
        }

        val routineTurboDbFileId = routineTurboDbFile?.id
        val onedriveDestination = context.getDatabasePath("RoutineTurbo_PyQt6.db")

        if (routineTurboDbFileId != null) {
            Log.d(tag, "${routineTurboDbFile.name} file found. File ID: $routineTurboDbFileId")
            Log.d(tag, "Attempting to download the ${routineTurboDbFile.name} file to ${onedriveDestination.absolutePath}")

            // Try downloading the file and log the result
            withContext(Dispatchers.IO) {
                val downloadSuccess = oneDriveManager.downloadFile(routineTurboDbFileId, onedriveDestination)

                // Log the outcome of the download process
                if (downloadSuccess) {
                    Log.d(tag, "RoutineTurbo.db successfully downloaded to ${onedriveDestination.absolutePath}")
                } else {
                    Log.e(tag, "Failed to download RoutineTurbo.db.")
                }
            }

        } else {
            // Handle the case where the RoutineTurbo.db file was not found in the folder
            Log.e(tag, "RoutineTurbo.db file not found in the RoutineTurbo folder")
        }
    } else {
        Log.d(tag, "RoutineTurbo folder not found")
    }

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