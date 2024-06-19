package com.app.routineturboa.services

import android.util.Log
import com.microsoft.graph.authentication.IAuthenticationProvider
import com.microsoft.graph.models.DriveItem
import com.microsoft.graph.requests.GraphServiceClient
import com.microsoft.identity.client.IAuthenticationResult
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.concurrent.CompletableFuture

class OneDriveManager(private val authProvider: IAuthenticationProvider) {

    private val graphClient = GraphServiceClient.builder()
        .authenticationProvider(authProvider)
        .buildClient()

    fun listFiles(driveItemId: String? = null): List<DriveItem> {
        Log.d("OneDriveManager", "Listing files for drive item ID: $driveItemId")
        val request = if (driveItemId == null) {
            graphClient.me().drive().root().children().buildRequest()
        } else {
            graphClient.me().drive().items(driveItemId).children().buildRequest()
        }

        val files = request.get()?.currentPage ?: emptyList()
        Log.d("OneDriveManager", "Found ${files.size} files")
        return files
    }

    fun downloadFile(driveItemId: String, destinationFile: File): Boolean {
        Log.d("OneDriveManager", "Downloading file with ID: $driveItemId to ${destinationFile.absolutePath}")
        val request = graphClient.me().drive().items(driveItemId).content().buildRequest()
        return try {
            val inputStream = request.get()
            if (inputStream != null) {
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                Log.d("OneDriveManager", "File downloaded successfully")
                true
            } else {
                Log.e("OneDriveManager", "Failed to download file: input stream is null")
                false
            }
        } catch (e: Exception) {
            Log.e("OneDriveManager", "Error downloading file: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    class MsalAuthProvider(private val authenticationResult: IAuthenticationResult) : IAuthenticationProvider {
        override fun getAuthorizationTokenAsync(requestUrl: URL): CompletableFuture<String> {
            return CompletableFuture.completedFuture(authenticationResult.accessToken)
        }
    }
}
