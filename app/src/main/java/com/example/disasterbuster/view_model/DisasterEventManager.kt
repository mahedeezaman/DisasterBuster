package com.example.disasterbuster.view_model

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.disasterbuster.model.DisasterItem
import com.example.disasterbuster.model.DisasterModel
import com.example.disasterbuster.services.network_services.GdacsNetworkService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DisasterEventManager : ViewModel() {

    private lateinit var model: DisasterModel
    private val _disasters = MutableStateFlow<List<DisasterItem>>(emptyList())
    val disasters: StateFlow<List<DisasterItem>> get() = _disasters
    private val _item = MutableSharedFlow<Pair<String, Bitmap>>(replay = 1)
    val item: SharedFlow<Pair<String, Bitmap>> = _item


    fun init(context: Context) {
        model = DisasterModel(
            context,
            GdacsNetworkService(),
            viewModelScope
        )
        CoroutineScope(Dispatchers.Default).launch {
            model.items.collect { (type, bmp) ->
                _item.emit(type to bmp)
            }
        }
    }

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
