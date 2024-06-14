package com.app.routineturboa.services

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
        val request = if (driveItemId == null) {
            graphClient.me().drive().root().children().buildRequest()
        } else {
            graphClient.me().drive().items(driveItemId).children().buildRequest()
        }

        return request.get()?.currentPage ?: emptyList()
    }

    fun downloadFile(driveItemId: String, destinationFile: File): Boolean {
        val request = graphClient.me().drive().items(driveItemId).content().buildRequest()
        return try {
            val inputStream = request.get()
            if (inputStream != null) {
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
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
