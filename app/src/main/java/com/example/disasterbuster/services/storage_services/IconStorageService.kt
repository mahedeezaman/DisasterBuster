package com.example.disasterbuster.services.storage_services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.disasterbuster.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.io.File
import java.net.URL

class IconStorageService(private val context: Context) {

    private val iconDir = File(context.filesDir, "icons").apply {
        if (!exists()) mkdirs()
    }

    private val _iconUpdates = MutableSharedFlow<String>()
    val iconUpdates: SharedFlow<String> = _iconUpdates
    fun getIconFile(type: String): File {
        return File(iconDir, "$type.png")
    }

    fun iconExists(type: String): Boolean {
        return getIconFile(type).exists()
    }

    fun loadIcon(type: String): Bitmap {
        val file = getIconFile(type)

        return if (file.exists()) {
            BitmapFactory.decodeFile(file.path)
        } else {
            BitmapFactory.decodeResource(context.resources, R.drawable.unknown)
        }
    }

    fun saveIcon(type: String, bitmap: Bitmap) {
        val file = getIconFile(type)
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        _iconUpdates.tryEmit(type)
    }

    fun downloadAndSaveIcon(type: String, url: String) {
        try {
            val stream = URL(url).openStream()
            val bitmap = BitmapFactory.decodeStream(stream)
            saveIcon(type, bitmap)
        } catch (_: Exception) {
            //if it fails, it fails. will try to load in next launch i guess. ignore for now
        }
    }
}
