package com.evya.myweatherapp.ui

import android.app.SearchManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.GoogleMapsAttractionFragmentLayoutBinding
import com.evya.myweatherapp.firebaseanalytics.FireBaseEvents
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsNamesStrings.PRESS_ON_ATTRACTION_ON_GOOGLE_MAPS
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsParamsStrings.PARAMS_CLICKED_ATTRACTION
import com.evya.myweatherapp.model.placesmodel.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class AttractionMapActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_PLACES = "places"
        const val EXTRA_LATITUDE = "latitude"
        const val EXTRA_LONGITUDE = "longitude"
    }

    private lateinit var binding: GoogleMapsAttractionFragmentLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GoogleMapsAttractionFragmentLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mapLayout.onCreate(savedInstanceState)
        val places = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_PLACES, Places::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getParcelableExtra(EXTRA_PLACES)
        } ?: Places(emptyList(), "")
        binding.mapLayout.getMapAsync { map ->
            places.features.forEach { feature ->
                map.addMarker(MarkerOptions()
                    .position(LatLng(feature.geometry.coordinates[1], feature.geometry.coordinates[0]))
                    .title(feature.properties.name)
                    .snippet(getString(R.string.more_details)))
            }
            val center = LatLng(
                intent.getDoubleExtra(EXTRA_LATITUDE, 0.0),
                intent.getDoubleExtra(EXTRA_LONGITUDE, 0.0),
            )
            map.animateCamera(CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder().target(center).zoom(11f).build()))
            map.setOnMarkerClickListener { it.showInfoWindow(); true }
            map.setOnInfoWindowLongClickListener { marker ->
                val title = marker.title?.takeIf(String::isNotBlank) ?: return@setOnInfoWindowLongClickListener
                FireBaseEvents.sendFireBaseCustomEvents(
                    PRESS_ON_ATTRACTION_ON_GOOGLE_MAPS.eventName,
                    bundleOf(PARAMS_CLICKED_ATTRACTION.paramsName to title))
                startActivity(Intent(Intent.ACTION_WEB_SEARCH).apply {
                    putExtra(SearchManager.QUERY, title)
                })
            }
        }
    }

    override fun onStart() { super.onStart(); binding.mapLayout.onStart() }
    override fun onResume() { super.onResume(); binding.mapLayout.onResume() }
    override fun onPause() { binding.mapLayout.onPause(); super.onPause() }
    override fun onStop() { binding.mapLayout.onStop(); super.onStop() }
    override fun onDestroy() { binding.mapLayout.onDestroy(); super.onDestroy() }
    override fun onLowMemory() { super.onLowMemory(); binding.mapLayout.onLowMemory() }
    override fun onSaveInstanceState(outState: Bundle) {
        binding.mapLayout.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }
}
