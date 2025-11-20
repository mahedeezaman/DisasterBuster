package com.example.disasterbuster.model

import com.example.disasterbuster.services.network_services.EonetNetworkService

data class EventItem(
    val id: String,
    val title: String,
    val category: String,
    val date: String,
    val coordinates: List<Double>
)

class EventModel(private val networkService: EonetNetworkService) {

    // This is what ViewModel will call
    suspend fun getFilteredEvents(): List<EventItem> {
        val rawData = networkService.fetchEvents() // get raw API response

        // filter and transform
        return rawData.events.map { event ->
            val firstGeometry = event.geometry.firstOrNull()
            EventItem(
                id = event.id,
                title = event.title,
                category = event.categories.firstOrNull()?.title ?: "Unknown",
                date = firstGeometry?.date ?: "",
                coordinates = firstGeometry?.coordinates ?: emptyList()
            )
        }
    }
}
