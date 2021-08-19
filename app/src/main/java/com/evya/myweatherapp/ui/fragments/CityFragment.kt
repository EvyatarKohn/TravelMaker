package com.evya.myweatherapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Observer
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.evya.myweatherapp.R
import com.evya.myweatherapp.ui.MainActivity
import com.evya.myweatherapp.ui.MainListener
import com.evya.myweatherapp.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.city_fragment_layout.*
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
    private lateinit var mSunRise: String
    private lateinit var mSunSet: String

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

        if (mLat.isEmpty() || mLong.isEmpty()) {
            getWeather(mCityName, mUnits)
            mCitiesListBtn.visibility = View.GONE
            mCustomCityBtn.visibility = View.GONE
        } else {
            getCityByLocation(mLat, mLong, mUnits)
            mCitiesListBtn.setOnClickListener {
                mMainListener.showCitiesListDialog()
            }
            mCustomCityBtn.setOnClickListener {
                mMainListener.replaceToCustomCityFragment()
            }
        }

        mViewModel.weatherRepo.observe(viewLifecycleOwner, Observer {weather->
            city_name.text = getString(R.string.city_name_country, weather.name, weather.sys.country)
            (activity as MainActivity).mCityName =  city_name.text.toString()
            clear_sky.text = weather.weather[0].description
            humidity.text = getString(R.string.humidity, weather.main.humidity.toString() + "%")
            var degreeUnit = "\u2103"
            var windSpeedDgree =" m/s"
            if (mUnits == "imperial") {
                degreeUnit = "\u2109"
                windSpeedDgree = " miles/hr"
            }
            wind_speed.text = getString(R.string.wind_speed, weather.wind.speed.toString() + windSpeedDgree)
            current_temp.text = getString(R.string.right_now_temp, weather.main.temp.toInt().toString() + degreeUnit)
            feels_like.text = getString(R.string.feels_like_temp, weather.main.feels_like.toInt().toString() + degreeUnit)
            temp_var.text = getString(R.string.temp_var, weather.main.temp_min.toInt().toString() + degreeUnit, weather.main.temp_max.toInt().toString() + degreeUnit)
            sunrise_sunset.text = sunRiseSunSetTime(weather.sys.sunrise, weather.sys.sunset)
        })

        return v
    }

    fun getCityByLocation(lat: String, long: String, units: String) {
        mUnits = units
        mViewModel.getCityByLocation(lat, long, units)
    }

    fun getWeather(cityName: String, units: String) {
        mUnits = units
        mViewModel.getWeather(cityName, units)
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