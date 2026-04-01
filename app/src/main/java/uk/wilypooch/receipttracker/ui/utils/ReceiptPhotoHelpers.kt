package uk.wilypooch.receipttracker.ui.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Helper function for when user wants to take a photo using device camera
fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"

    val storageDir = File(getExternalFilesDir(null), "receipts")

    if (!storageDir.exists()) storageDir.mkdirs()

    return File.createTempFile(
        imageFileName,
        ".jpg",
        storageDir
    )
}

// Helper function for when user selects photo to use in app that is already in gallery
fun Context.copyUriToFile(uri: Uri, targetFile: File) {
    contentResolver.openInputStream(uri)?.use { input ->
        targetFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }
}