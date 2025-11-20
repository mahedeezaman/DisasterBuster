package com.example.disasterbuster.model

import android.graphics.Bitmap

data class DisasterItem(
    val id: Int,
    val name: String,
    val type: String,
    val description: String,
    val coordinates: List<Double>,
    val reportUrl: String,
    var icon: Bitmap,
    val alertscore: String
)
