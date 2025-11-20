package com.example.disasterbuster

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var loaderOverlay: FrameLayout
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loaderOverlay = findViewById(R.id.loader_overlay)
        locationManager = LocationManager(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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
    }

    private fun moveMapToLocation(lat: Double, lng: Double) {
        val currentLatLng = LatLng(lat, lng)
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(currentLatLng).title("You are here"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 2f))
    }

    private fun showLoader(show: Boolean) {
        loaderOverlay.visibility = if (show) View.VISIBLE else View.GONE
        loaderOverlay.setOnTouchListener { _, _ -> show }
    }
}