package com.example.disasterbuster.view

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.disasterbuster.R
import com.example.disasterbuster.view_model.DisasterEventManager
import com.example.disasterbuster.view_model.LocationManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.graphics.scale
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.disasterbuster.model.DisasterItem
import com.example.disasterbuster.view_model.WebOverlayManager

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var loaderOverlay: FrameLayout
    private lateinit var locationManager: LocationManager
    private lateinit var overlayManager: WebOverlayManager
    private val disasterViewModel: DisasterEventManager by viewModels()
    private val markerMap = mutableMapOf<String, MutableList<Marker>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fixSafeAreaIssues()

        loaderOverlay = findViewById(R.id.loader_overlay)
        locationManager = LocationManager(this)

        overlayManager = WebOverlayManager(this, findViewById(R.id.main_container))

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        disasterViewModel.init(this)
        disasterViewModel.fetchDisasters()

        iconUpdateListener()
        observeLocation()
    }

    private fun fixSafeAreaIssues() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val root = findViewById<View>(R.id.main_container)

        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun iconUpdateListener() {
        lifecycleScope.launch {
            disasterViewModel.item.collect { (typeKey, bmp) ->
                val scaled = withContext(Dispatchers.Default) {
                    bmp.scale(75, 100).copy(android.graphics.Bitmap.Config.ARGB_8888, false)
                }
                markerMap[typeKey]?.forEach { marker ->
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(scaled))
                }
            }
        }
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
        locationManager.fetchLocationWithPermission(this)
        getDisasterLocationInfo()

        mMap.setOnMarkerClickListener { marker ->
            marker.showInfoWindow()
            false
        }

        mMap.setOnInfoWindowClickListener { marker ->
            val disaster = marker.tag as? DisasterItem
            disaster?.let { overlayManager.show(it.reportUrl) }
        }
    }

    private fun getDisasterLocationInfo() {
        lifecycleScope.launch {
            disasterViewModel.disasters.collectLatest { disasters ->
                disasters.forEach { disaster ->
                    val coords = disaster.coordinates
                    if (coords.size >= 2) {
                        val latLng = LatLng(coords[1], coords[0])
                        val placeholderIcon =
                            BitmapDescriptorFactory.fromBitmap(disaster.icon.scale(75, 75))

                        val markerOptions = MarkerOptions()
                            .position(latLng)
                            .title("${disaster.name} (Alert Score: ${disaster.alertscore})")
                            .icon(placeholderIcon)

                        val marker = mMap.addMarker(markerOptions)
                        marker?.tag = disaster
                        if (marker != null) {
                            marker.tag = disaster
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

    fun showLoader(show: Boolean) {
        loaderOverlay.visibility = if (show) View.VISIBLE else View.GONE
        loaderOverlay.setOnTouchListener { _, _ -> show }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationManager.handlePermissionResult(requestCode, grantResults)
    }
}
