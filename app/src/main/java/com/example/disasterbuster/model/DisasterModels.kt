package com.example.disasterbuster.model

import android.content.Context
import android.graphics.Bitmap
import com.example.disasterbuster.services.network_services.GdacsNetworkService
import com.example.disasterbuster.services.storage_services.IconStorageService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch


class DisasterModel(
    private val context: Context,
    private val networkService: GdacsNetworkService,
    private val scope: CoroutineScope
) {

    private val iconService = IconStorageService(context)
    private val _items = MutableSharedFlow<Pair<String, Bitmap>>(replay = 1)
    val items: SharedFlow<Pair<String, Bitmap>> = _items

    init {
        scope.launch {
            iconService.iconUpdates.collect { (type, bmp) ->
                _items.emit(type to bmp)
            }
        }
    }

    suspend fun getFilteredDisasters(): List<DisasterItem> {
        val response = networkService.fetchDisasters()
        val items = response.features.map { feature ->
            createDisasterItem(feature)
        }
        return items
    }
    fun createDisasterItem(feature: Feature): DisasterItem {
        val typeKey = feature.properties.eventtype + feature.properties.alertscore
        val placeholder = iconService.loadIcon(typeKey)

        scope.launch(Dispatchers.IO) {
            iconService.downloadAndSaveIcon(typeKey, feature.properties.icon)
        }

        return DisasterItem(
            id = feature.properties.eventid,
            name = feature.properties.name,
            type = feature.properties.eventtype,
            description = feature.properties.description,
            coordinates = feature.geometry.coordinates,
            reportUrl = feature.properties.url.report,
            icon = placeholder,
            iconUrl = feature.properties.icon,
            alertscore = feature.properties.alertscore
        )
    }
}
