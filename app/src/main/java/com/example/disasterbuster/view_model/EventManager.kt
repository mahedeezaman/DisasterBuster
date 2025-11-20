package com.example.disasterbuster.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.disasterbuster.model.EventItem
import com.example.disasterbuster.model.EventModel
import com.example.disasterbuster.services.network_services.EonetNetworkService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventManager : ViewModel() {

    private val model = EventModel(EonetNetworkService())

    private val _events = MutableStateFlow<List<EventItem>>(emptyList())
    val events: StateFlow<List<EventItem>> get() = _events

    fun loadEvents() {
        viewModelScope.launch {
            val filteredData = model.getFilteredEvents()
            _events.value = filteredData
        }
    }
}
