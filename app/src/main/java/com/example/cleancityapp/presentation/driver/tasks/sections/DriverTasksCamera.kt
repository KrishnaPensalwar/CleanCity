package com.example.cleancityapp.presentation.driver.tasks.sections

import android.content.Context
import android.net.Uri
import android.text.format.DateUtils
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar

fun createTempPictureUri(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
    val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file,
    )
}

fun formatDriverTaskDate(dateStr: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(dateStr) ?: return dateStr
        
        val now = Calendar.getInstance()
        val taskDate = Calendar.getInstance()
        taskDate.time = date
        
        val isSameDay = now.get(Calendar.YEAR) == taskDate.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == taskDate.get(Calendar.DAY_OF_YEAR)
        
        if (isSameDay) {
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date)
        } else {
            SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(date)
        }
    } catch (_: Exception) {
        dateStr
    }
}
