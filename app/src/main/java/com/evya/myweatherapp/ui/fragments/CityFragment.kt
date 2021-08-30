package com.evya.myweatherapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Observer
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.evya.myweatherapp.R
import com.evya.myweatherapp.adapter.DailyWeatherAdapter
import com.evya.myweatherapp.adapter.MainCityAdapter
import com.evya.myweatherapp.model.dailyweathermodel.DailyWeatherData
import com.evya.myweatherapp.ui.MainActivity
import com.evya.myweatherapp.ui.MainListener
import com.evya.myweatherapp.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.city_fragment_layout.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min


@AndroidEntryPoint
class CityFragment: Fragment() {
    private val mViewModel: MainViewModel by viewModels()
    private var mCityName = "Tel-aviv"
    private var mCountryCode = "IL"
    private lateinit var mUnits: String
    private lateinit var mLat: String
    private lateinit var mLong: String
    private lateinit var mMainListener: MainListener
    private lateinit var mMainCitiesAdapter: MainCityAdapter
    private lateinit var mDailyAdapter: DailyWeatherAdapter
    private lateinit var mLocationBtn: TextView
    private var mDegreeUnit = "\u2103"
    private var mWindSpeed = " m/s"

    companion object {
        fun newInstance(lat: String, long: String, cityName: String, units: String, mainListener: MainListener) = CityFragment().apply {
            mCityName = cityName
            mUnits = units
            mLat = lat
            mLong = long
            mMainListener = mainListener
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTopAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.city_fragment_layout, container, false)

        if (mUnits == "imperial") {
            mDegreeUnit = "\u2109"
            mWindSpeed = " miles/hr"
        }

        if (mLat.isEmpty() || mLong.isEmpty()) {
            getWeather(mCityName, mUnits)
            getDailyWeather(mCityName, mCountryCode,  mUnits)
        } else {
            getCityByLocation(mLat, mLong, mUnits)
            getDailyWeatherByLocation(mLat, mLong, mUnits)
        }

        mLocationBtn = v.findViewById(R.id.location_icon)
        mLocationBtn.setOnClickListener {
            mMainListener.replaceToCustomCityFragment()
        }

        mViewModel.weatherRepo.observe(viewLifecycleOwner, Observer { weather ->
            mCountryCode = weather.sys.country
            (activity as MainActivity).mCountryCode = weather.sys.country
            city_name.text = getString(R.string.city_name_country, weather.name, weather.sys.country)
            (activity as MainActivity).mCityName =  weather.name
            temp.text = getString(R.string.temp,weather.main.temp.toInt().toString(), mDegreeUnit)
            feels_like.text = getString(R.string.feels_like_temp, weather.main.feelsLike.toInt().toString() + mDegreeUnit)
            humidity.text = getString(R.string.humidity_new, weather.main.humidity.toString() + "%")
            wind_speed.text =
                getString(R.string.wind_speed_new, weather.wind.speed.toString() + mWindSpeed)
            feels_like.text = getString(
                R.string.feels_like_temp,
                weather.main.feelsLike.toInt().toString() + mDegreeUnit
            )
//            temp_var.text = getString(R.string.temp_var, weather.main.tempMin.toInt().toString() + degreeUnit, weather.main.tempMax.toInt().toString() + degreeUnit)
            sunrise.text = getString(R.string.sunrise_time, setTimeToHour(weather.sys.sunrise))
            sunset.text = getString(R.string.sunset_time, setTimeToHour(weather.sys.sunset))
           /* if (setTimeToHour(Calendar.getInstance().time.time.toInt()) > setTimeToHour(weather.sys.sunrise) &&
               (setTimeToHour(Calendar.getInstance().time.time.toInt()) > setTimeToHour(weather.sys.sunset))) {
                main_image.setBackgroundColor(R.drawable.ic_night)
               }*/

            description.text = getString(R.string.description, weather.weather[0].description)
            var distanceUnits = "m"
            var visibility = weather.visibility
            if (weather.visibility > 999) {
                visibility = weather.visibility/1000
                distanceUnits = "Km"
            }
            visible.text = getString(R.string.visibility, visibility.toString(), distanceUnits)
/*            temp_max.text = getString(R.string.temp_max, weather.main.tempMax.toInt().toString(), mDegreeUnit)
            temp_min.text = getString(R.string.temp_min, weather.main.tempMin.toInt().toString(), mDegreeUnit)*/
        })


        mViewModel.dailyWeatherRepo.observe(viewLifecycleOwner, Observer {dailyWeather ->
            setDailyAdapter(dailyWeather.list)
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

    fun getDailyWeatherByLocation(lat: String, long: String, units: String) {
        mUnits = units
        mViewModel.getDailyWeatherByLocation(lat, long, units)
    }

    fun getDailyWeather(cityName: String, countryCode: String, units: String) {
        mUnits = units
        mViewModel.getDailyWeather(cityName, countryCode, units)
    }

    private fun setTimeToHour(time: Int): String {
        val calendar = Calendar.getInstance()
        val tz = TimeZone.getDefault()
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.timeInMillis))
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(time.toLong() * 1000))
    }

    private fun setTopAdapter() {
        val citiesList = arrayListOf("Tel-Aviv", "Jerusalem", "Eilat", "Haifa", "Tiberias", "Ramat Gan", "Beersheba", "Mitzpe Ramon")
        mMainCitiesAdapter = MainCityAdapter(activity?.applicationContext, citiesList, mMainListener)
        val layoutManager = LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.HORIZONTAL, false)
        main_cities_recycler_view.layoutManager = layoutManager
        main_cities_recycler_view.adapter = mMainCitiesAdapter
    }

    private fun setDailyAdapter(dailyWeatherList: List<DailyWeatherData>) {

/*        val minMaxTempArray: ArrayList<Pair<Int,Int>> = ArrayList()
        dailyWeatherList.forEach { dailyWeatherData ->
            minMaxTempArray.add(Pair(dailyWeatherData.main.tempMin.toInt(), dailyWeatherData.main.tempMax.toInt()))
        }*/

        val minTempRawArray: ArrayList<Int> = ArrayList()
        dailyWeatherList.forEach {
            minTempRawArray.add(it.main.tempMin.toInt())
        }
        val minTempArray = minTempRawArray.sorted().take(5)

        val maxTempRawArray: ArrayList<Int> = ArrayList()
        dailyWeatherList.forEach {
            maxTempRawArray.add(it.main.tempMax.toInt())
        }
        val maxTempArray = maxTempRawArray.sortedDescending().take(5)

        val newList = dailyWeatherList.filterIndexed { index, dailyWeatherData ->  index % 8 == 0}

        mDailyAdapter = DailyWeatherAdapter(newList, minTempArray, maxTempArray)
        val layoutManager = LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.HORIZONTAL, false)
        daily_weather_recycler_view.layoutManager = layoutManager
        daily_weather_recycler_view.adapter = mDailyAdapter
    }
}