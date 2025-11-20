package com.example.disasterbuster.model

data class EonetResponse(
    val events: List<Event>
)

data class Event(
    val id: String,
    val title: String,
    val description: String?,
    val categories: List<Category>,
    val geometry: List<Geometry>
)

data class Category(val id: String, val title: String)
data class Geometry(val date: String, val coordinates: List<Double>, val type: String)
