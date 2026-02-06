package com.example.receipttracker.ui.utils

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import java.io.File

fun exportReceiptToGallery(context: Context, filePath: String, fileName: String): Boolean {
    val contentResolver = context.contentResolver
    val privateFile = File(filePath)
    if (!privateFile.exists()) return false

    val volumeUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.jpg")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(
            MediaStore.MediaColumns.RELATIVE_PATH,
            Environment.DIRECTORY_PICTURES + "/ReceiptTracker"
        )
        put(MediaStore.MediaColumns.IS_PENDING, 1)
    }

    val publicUri = contentResolver.insert(volumeUri, contentValues) ?: return false

    return try {
        contentResolver.openOutputStream(publicUri)?.use { outputStream ->
            privateFile.inputStream().use { inputStream -> inputStream.copyTo(outputStream) }
        }
        contentValues.clear()
        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
        contentResolver.update(publicUri, contentValues, null, null)
        true
    } catch (_: Exception) {
        contentResolver.delete(publicUri, null, null)
        false
    }
}