package com.example.myweatherapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.myweatherapp.R
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweatherapp.adapter.CitiesAdapter
import com.example.myweatherapp.citiesmodel.CityData
import com.example.myweatherapp.ui.MainListener
import com.example.myweatherapp.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.cities_fragment_layout.*

@AndroidEntryPoint
class CitiesFragment : Fragment() {
    private val mViewModel: MainViewModel by viewModels()
    private lateinit var mAdapter: CitiesAdapter
    private lateinit var mCitiesList: List<CityData>
    private lateinit var mMainListener: MainListener
    private lateinit var mUnits: String
    private lateinit var mBoundaryBox: String /*"west:34,south:29.5,east:34.9,north:36.5,200"*/

    companion object {
        fun newInstance(units: String, boundaryBox: String, mainListener: MainListener) = CitiesFragment().apply {
            mUnits = units
            mMainListener = mainListener
            mBoundaryBox = boundaryBox
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.cities_fragment_layout, container, false)

        getCitiesList(mUnits, mBoundaryBox)
        mViewModel.citiesWeatherRepo.observe(viewLifecycleOwner, Observer { citiesWeather ->
            mCitiesList = citiesWeather.list
            mAdapter = CitiesAdapter(mCitiesList, mMainListener, mUnits)
            val layoutManager = LinearLayoutManager(activity?.applicationContext)
            recycler_view.layoutManager = layoutManager
            recycler_view.adapter = mAdapter
        })

        return v
    }

    fun getCitiesList(units: String, boundaryBox: String) {
        mUnits = units
        mViewModel.getCitiesList(units, boundaryBox)
    }

}