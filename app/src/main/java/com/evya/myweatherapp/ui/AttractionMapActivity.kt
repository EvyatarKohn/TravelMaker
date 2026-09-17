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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.AttractionResultItemBinding
import com.evya.myweatherapp.databinding.GoogleMapsAttractionFragmentLayoutBinding
import com.evya.myweatherapp.model.placesmodel.Feature
import com.evya.myweatherapp.model.placesmodel.Places
import com.evya.myweatherapp.model.PlanStop
import com.evya.myweatherapp.repository.DayPlanStore
import com.evya.myweatherapp.MainData
import com.evya.myweatherapp.util.bestOutdoorWindow
import com.evya.myweatherapp.util.preferIndoorOuting
import com.evya.myweatherapp.util.rankPlacesForWeather
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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
    private lateinit var planStore: DayPlanStore

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
        planStore = DayPlanStore(this)
        binding.dayPlan.setOnClickListener { startActivity(Intent(this, DayPlanActivity::class.java)) }
        binding.showAll.setOnClickListener { fitVisiblePlaces() }
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
        binding.search.setText(savedInstanceState?.getString("query").orEmpty())
        binding.search.doAfterTextChanged { showResults(all) }
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
                updateSelection()
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
        val query = binding.search.text.toString().trim()
        val filtered = all.filter { (filter.isEmpty() || filter in kinds(it)) && name(it).contains(query, true) }
        val weather = MainData.weather
        val now = System.currentTimeMillis() / 1000
        val hours = weather?.hourly.orEmpty().filter { it.dt.toLong() >= now - 3600 }
            .sortedBy { it.dt }.take(24)
        val window = weather?.let {
            bestOutdoorWindow(
                hours,
                it.daily.orEmpty().map { day -> day.sunrise.toLong()..day.sunset.toLong() },
                it.isImperial(),
                now,
            )
        }
        val indoorFirst = preferIndoorOuting(weather, window != null)
        visiblePlaces = rankPlacesForWeather(filtered, indoorFirst)
        binding.weatherSortHint.isVisible = indoorFirst && visiblePlaces.isNotEmpty()
        resultsAdapter.notifyDataSetChanged()
        binding.resultCount.text = getString(R.string.results_count, visiblePlaces.size)
        binding.emptyResults.isVisible = visiblePlaces.isEmpty()
        binding.showAll.isEnabled = visiblePlaces.isNotEmpty()
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
        updateSelection()
    }

    private fun updateSelection() {
        markers.forEach { (id, marker) -> marker.setIcon(BitmapDescriptorFactory.defaultMarker(
            if (id == selectedId) BitmapDescriptorFactory.HUE_ORANGE else BitmapDescriptorFactory.HUE_CYAN)) }
        resultsAdapter.notifyDataSetChanged()
    }

    private fun fitVisiblePlaces() {
        val googleMap = map ?: return
        if (visiblePlaces.isEmpty()) return
        if (visiblePlaces.size == 1) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position(visiblePlaces.first()), 16f))
        } else if (binding.mapLayout.width > 0 && binding.mapLayout.height > 0) {
            val bounds = LatLngBounds.builder().apply { visiblePlaces.forEach { include(position(it)) } }.build()
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, binding.mapLayout.width,
                binding.mapLayout.height, (32 * resources.displayMetrics.density).toInt()))
        }
    }

    private fun openMaps(place: Feature) {
        val point = position(place)
        val uri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" +
            Uri.encode("${point.latitude},${point.longitude}"))
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (_: ActivityNotFoundException) {
            // Maps app unavailable.
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
                row.selectedLabel.isVisible = place.id == selectedId
                val saved = planStore.load().stops.any { it.id == place.id }
                row.savePlace.setText(if (saved) R.string.plan_added else R.string.plan_add)
                row.savePlace.isEnabled = !saved
                row.savePlace.setOnClickListener {
                    val point = position(place)
                    planStore.save(planStore.load().add(PlanStop(place.id, name(place),
                        kinds(place).firstOrNull()?.let(::categoryLabel).orEmpty(), point.latitude, point.longitude)))
                    notifyItemChanged(bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener)
                }
                val distance = NumberFormat.getNumberInstance().apply { maximumFractionDigits = 1 }
                    .format(place.properties.dist / 1000)
                row.placeDetails.text = getString(R.string.results_details, distance,
                    kinds(place).firstOrNull()?.let(::categoryLabel) ?: getString(R.string.results_title))
                row.showOnMap.setOnClickListener {
                    selectedId = place.id
                    updateSelection()
                    map?.animateCamera(CameraUpdateFactory.newLatLngZoom(position(place), 16f))
                    markers[place.id]?.showInfoWindow()
                }
                row.openMaps.setOnClickListener { openMaps(place) }
            }
        }
    }

    override fun onStart() { super.onStart(); binding.mapLayout.onStart() }
    override fun onResume() { super.onResume(); binding.mapLayout.onResume(); if (::resultsAdapter.isInitialized) resultsAdapter.notifyDataSetChanged() }
    override fun onPause() { binding.mapLayout.onPause(); super.onPause() }
    override fun onStop() { binding.mapLayout.onStop(); super.onStop() }
    override fun onDestroy() { binding.mapLayout.onDestroy(); super.onDestroy() }
    override fun onLowMemory() { super.onLowMemory(); binding.mapLayout.onLowMemory() }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBundle("map_state", Bundle().also(binding.mapLayout::onSaveInstanceState))
        outState.putString("filter", filter)
        outState.putString("selected", selectedId)
        outState.putString("query", binding.search.text.toString())
        super.onSaveInstanceState(outState)
    }
}

