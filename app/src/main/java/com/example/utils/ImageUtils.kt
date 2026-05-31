package com.example.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object ImageUtils {

    suspend fun uriToBitmap(context: Context, uri: Uri): Bitmap = withContext(Dispatchers.IO) {
        val inputStream: InputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Failed to open input stream for URI: $uri")
        inputStream.use { stream ->
            BitmapFactory.decodeStream(stream)
                ?: throw IllegalStateException("Failed to decode bitmap from stream")
        }
    }

    suspend fun bitmapToBase64(bitmap: Bitmap): String = withContext(Dispatchers.IO) {
        val outputStream = ByteArrayOutputStream()
        // Resize bitmap if it's extremely large to optimize network payload size and respect token limits
        val maxDimension = 1024
        val resized = if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
            val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
            val (newWidth, newHeight) = if (ratio > 1) {
                maxDimension to (maxDimension / ratio).toInt()
            } else {
                (maxDimension * ratio).toInt() to maxDimension
            }
            Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        } else {
            bitmap
        }

        resized.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        val byteArray = outputStream.toByteArray()
        Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    suspend fun saveImageToInternalStorage(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        val inputStream: InputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Failed to open input stream for uri: $uri")
        
        val filename = "plant_${UUID.randomUUID()}.jpg"
        val file = File(context.filesDir, filename)
        
        FileOutputStream(file).use { outputStream ->
            inputStream.use { stream ->
                stream.copyTo(outputStream)
            }
        }
        file.absolutePath
    }

    suspend fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap): String = withContext(Dispatchers.IO) {
        val filename = "plant_${UUID.randomUUID()}.jpg"
        val file = File(context.filesDir, filename)
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        }
        file.absolutePath
    }
}
