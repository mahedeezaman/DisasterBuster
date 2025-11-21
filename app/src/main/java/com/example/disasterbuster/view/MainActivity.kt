package com.example.disasterbuster.view

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.disasterbuster.R
import com.example.disasterbuster.view_model.LocationManager
import com.example.disasterbuster.view_model.DisasterEventManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.graphics.scale

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var loaderOverlay: FrameLayout
    private lateinit var locationManager: LocationManager
    private val disasterViewModel: DisasterEventManager by viewModels()

    // Map of typeKey -> list of markers of that type
    private val markerMap = mutableMapOf<String, MutableList<Marker>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loaderOverlay = findViewById(R.id.loader_overlay)
        locationManager = LocationManager(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        disasterViewModel.init(this)
        disasterViewModel.fetchDisasters()

        // Update markers dynamically when icon downloads complete
        lifecycleScope.launch {
            disasterViewModel.item.collect { (typeKey, bmp) ->
                val scaled = withContext(Dispatchers.Default) {
                    bmp.scale(75, 75).copy(Bitmap.Config.ARGB_8888, false)
                }
                markerMap[typeKey]?.forEach { marker ->
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(scaled))
                }
            }
        }

        observeLocation()
    }

    private fun observeLocation() {
        locationManager.locationLiveData.observe(this) { coords ->
            if (coords != null) {
                moveMapToLocation(coords.first, coords.second)
            }
            showLoader(false)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        showLoader(true)
        locationManager.fetchLocation()

        // Add all DisasterItem markers with placeholder icons
        lifecycleScope.launch {
            disasterViewModel.disasters.collectLatest { disasters ->
                disasters.forEach { disaster ->
                    val coords = disaster.coordinates
                    if (coords.size >= 2) {
                        val latLng = LatLng(coords[1], coords[0])

                        // Use placeholder first
                        val placeholderIcon = BitmapDescriptorFactory.fromBitmap(disaster.icon.scale(75, 75))

                        val markerOptions = MarkerOptions()
                            .position(latLng)
                            .title("${disaster.name} (Alert Score: ${disaster.alertscore})")
                            .icon(placeholderIcon)

                        val marker = mMap.addMarker(markerOptions)
                        if (marker != null) {
                            val typeKey = disaster.type + disaster.alertscore
                            val list = markerMap.getOrPut(typeKey) { mutableListOf() }
                            list.add(marker)
                        }
                    }
                }
            }
        }
    }

    private fun moveMapToLocation(lat: Double, lng: Double) {
        val currentLatLng = LatLng(lat, lng)
        mMap.addMarker(
            MarkerOptions()
                .position(currentLatLng)
                .title("You are here")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 2f))
    }

    private fun showLoader(show: Boolean) {
        loaderOverlay.visibility = if (show) View.VISIBLE else View.GONE
        loaderOverlay.setOnTouchListener { _, _ -> show }
    }
}
