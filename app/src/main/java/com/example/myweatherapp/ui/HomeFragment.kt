package com.example.myweatherapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.fragment.app.viewModels
import com.example.myweatherapp.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment_layout.*

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val mViewModel: MainViewModel by viewModels()
    private lateinit var mCityName: String
    private lateinit var mUnits: String

    companion object {
        fun newInstance(cityName: String, units: String) = HomeFragment().apply {
            mCityName = cityName
            mUnits = units
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.home_fragment_layout, container, false)
        mViewModel.getWeather(mCityName, mUnits)

        mViewModel.weatherRepo.observe(viewLifecycleOwner, Observer {weather->
            city_name.text = weather.name
            clear_sky.text = weather.weather[0].description
            current_temp.text = weather.main.temp.toString()
            temp_var.text = "min " +weather.main.temp_min.toString() + "- max " + weather.main.temp_max.toString()
        })

        return v
    }

}