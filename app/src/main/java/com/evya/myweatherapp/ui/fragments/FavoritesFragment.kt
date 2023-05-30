package com.evya.myweatherapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.FavoriteFragmentLayoutBinding
import com.evya.myweatherapp.firebaseanalytics.FireBaseEvents
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsNamesStrings.*
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsParamsStrings.*
import com.evya.myweatherapp.ui.adapters.FavoritesAdapter
import com.evya.myweatherapp.ui.dialogs.DeleteFavoritesDialog
import com.evya.myweatherapp.util.UtilsFunctions.Companion.showToast
import com.evya.myweatherapp.viewmodels.FavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FavoritesFragment : Fragment(R.layout.favorite_fragment_layout) {

    private val mFavoritesViewModel: FavoritesViewModel by viewModels()
    private lateinit var mBinding: FavoriteFragmentLayoutBinding
    private lateinit var mFavoritesAdapter: FavoritesAdapter
    private lateinit var mNavController: NavController
    private lateinit var mCityName: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = FavoriteFragmentLayoutBinding.bind(view)
        mNavController = Navigation.findNavController(view)

        mFavoritesViewModel.fetchAllCitiesFromDB.observe(viewLifecycleOwner) { weatherList ->
            if (weatherList.isNullOrEmpty()) {
                showToast(context?.getString(R.string.no_saved_favorites), activity?.applicationContext)
            } else {
                mFavoritesAdapter = FavoritesAdapter(weatherList.sortedBy { it.name }, mNavController, this)
                val layoutManager =
                    LinearLayoutManager(
                        activity?.applicationContext,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                mBinding.citiesNameRecyclerView.layoutManager = layoutManager
                mBinding.citiesNameRecyclerView.adapter = mFavoritesAdapter
            }
        }

        mBinding.deleteAllFavorites.setOnClickListener {
            activity?.supportFragmentManager?.let {
                DeleteFavoritesDialog.newInstance(this, true, "").show(
                    it, "DELETE_FAVORITES_DIALOG"
                )
            }
        }
    }

    fun deleteSpecificCityFromDBPopUp(cityName: String) {
        mCityName = cityName
        activity?.supportFragmentManager?.let {
            DeleteFavoritesDialog.newInstance(this, false, cityName).show(
                it, "DELETE_FAVORITES_DIALOG"
            )
        }
    }

    fun deleteSpecificCityFromDB() {
        mFavoritesViewModel.removeCityDataFromDB(mCityName)
    }

    fun deleteAllCitiesFromDB() {
        val params = bundleOf(
            PARAMS_CITY_NAME.paramsName to mCityName
        )
        FireBaseEvents.sendFireBaseCustomEvents(DELETE_ALL_CITIES_FROM_FAVORITES.eventName, params)
        mFavoritesViewModel.deleteAllFavoritesFromDB()
    }
}