package com.example.myweatherapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Observer
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myweatherapp.R
import com.example.myweatherapp.ui.MainListener
import com.example.myweatherapp.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.city_fragment_layout.*
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class CityFragment: Fragment() {
    private val mViewModel: MainViewModel by viewModels()
    private lateinit var mCityName: String
    private lateinit var mUnits: String
    private lateinit var mLat: String
    private lateinit var mLong: String
    private lateinit var mMainListener: MainListener
    private lateinit var mCitiesListBtn: Button
    private lateinit var mCustomCityBtn: Button
    private lateinit var mSunRise: Any
    private lateinit var mSunSet: Any

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
        mCitiesListBtn = v.findViewById(R.id.to_cities_list_btn)
        mCustomCityBtn = v.findViewById(R.id.to_custom_city_btn)

        var degreeUnit = "\u2103"
        var windSpeedDgree =" m/s"
        if (mUnits == "imperial") {
            degreeUnit = "\u2109"
            windSpeedDgree = " miles/hr"
        }
        if (mLat.isNullOrEmpty() || mLong.isNullOrEmpty()) {
            mViewModel.getWeather(mCityName, mUnits)
            mCitiesListBtn.visibility = View.GONE
            mCustomCityBtn.visibility = View.GONE
        } else {
            mViewModel.getCityByLocation(mLat, mLong, mUnits)
            mCitiesListBtn.setOnClickListener {
                mMainListener.replaceToCitiesListFragment()
            }
            mCustomCityBtn.setOnClickListener {
                mMainListener.replaceToCustomCityFragment()
            }
        }

        mViewModel.weatherRepo.observe(viewLifecycleOwner, Observer {weather->
            city_name.text = getString(R.string.city_name_country, weather.name, weather.sys.country)
            clear_sky.text = weather.weather[0].description
            humidity.text = getString(R.string.humidity, weather.main.humidity.toString() + "%")
            wind_speed.text = getString(R.string.wind_speed, weather.wind.speed.toString() + windSpeedDgree)
            current_temp.text = getString(R.string.right_now_temp, weather.main.temp.toInt().toString() + degreeUnit)
            feels_like.text = getString(R.string.feels_like_temp, weather.main.feels_like.toInt().toString() + degreeUnit)
            temp_var.text = getString(R.string.temp_var, weather.main.temp_min.toInt().toString() + degreeUnit, weather.main.temp_max.toInt().toString() + degreeUnit)
            sunrise_sunset.text = sunRiseSunSetTime(weather.sys.sunrise, weather.sys.sunset)
        })

        return v
    }

    private fun sunRiseSunSetTime(sunRise: Int, sunSet: Int): String {
        val calendar = Calendar.getInstance()
        val tz = TimeZone.getDefault()
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.timeInMillis))
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        mSunRise = sdf.format(Date(sunRise.toLong() * 1000))
        mSunSet = sdf.format(Date(sunSet.toLong() * 1000))

        return "Sunrise: $mSunRise\n\nSunset: $mSunSet"
    }

}