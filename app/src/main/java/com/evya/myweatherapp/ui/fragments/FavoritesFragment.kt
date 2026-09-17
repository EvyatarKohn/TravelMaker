package com.evya.myweatherapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.evya.myweatherapp.Constants.CITY_NAME
import com.evya.myweatherapp.Constants.FROM_FAVORITES
import com.evya.myweatherapp.Constants.LAT
import com.evya.myweatherapp.Constants.LONG
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.FavoriteFragmentLayoutBinding
import com.evya.myweatherapp.firebaseanalytics.FireBaseEvents
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsNamesStrings.*
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsParamsStrings.PARAMS_CITY_NAME
import com.evya.myweatherapp.model.weathermodel.Weather
import com.evya.myweatherapp.ui.MainActivity
import com.evya.myweatherapp.ui.adapters.FavoritesAdapter
import com.evya.myweatherapp.ui.dialogs.DeleteFavoritesDialog
import com.evya.myweatherapp.viewmodels.CitiesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class FavoritesFragment : Fragment(R.layout.favorite_fragment_layout) {
    private val citiesViewModel: CitiesViewModel by viewModels()
    private var binding: FavoriteFragmentLayoutBinding? = null
    private var favorites: List<Weather> = emptyList()
    private val favoritesAdapter = FavoritesAdapter(::openCity, ::confirmRemoval)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val ui = FavoriteFragmentLayoutBinding.bind(view)
        binding = ui
        ui.citiesNameRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        ui.citiesNameRecyclerView.adapter = favoritesAdapter
        ui.favoritesSearch.doAfterTextChanged { renderFavorites() }
        ui.deleteAllFavorites.setOnClickListener {
            if (favorites.isNotEmpty()) showDeleteDialog(true)
        }
        ui.emptyAction.setOnClickListener {
            if (ui.favoritesSearch.text.isNotBlank()) ui.favoritesSearch.text.clear()
            else (requireActivity() as MainActivity).changeNavBarIndex(R.id.googleMapsFragment, R.id.map)
        }
        childFragmentManager.setFragmentResultListener(DeleteFavoritesDialog.RESULT, viewLifecycleOwner) { _, result ->
            if (result.getBoolean(DeleteFavoritesDialog.DELETE_ALL)) {
                citiesViewModel.deleteAllFavorite(true)
                FireBaseEvents.sendFireBaseCustomEvents(DELETE_ALL_CITIES_FROM_FAVORITES.eventName, bundleOf())
            } else {
                val city = result.getString(DeleteFavoritesDialog.CITY) ?: return@setFragmentResultListener
                citiesViewModel.removeCityDataFromDB(city)
                FireBaseEvents.sendFireBaseCustomEvents(
                    DELETE_CITY_FROM_FAVORITES.eventName, bundleOf(PARAMS_CITY_NAME.paramsName to city)
                )
            }
        }
        citiesViewModel.fetchAllCitiesFromDB.observe(viewLifecycleOwner) { cities ->
            favorites = cities.filter { it.isInFavorites }.sortedBy { it.cityName.lowercase() }
            renderFavorites()
        }
    }

    private fun renderFavorites() {
        val ui = binding ?: return
        val query = ui.favoritesSearch.text.toString().trim()
        val visible = favorites.filter { it.cityName.contains(query, ignoreCase = true) }
        favoritesAdapter.submitList(visible)
        ui.favoritesCount.text = resources.getQuantityString(R.plurals.favorites_count, favorites.size, favorites.size)
        ui.deleteAllFavorites.isVisible = favorites.isNotEmpty()
        ui.citiesNameRecyclerView.isVisible = visible.isNotEmpty()
        ui.emptyState.isVisible = visible.isEmpty()
        val searching = query.isNotEmpty()
        ui.emptyTitle.setText(if (searching) R.string.favorites_no_matches else R.string.favorites_empty_title)
        ui.emptyBody.setText(if (searching) R.string.favorites_no_matches_body else R.string.favorites_empty_body)
        ui.emptyAction.setText(if (searching) R.string.favorites_clear_search else R.string.favorites_explore)
    }

    private fun openCity(weather: Weather) {
        val controller = findNavController()
        if (controller.currentDestination?.id != R.id.favoritesFragment) return
        FireBaseEvents.sendFireBaseCustomEvents(
            CHOOSE_CITY_FROM_FAVORITES.eventName, bundleOf(PARAMS_CITY_NAME.paramsName to weather.cityName)
        )
        controller.navigate(R.id.action_favoritesFragment_to_cityFragment, bundleOf(
            LAT to weather.lat.toFloat(), LONG to weather.lon.toFloat(),
            CITY_NAME to weather.cityName, FROM_FAVORITES to true
        ))
    }

    private fun confirmRemoval(weather: Weather) = showDeleteDialog(false, weather.cityName)

    private fun showDeleteDialog(all: Boolean, city: String = "") {
        if (childFragmentManager.findFragmentByTag(DeleteFavoritesDialog.RESULT) == null) {
            DeleteFavoritesDialog.newInstance(all, city).show(childFragmentManager, DeleteFavoritesDialog.RESULT)
        }
    }

    override fun onDestroyView() {
        binding?.citiesNameRecyclerView?.adapter = null
        binding = null
        super.onDestroyView()
    }
}
