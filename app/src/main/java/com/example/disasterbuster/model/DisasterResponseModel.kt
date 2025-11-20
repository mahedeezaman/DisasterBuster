package com.example.disasterbuster.model

data class DisasterResponse(
    val features: List<Feature>
)

data class Feature(
    val geometry: GeometryCoordinate,
    val properties: Properties
)

data class GeometryCoordinate(
    val coordinates: List<Double>
)

data class Properties(
    val eventtype: String,
    val eventid: Int,
    val name: String,
    val description: String,
    val icon: String,
    val url: URLClass
)

data class URLClass(
    val report: String
)
