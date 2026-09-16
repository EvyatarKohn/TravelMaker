package com.evya.myweatherapp.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.AttractionResultItemBinding
import com.evya.myweatherapp.databinding.GoogleMapsAttractionFragmentLayoutBinding
import com.evya.myweatherapp.model.placesmodel.Feature
import com.evya.myweatherapp.model.placesmodel.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.text.NumberFormat

class AttractionMapActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_PLACES = "places"
        const val EXTRA_LATITUDE = "latitude"
        const val EXTRA_LONGITUDE = "longitude"
    }

    private lateinit var binding: GoogleMapsAttractionFragmentLayoutBinding
    private var map: GoogleMap? = null
    private var visiblePlaces = emptyList<Feature>()
    private val markers = mutableMapOf<String, Marker>()
    private var selectedId: String? = null
    private var filter = ""
    private lateinit var resultsAdapter: ResultsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GoogleMapsAttractionFragmentLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        binding.backButton.setOnClickListener { finish() }
        binding.mapLayout.onCreate(savedInstanceState?.getBundle("map_state"))
        val places = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_PLACES, Places::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getParcelableExtra(EXTRA_PLACES)
        } ?: Places(emptyList(), "")
        val all = places.features.filter {
            val coordinates = it.geometry.coordinates
            coordinates.size >= 2 && coordinates[0].isFinite() && coordinates[1].isFinite() &&
                coordinates[0] in -180.0..180.0 && coordinates[1] in -90.0..90.0
        }.sortedBy { it.properties.dist }
        selectedId = savedInstanceState?.getString("selected")
        filter = savedInstanceState?.getString("filter") ?: ""
        resultsAdapter = ResultsAdapter()
        binding.resultsList.layoutManager = LinearLayoutManager(this)
        binding.resultsList.adapter = resultsAdapter
        val categories = listOf("") + all.flatMap { kinds(it) }.distinct().sorted()
        binding.categoryFilter.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
            categories.map { if (it.isEmpty()) getString(R.string.results_all) else categoryLabel(it) })
        binding.categoryFilter.setSelection(categories.indexOf(filter).coerceAtLeast(0))
        binding.categoryFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val next = categories[position]
                if (next == filter && visiblePlaces.isNotEmpty()) return
                filter = next
                showResults(all)
            }
        }
        showResults(all)
        binding.mapLayout.getMapAsync { googleMap ->
            map = googleMap
            renderMarkers()
            if (savedInstanceState == null) googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                LatLng(intent.getDoubleExtra(EXTRA_LATITUDE, 0.0), intent.getDoubleExtra(EXTRA_LONGITUDE, 0.0)), 13f))
            googleMap.setOnMarkerClickListener { marker ->
                selectedId = marker.tag as? String
                val index = visiblePlaces.indexOfFirst { it.id == selectedId }
                if (index >= 0) binding.resultsList.smoothScrollToPosition(index)
                marker.showInfoWindow()
                true
            }
            googleMap.setOnInfoWindowClickListener { marker ->
                visiblePlaces.firstOrNull { it.id == marker.tag }?.let(::openMaps)
            }
        }
    }

    private fun kinds(place: Feature) = place.properties.kinds.split(",").map(String::trim)
        .filter { it.isNotEmpty() && it != "interesting_places" }
    private fun categoryLabel(kind: String) = kind.replace("_", " ").replaceFirstChar { it.titlecase() }
    private fun name(place: Feature) = place.properties.name.takeIf { it.isNotBlank() } ?: getString(R.string.results_unnamed)
    private fun position(place: Feature) = LatLng(place.geometry.coordinates[1], place.geometry.coordinates[0])

    private fun showResults(all: List<Feature>) {
        visiblePlaces = all.filter { filter.isEmpty() || filter in kinds(it) }
        resultsAdapter.notifyDataSetChanged()
        binding.resultCount.text = getString(R.string.results_count, visiblePlaces.size)
        renderMarkers()
    }

    private fun renderMarkers() {
        val googleMap = map ?: return
        googleMap.clear()
        markers.clear()
        visiblePlaces.forEach { place ->
            googleMap.addMarker(MarkerOptions().position(position(place)).title(name(place))
                .snippet(getString(R.string.results_open_maps)))?.let {
                it.tag = place.id
                markers[place.id] = it
            }
        }
        markers[selectedId]?.showInfoWindow()
    }

    private fun openMaps(place: Feature) {
        val point = position(place)
        val uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" +
            Uri.encode("${point.latitude},${point.longitude}"))
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, R.string.results_no_maps, Toast.LENGTH_SHORT).show()
        }
    }

    private inner class ResultsAdapter : RecyclerView.Adapter<ResultsAdapter.Holder>() {
        override fun getItemCount() = visiblePlaces.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            Holder(AttractionResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(visiblePlaces[position])
        inner class Holder(private val row: AttractionResultItemBinding) : RecyclerView.ViewHolder(row.root) {
            fun bind(place: Feature) {
                row.placeName.text = name(place)
                val distance = NumberFormat.getNumberInstance().apply { maximumFractionDigits = 1 }
                    .format(place.properties.dist / 1000)
                row.placeDetails.text = getString(R.string.results_details, distance,
                    kinds(place).firstOrNull()?.let(::categoryLabel) ?: getString(R.string.results_title))
                row.showOnMap.setOnClickListener {
                    selectedId = place.id
                    map?.animateCamera(CameraUpdateFactory.newLatLngZoom(position(place), 16f))
                    markers[place.id]?.showInfoWindow()
                }
                row.openMaps.setOnClickListener { openMaps(place) }
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
        outState.putBundle("map_state", Bundle().also(binding.mapLayout::onSaveInstanceState))
        outState.putString("filter", filter)
        outState.putString("selected", selectedId)
        super.onSaveInstanceState(outState)
    }
}

