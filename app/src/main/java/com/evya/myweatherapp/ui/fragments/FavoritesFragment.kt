package com.evya.myweatherapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.FavoriteFragmentLayoutBinding
import com.evya.myweatherapp.ui.adapters.FavoritesAdapter
import com.evya.myweatherapp.ui.dialogs.DeleteFavoritesDialog
import com.evya.myweatherapp.util.FireBaseEvents
import com.evya.myweatherapp.util.UtilsFunctions
import com.evya.myweatherapp.viewmodels.FavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


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

        mFavoritesViewModel.fetchAllCitiesFromDB.observe(viewLifecycleOwner, {
            if (it.isNullOrEmpty()) {
                UtilsFunctions.showToast(R.string.no_saved_favorites, activity?.applicationContext)
            } else {
                mFavoritesAdapter = FavoritesAdapter(it, mNavController, this)
                val layoutManager =
                    LinearLayoutManager(
                        activity?.applicationContext,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                mBinding.citiesNameRecyclerView.layoutManager = layoutManager
                mBinding.citiesNameRecyclerView.adapter = mFavoritesAdapter
            }
        })

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
        FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.DeleteAllCitiesFromFavorites.toString())
        mFavoritesViewModel.deleteAllFavoritesFromDB()
        mFavoritesAdapter.notifyDataSetChanged()
    }
}