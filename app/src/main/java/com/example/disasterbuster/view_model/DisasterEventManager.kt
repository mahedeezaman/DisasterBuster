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

    fun init(context: Context) {
        model = DisasterModel(
            context,
            GdacsNetworkService(),
            viewModelScope
        )
    }

    private val _disasters = MutableStateFlow<List<DisasterItem>>(emptyList())
    val disasters: StateFlow<List<DisasterItem>> get() = _disasters

    fun fetchDisasters() {
        viewModelScope.launch {
            try {
                val items = model.getFilteredDisasters()
                _disasters.value = items
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

