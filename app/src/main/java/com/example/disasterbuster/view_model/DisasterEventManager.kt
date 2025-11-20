package com.example.disasterbuster.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.disasterbuster.model.DisasterItem
import com.example.disasterbuster.model.DisasterModel
import com.example.disasterbuster.model.Feature
import com.example.disasterbuster.services.network_services.GdacsNetworkService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DisasterEventManager : ViewModel() {

    private lateinit var model: DisasterModel
    private val _disasters = MutableStateFlow<List<DisasterItem>>(emptyList())
    val disasters: StateFlow<List<DisasterItem>> get() = _disasters

    private val _disasterStats = MutableStateFlow<Map<String, Int>>(emptyMap())
    val disasterStats: StateFlow<Map<String, Int>> get() = _disasterStats

    fun init(context: Context) {
        model = DisasterModel(
            context,
            GdacsNetworkService(),
            viewModelScope
        )
    }

    fun fetchDisasters() {
        viewModelScope.launch {
            try {
                val items = model.getFilteredDisasters()
                _disasters.value = items

                val stats = mutableMapOf<String, Int>()
                items.forEach { disaster ->
                    stats[disaster.type] = (stats[disaster.type] ?: 0) + 1
                }
                _disasterStats.value = stats

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
