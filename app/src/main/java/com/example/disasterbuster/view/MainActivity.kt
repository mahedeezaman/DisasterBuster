package com.example.disasterbuster.view

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.disasterbuster.R
import com.example.disasterbuster.view_model.LocationManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.lifecycle.lifecycleScope
import com.example.disasterbuster.view_model.DisasterEventManager
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var loaderOverlay: FrameLayout
    private lateinit var locationManager: LocationManager
    private val disasterViewModel: DisasterEventManager by viewModels()


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

        lifecycleScope.launch {
            disasterViewModel.disasters.collectLatest { disasters ->
                disasters.forEach { disaster ->
                    val coords = disaster.coordinates
                    if (coords.size >= 2) {
                        val latLng = LatLng(coords[1], coords[0])
                        val scaledIcon = Bitmap.createScaledBitmap(disaster.icon, 100, 100, true)
                        val markerOptions = MarkerOptions()
                            .position(latLng)
                            .title(disaster.name + " (Alert Score: ${disaster.alertscore})")
                            .icon(BitmapDescriptorFactory.fromBitmap(scaledIcon))
                        mMap.addMarker(markerOptions)
                    }
                }
            }
        }
    }

    private fun moveMapToLocation(lat: Double, lng: Double) {
        val currentLatLng = LatLng(lat, lng)
        mMap.addMarker(MarkerOptions().position(currentLatLng).title("You are here"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 2f))
    }

    private fun showLoader(show: Boolean) {
        loaderOverlay.visibility = if (show) View.VISIBLE else View.GONE
        loaderOverlay.setOnTouchListener { _, _ -> show }
    }
}