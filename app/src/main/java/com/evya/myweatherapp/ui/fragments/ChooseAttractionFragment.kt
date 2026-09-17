package com.evya.myweatherapp.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.evya.myweatherapp.Constants
import com.evya.myweatherapp.MainData
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.ChooseAttractionFragmentLayoutBinding
import com.evya.myweatherapp.firebaseanalytics.FireBaseEvents
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsNamesStrings
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsNamesStrings.*
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsParamsStrings.PARAMS_FAILED_TO_LOAD_INTERSTITIAL_AD
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsParamsStrings.PARAMS_WHAT_TO_DO
import com.evya.myweatherapp.ui.MainActivity
import com.evya.myweatherapp.ui.AttractionMapActivity
import com.evya.myweatherapp.util.bestOutdoorWindow
import com.evya.myweatherapp.util.preferIndoorOuting
import com.evya.myweatherapp.viewmodels.PlacesSearchState
import com.evya.myweatherapp.viewmodels.PlacesViewModel
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class ChooseAttractionFragment : Fragment(R.layout.choose_attraction_fragment_layout) {
    private val placesViewModel: PlacesViewModel by viewModels()
    private var binding: ChooseAttractionFragmentLayoutBinding? = null
    private var interstitialAd: InterstitialAd? = null
    private var awaitingAd = false
    private var categories: List<Pair<View, String>> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val ui = ChooseAttractionFragmentLayoutBinding.bind(view)
        binding = ui
        categories = listOf(
            ui.getHotelBtn to "accomodations", ui.getFoodBtn to "foods",
            ui.getNatureBtn to "natural", ui.getMuseumsBtn to "museums",
            ui.getHistoryBtn to "historic", ui.getCultureBtn to "cultural",
            ui.getNightlifeBtn to "adult", ui.getTransportBtn to "transport", ui.getBanksBtn to "banks"
        )
        categories.forEach { (button, kind) -> button.setOnClickListener { startSearch(kind) } }

        val activities = Constants.manipulatedList()
        ui.autoCompleteTextview.threshold = 1
        ui.autoCompleteTextview.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, activities))
        ui.autoCompleteTextview.setOnItemClickListener { _, _, _, _ -> searchText(activities) }
        ui.autoCompleteTextview.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_SEARCH) {
                searchText(activities)
                true
            } else false
        }

        val preferences = requireContext().getSharedPreferences("discovery", Context.MODE_PRIVATE)
        val distance = preferences.getInt("radius_km", (MainData.attractionRadius.toIntOrNull() ?: 1000) / 1000).coerceIn(1, 10)
        MainData.attractionRadius = (distance * 1000).toString()
        ui.radiusSpinner.adapter = ArrayAdapter(
            requireContext(), R.layout.radius_spinner_item, (1..10).map { getString(R.string.discover_distance_value, it) }
        ).apply { setDropDownViewResource(R.layout.radius_spinner_item) }
        ui.radiusSpinner.setSelection(distance - 1)
        ui.radiusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                MainData.attractionRadius = ((position + 1) * 1000).toString()
                preferences.edit().putInt("radius_km", position + 1).apply()
            }
        }
        ui.retrySearch.setOnClickListener { placesViewModel.retry() }
        ui.cancelSearch.setOnClickListener { placesViewModel.cancel() }
        placesViewModel.state.observe(viewLifecycleOwner, ::render)
        loadAd()
        showWeatherHint(ui)
    }

    private fun showWeatherHint(ui: ChooseAttractionFragmentLayoutBinding) {
        val weather = MainData.weather
        if (weather == null) {
            ui.weatherHint.setText(R.string.discover_weather_unknown)
            return
        }
        val now = System.currentTimeMillis() / 1000
        val hours = weather.hourly.orEmpty().filter { it.dt.toLong() >= now - 3600 }
            .sortedBy { it.dt }.take(24)
        val window = bestOutdoorWindow(
            hours,
            weather.daily.orEmpty().map { it.sunrise.toLong()..it.sunset.toLong() },
            weather.isImperial(),
            now,
        )
        ui.weatherHint.setText(
            if (preferIndoorOuting(weather, window != null)) R.string.discover_weather_indoor
            else R.string.discover_weather_outdoor
        )
    }

    private fun searchText(activities: List<String>) {
        val text = binding?.autoCompleteTextview?.text.toString().trim()
        val activity = activities.firstOrNull { it.equals(text, ignoreCase = true) }
        if (activity == null) {
            binding?.autoCompleteTextview?.error = getString(R.string.discover_choose_category)
        } else {
            logEvent(SEARCH_ATTRACTIONS, bundleOf(PARAMS_WHAT_TO_DO.paramsName to activity))
            startSearch(activity.lowercase(Locale.ROOT).replace(" ", "_"))
        }
    }

    private fun startSearch(kind: String) {
        if (awaitingAd || placesViewModel.state.value == PlacesSearchState.Loading) return
        val latitude = MainData.lat.toDoubleOrNull()
        val longitude = MainData.long.toDoubleOrNull()
        if (latitude == null || longitude == null || latitude !in -90.0..90.0 || longitude !in -180.0..180.0) {
            val ui = binding ?: return
            ui.statusPanel.isVisible = true
            ui.statusTitle.setText(R.string.discover_error_title)
            ui.statusBody.setText(R.string.discover_location_needed)
            ui.retrySearch.isVisible = false
            return
        }
        val ui = binding ?: return
        (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(ui.root.windowToken, 0)
        ui.autoCompleteTextview.clearFocus()
        val search = {
            if (binding != null && findNavController().currentDestination?.id == R.id.chooseAttractionFragment) {
                FireBaseEvents.sendFireBaseCustomEvents(WHAT_TO_DO.eventName, bundleOf(PARAMS_WHAT_TO_DO.paramsName to kind))
                placesViewModel.search(latitude.toString(), longitude.toString(), kind)
            }
        }
        val ad = interstitialAd
        if (ad == null) {
            search()
            return
        }
        awaitingAd = true
        interstitialAd = null
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                logEvent(ON_INTERSTITIAL_AD_DISMISSED_FULL_SCREEN_CONTENT)
                awaitingAd = false
                // Let the Back event that dismissed the full-screen ad finish
                // before starting the result activity.
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(300)
                    if (binding != null && lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)) {
                        search()
                    }
                }
            }
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                logEvent(ON_INTERSTITIAL_AD_FAILED_TO_SHOW_FULL_SCREEN_CONTENT,
                    bundleOf(PARAMS_FAILED_TO_LOAD_INTERSTITIAL_AD.paramsName to adError.message))
                awaitingAd = false
                search()
            }
            override fun onAdClicked() = logEvent(CLICK_ON_INTERSTITIAL_AD)
            override fun onAdImpression() = logEvent(ON_INTERSTITIAL_AD_IMPRESSION)
            override fun onAdShowedFullScreenContent() = logEvent(ON_INTERSTITIAL_AD_SHOWED_FULL_SCREEN_CONTENT)
        }
        ad.show(requireActivity())
    }

    private fun render(state: PlacesSearchState) {
        val ui = binding ?: return
        val loading = state == PlacesSearchState.Loading
        ui.loadingPanel.isVisible = loading
        ui.radiusSpinner.isEnabled = !loading
        ui.autoCompleteTextview.isEnabled = !loading
        categories.forEach { (button, _) -> button.isEnabled = !loading; button.alpha = if (loading) 0.5f else 1f }
        ui.statusPanel.isVisible = state == PlacesSearchState.Error || state == PlacesSearchState.Empty
        val empty = state == PlacesSearchState.Empty
        ui.statusTitle.setText(if (empty) R.string.discover_empty_title else R.string.discover_error_title)
        ui.statusBody.setText(if (empty) R.string.discover_empty_body else R.string.discover_error_body)
        if (state is PlacesSearchState.Success && findNavController().currentDestination?.id == R.id.chooseAttractionFragment) {
            startActivity(Intent(requireContext(), AttractionMapActivity::class.java).apply {
                putExtra(AttractionMapActivity.EXTRA_PLACES, state.places)
                putExtra(AttractionMapActivity.EXTRA_LATITUDE, MainData.lat.toDoubleOrNull() ?: 0.0)
                putExtra(AttractionMapActivity.EXTRA_LONGITUDE, MainData.long.toDoubleOrNull() ?: 0.0)
            })
            placesViewModel.consumeResult()
        }
    }

    private fun loadAd() {
        val request = (requireActivity() as MainActivity).adRequest
        InterstitialAd.load(requireContext(), "ca-app-pub-9058418744370338/1048685069", request,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    logEvent(ON_INTERSTITIAL_AD_LOADED)
                    if (binding != null) interstitialAd = ad
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    logEvent(ON_INTERSTITIAL_AD_FAILED_TO_LOAD,
                        bundleOf(PARAMS_FAILED_TO_LOAD_INTERSTITIAL_AD.paramsName to error.message))
                    interstitialAd = null
                }
            })
    }

    private fun logEvent(event: FireBaseEventsNamesStrings, params: Bundle = bundleOf()) {
        FireBaseEvents.sendFireBaseCustomEvents(event.eventName, params)
    }

    override fun onDestroyView() {
        categories = emptyList()
        binding = null
        interstitialAd?.fullScreenContentCallback = null
        interstitialAd = null
        awaitingAd = false
        super.onDestroyView()
    }
}
