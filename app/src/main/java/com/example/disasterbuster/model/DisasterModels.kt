package com.example.disasterbuster.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.disasterbuster.R
import com.example.disasterbuster.services.network_services.GdacsNetworkService
import com.example.disasterbuster.services.storage_services.IconStorageService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DisasterModel(
    private val context: Context,
    private val networkService: GdacsNetworkService,
    private val scope: CoroutineScope
) {

    private val iconService = IconStorageService(context)

    suspend fun getFilteredDisasters(): List<DisasterItem> {
        val response = networkService.fetchDisasters()
        return response.features.map { feature ->
            val type = feature.properties.eventtype

            // default bitmap if not yet downloaded
            val iconBitmap: Bitmap = if (iconService.iconExists(type)) {
                iconService.loadIcon(type)
            } else {
                BitmapFactory.decodeResource(context.resources, R.drawable.unknown)
            }

            val disaster = DisasterItem(
                id = feature.properties.eventid,
                name = feature.properties.name,
                type = type,
                description = feature.properties.description,
                coordinates = feature.geometry.coordinates,
                reportUrl = feature.properties.url.report,
                icon = iconBitmap,
                alertscore = feature.properties.alertscore
            )

            // async download for later update
            if (!iconService.iconExists(type)) {
                scope.launch(Dispatchers.IO) {
                    iconService.downloadAndSaveIcon(type, feature.properties.icon)
                    disaster.icon = iconService.loadIcon(type)
                }
            }

            disaster
        }
    }
}
