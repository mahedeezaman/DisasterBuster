package com.example.disasterbuster.model
data class DisasterItem(
    val id: Int,
    val name: String,
    val type: String,
    val description: String,
    val coordinates: List<Double>,
    val reportUrl: String,
    val icon: String,
    val alertscore: String
)
