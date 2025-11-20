package com.example.disasterbuster.model

import com.example.disasterbuster.services.network_services.GdacsNetworkService

class DisasterModel(private val networkService: GdacsNetworkService) {

    // Fetch and map API data to DisasterItem
    suspend fun getFilteredDisasters(): List<DisasterItem> {
        val response = networkService.fetchDisasters()
        return response.features.map { feature ->
            DisasterItem(
                id = feature.properties.eventid,
                name = feature.properties.name,
                type = feature.properties.eventtype,
                description = feature.properties.description,
                coordinates = feature.geometry.coordinates,
                reportUrl = feature.properties.url.report,
                icon = feature.properties.icon
            )
        }
    }
}
