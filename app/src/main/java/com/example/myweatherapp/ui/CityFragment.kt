package com.example.myweatherapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Observer
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myweatherapp.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.city_fragment_layout.*

@AndroidEntryPoint
class CityFragment: Fragment() {
    private val mViewModel: MainViewModel by viewModels()
    private lateinit var mCityName: String
    private lateinit var mUnits: String
    private lateinit var mLat: String
    private lateinit var mLong: String
    private lateinit var mMainListener: MainListener
    private lateinit var nBtn: Button

    companion object {
        fun newInstance(lat: String, long: String, cityName: String, units: String, mainListener: MainListener) = CityFragment().apply {
            mCityName = cityName
            mUnits = units
            mLat = lat
            mLong = long
            mMainListener = mainListener
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.city_fragment_layout, container, false)
        nBtn = v.findViewById(R.id.to_cities_list_btn)
        var degreeUnit = "\u2103"
        if (mUnits == "imperial") {
            degreeUnit = "\u2109"
        }
        if (mLat.isNullOrEmpty() || mLong.isNullOrEmpty()) {
            mViewModel.getWeather(mCityName, mUnits)
            nBtn.visibility = View.GONE
        } else {
            mViewModel.getCityByLocation(mLat, mLong, mUnits)
            nBtn.setOnClickListener {
                mMainListener.replaceToCitiesListFragment()
            }
        }

        mViewModel.weatherRepo.observe(viewLifecycleOwner, Observer {weather->
            city_name.text = weather.name
            clear_sky.text = weather.weather[0].description
            humidity.text = "Humidity: " + weather.main.humidity + "%"
            current_temp.text = "Right now: " + weather.main.temp.toInt().toString() + degreeUnit
            feels_like.text = "Feels like: " + weather.main.feels_like.toInt().toString() + degreeUnit
            temp_var.text = "min " + weather.main.temp_min.toInt().toString() + degreeUnit + "- max " + weather.main.temp_max.toInt().toString() + degreeUnit
        })

        return v
    }

}