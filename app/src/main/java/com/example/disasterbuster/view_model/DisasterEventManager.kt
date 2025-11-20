package com.example.disasterbuster.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.disasterbuster.model.DisasterItem
import com.example.disasterbuster.model.DisasterModel
import com.example.disasterbuster.services.network_services.GdacsNetworkService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DisasterEventManager : ViewModel() {

    // Model handles fetching & filtering
    private val model = DisasterModel(GdacsNetworkService)

    private val _disasters = MutableStateFlow<List<DisasterItem>>(emptyList())
    val disasters: StateFlow<List<DisasterItem>> get() = _disasters

    fun loadDisasters() {
        viewModelScope.launch {
            try {
                val filteredData = model.getFilteredDisasters()
                _disasters.value = filteredData
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
