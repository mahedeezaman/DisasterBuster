package com.example.disasterbuster.services.storage_services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.disasterbuster.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.File
import java.net.URL
import androidx.core.graphics.createBitmap

class IconStorageService(private val context: Context) {
    private val iconDir = File(context.filesDir, "icons").apply { if (!exists()) mkdirs() }
    private val _iconUpdates = MutableSharedFlow<Pair<String, Bitmap>>(replay = 1)
    val iconUpdates: SharedFlow<Pair<String, Bitmap>> = _iconUpdates

    private val inProgress = mutableMapOf<String, Boolean>()
    private val scaledCache = mutableMapOf<String, Bitmap>()

    fun getIconFile(type: String) = File(iconDir, type)

    fun loadIcon(type: String): Bitmap =
        if (getIconFile(type).exists()) BitmapFactory.decodeFile(getIconFile(type).path)
        else fallbackBitmap()

    fun fallbackBitmap() =
        BitmapFactory.decodeResource(context.resources, R.drawable.unknown)
            ?: Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

    suspend fun downloadAndSaveIcon(type: String, url: String) {
        if (inProgress[type] == true || getIconFile(type).exists()) return
        inProgress[type] = true
        try {
            val bitmap = URL(url).openStream().use { BitmapFactory.decodeStream(it) }
            getIconFile(type).outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            val scaled = Bitmap.createScaledBitmap(bitmap, 100, 100, true)
            scaledCache[type] = scaled
            _iconUpdates.emit(type to scaled)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inProgress.remove(type)
        }
    }

    fun getScaledIcon(type: String): Bitmap? = scaledCache[type]
}
