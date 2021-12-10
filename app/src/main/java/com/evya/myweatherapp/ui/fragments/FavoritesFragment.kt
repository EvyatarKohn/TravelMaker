package com.evya.myweatherapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.FavoriteFragmentLayoutBinding
import com.evya.myweatherapp.ui.adapters.FavoritesAdapter
import com.evya.myweatherapp.util.UtilsFunctions
import com.evya.myweatherapp.viewmodels.FavoritesViewModel

class FavoritesFragment : Fragment(R.layout.favorite_fragment_layout) {

    private val mFavoritesViewModel: FavoritesViewModel by viewModels()
    private lateinit var mBinding: FavoriteFragmentLayoutBinding
    private lateinit var mFavoritesAdapter: FavoritesAdapter
    private lateinit var mNavController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = FavoriteFragmentLayoutBinding.bind(view)
        mNavController = Navigation.findNavController(view)

        mFavoritesViewModel.fetchAllCities.observe(viewLifecycleOwner, {
            if (it.isNullOrEmpty()) {
                UtilsFunctions.showToast(R.string.no_saved_favorites, activity?.applicationContext)
            } else {
                mFavoritesAdapter = FavoritesAdapter(it, mNavController)
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

        mBinding.deleteFavorites.setOnClickListener {
            mFavoritesViewModel.deleteAllFavorites()
            mFavoritesAdapter.notifyDataSetChanged()
        }
    }
}