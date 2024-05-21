package com.app.routineturbo_android

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

fun copyDatabase(context: Context) {
    val dbPath = context.getDatabasePath(DatabaseHelper.DATABASE_NAME).absolutePath
    val dbFile = File(dbPath)
    if (!dbFile.exists()) {
        context.assets.open(DatabaseHelper.DATABASE_NAME).use { inputStream ->
            FileOutputStream(dbPath).use { outputStream ->
                copyStream(inputStream, outputStream)
            }
        }
    }
}

@Throws(IOException::class)
private fun copyStream(inputStream: InputStream, outputStream: OutputStream) {
    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream.read(buffer).also { length = it } > 0) {
        outputStream.write(buffer, 0, length)
    }
}
