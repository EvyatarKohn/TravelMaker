package com.evya.myweatherapp.ui.fragments

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.evya.myweatherapp.R
import com.evya.myweatherapp.model.citiesaroundmodel.CitiesAroundData
import com.evya.myweatherapp.model.dailyweathermodel.DailyWeatherData
import com.evya.myweatherapp.model.weathermodel.Weather
import com.evya.myweatherapp.ui.MainActivity
import com.evya.myweatherapp.ui.MainActivity.Companion.IMPERIAL
import com.evya.myweatherapp.ui.MainActivity.Companion.IMPERIAL_DEGREE
import com.evya.myweatherapp.ui.MainActivity.Companion.METRIC
import com.evya.myweatherapp.ui.MainActivity.Companion.METRIC_DEGREE
import com.evya.myweatherapp.ui.adapters.DailyWeatherAdapter
import com.evya.myweatherapp.ui.adapters.MainCityAdapter
import com.evya.myweatherapp.ui.viewmodels.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.city_fragment_layout.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CityFragment : Fragment() {
    private val mWeatherViewModel: WeatherViewModel by viewModels()
    private var mCityName = "Tel-aviv"
    private var mCountryCode = "IL"
    private var mUnits: String = METRIC
    private var mLat: String = "32.083333"
    private var mLong: String = "34.7999968"
    private lateinit var mMainCitiesAdapter: MainCityAdapter
    private lateinit var mDailyAdapter: DailyWeatherAdapter
    private var mWeather: Weather? = null
    private var mDegreeUnit = "\u2103"
    private var mWindSpeed = " m/s"
    private var mCelsius: Boolean = true
    private lateinit var mNavController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mNavController = Navigation.findNavController(view)
        setOnClickListener()
        setSpan(0, 1)

        if (arguments?.get("fromMaps") == true) {
            mLat = arguments?.get("lat").toString()
            mLong = arguments?.get("long").toString()
        }

        if (mUnits == "imperial") {
            mDegreeUnit = "\u2109"
            mWindSpeed = " miles/hr"
        }

        when {
            mWeather != null -> {
                showWeather(mWeather!!)
                getDailyWeather(mCityName, mCountryCode, mUnits)
            }
            arguments?.get("fromTopAdapter") == true -> {
                mCityName = arguments?.get("cityName").toString()
                getWeather(mCityName, mUnits)
                getDailyWeather(mCityName, mCountryCode, mUnits)
            }
            else -> {
                if (mLat.isEmpty() || mLong.isEmpty()) {
                    getWeather(mCityName, mUnits)
                    getDailyWeather(mCityName, mCountryCode, mUnits)
                } else {
                    getCityByLocation(mLat, mLong, mUnits)
                    getDailyWeatherByLocation(mLat, mLong, mUnits)
                }
                mWeatherViewModel.getCitiesAround(mLat, mLong, mUnits)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.city_fragment_layout, container, false)

        successObservers()
        errorObservers()

        return v
    }

    private fun successObservers() {
        mWeatherViewModel.weatherRepo.observe(viewLifecycleOwner, { weather ->
            mLat = weather.coord.lat.toString()
            mLong = weather.coord.lon.toString()
            mWeatherViewModel.getCitiesAround(mLat, mLong, mUnits)
            showWeather(weather)
        })

        mWeatherViewModel.dailyWeatherRepo.observe(viewLifecycleOwner, { dailyWeather ->
            setDailyAdapter(dailyWeather.list)
            probability_of_precipitation.text = getString(
                R.string.probability_of_precipitation,
                (dailyWeather.list[0].pop * 100).toString() + " %"
            )
            var rainHeight = "0"
            if (dailyWeather.list[0].rain != null) {
                rainHeight = dailyWeather.list[0].rain.h.toString()
            }
            rain_3h.text = getString(R.string.rain_last_3h, "$rainHeight mm")
            wind_direction.text = getString(
                R.string.wind_direction,
                dailyWeather.list[0].wind.deg.toString() + " deg"
            )
        })

        mWeatherViewModel.citiesAroundRepo.observe(viewLifecycleOwner, { citiesWeather ->
            setTopAdapter(citiesWeather.list)
        })
    }

    private fun errorObservers() {
        mWeatherViewModel.repoWeatherError.observe(viewLifecycleOwner, {
            getCityByLocation(mLat, mLong, mUnits)
            if (it.second) {
                showToast(it.first)
            }
        })

        mWeatherViewModel.repoDailyWeatherError.observe(viewLifecycleOwner, {
            getDailyWeatherByLocation(mLat, mLong, mUnits)
            if (it.second) {
                showToast(it.first)
            }
        })

        mWeatherViewModel.repoCitiesAroundError.observe(viewLifecycleOwner, {
            showToast(it)
        })
    }

    private fun showWeather(weather: Weather) {
        if (weather.name.isEmpty() || weather.sys.country.isEmpty()) {
            (activity as MainActivity).getLastLocation()
        } else {
            setWeatherDataInTextViews(weather)
        }
    }

    private fun getCityByLocation(lat: String, long: String, units: String) {
        mUnits = units
        mWeatherViewModel.getWeatherByLocation(lat, long, units)
    }

    private fun getWeather(cityName: String, units: String) {
        mUnits = units
        mWeatherViewModel.getWeather(cityName, units)
    }

    private fun getDailyWeatherByLocation(lat: String, long: String, units: String) {
        mUnits = units
        mWeatherViewModel.getDailyWeatherByLocation(lat, long, units)
    }

    private fun getDailyWeather(cityName: String, countryCode: String, units: String) {
        mUnits = units
        mWeatherViewModel.getDailyWeather(cityName, countryCode, units)
    }

    private fun setTimeToHour(time: Int): String {
        val calendar = Calendar.getInstance()
        val tz = TimeZone.getDefault()
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.timeInMillis))
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(time.toLong() * 1000))
    }

    private fun setTopAdapter(list: List<CitiesAroundData>) {
        mMainCitiesAdapter = MainCityAdapter(activity?.applicationContext, list, mNavController)
        val layoutManager =
            LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.HORIZONTAL, false)
        main_cities_recycler_view.layoutManager = layoutManager
        main_cities_recycler_view.adapter = mMainCitiesAdapter
    }

    private fun setDailyAdapter(dailyWeatherList: List<DailyWeatherData>) {

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

        val newList = dailyWeatherList.filterIndexed { index, _ -> index % 8 == 0 }

        mDailyAdapter = DailyWeatherAdapter(newList, minTempArray, maxTempArray, activity?.applicationContext)
        val layoutManager =
            LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.HORIZONTAL, false)
        daily_weather_recycler_view.layoutManager = layoutManager
        daily_weather_recycler_view.adapter = mDailyAdapter
    }


    private fun setWeatherDataInTextViews(weather: Weather) {
        main_image.setImageResource(R.drawable.ic_summer)
        if ((weather.main.temp <= 10 && mUnits == "metric") || (weather.main.temp <= 50 && mUnits == "imperial")) {
            main_image.setImageResource(R.drawable.ic_winter)
        }
        mCityName = weather.name
        (activity as MainActivity).mCityName = weather.name
        mCountryCode = weather.sys.country
        (activity as MainActivity).mCountryCode = weather.sys.country
        city_name.text =
            getString(R.string.city_name_country, weather.name, weather.sys.country)
        temp.text =
            getString(R.string.temp, weather.main.temp.toInt().toString())
        feels_like.text = getString(
            R.string.feels_like_temp,
            weather.main.feelsLike.toInt().toString() + mDegreeUnit
        )
        humidity.text =
            getString(R.string.humidity_new, weather.main.humidity.toString() + "%")
        wind_speed.text =
            getString(R.string.wind_speed_new, weather.wind.speed.toString() + mWindSpeed)
        feels_like.text = getString(
            R.string.feels_like_temp,
            weather.main.feelsLike.toInt().toString() + mDegreeUnit
        )
        sunrise.text = getString(R.string.sunrise_time, setTimeToHour(weather.sys.sunrise))
        sunset.text = getString(R.string.sunset_time, setTimeToHour(weather.sys.sunset))

        description.text = getString(R.string.description, weather.weather[0].description)
        var distanceUnits = "m"
        var visibilityValue = weather.visibility
        if (weather.visibility > 999) {
            visibilityValue = weather.visibility / 1000
            distanceUnits = "Km"
        }
        visibility.text =
            getString(R.string.visibility, visibilityValue.toString(), distanceUnits)
    }

    private fun setOnClickListener() {

        units.setOnClickListener {
            val start: Int
            val end: Int
            if (mCelsius) {
                start = units.text.length - 1
                end = units.text.length
                mUnits = IMPERIAL
                (activity as MainActivity).mUnits = IMPERIAL
                mDegreeUnit = "\u2109"
                mWindSpeed = IMPERIAL_DEGREE
                mCelsius = false
            } else {
                start = 0
                end = 1
                mUnits = METRIC
                (activity as MainActivity).mUnits = METRIC
                mDegreeUnit = "\u2103"
                mWindSpeed = METRIC_DEGREE
                mCelsius = true
            }

            setSpan(start, end)
            getWeather(mCityName, mUnits)
            getDailyWeather(mCityName, mCountryCode, mUnits)
        }

        val bundle = bundleOf("lat" to mLat.toFloat(), "long" to mLong.toFloat())

        location_icon.setOnClickListener {
            mNavController.navigate(R.id.action_cityFragment_to_googleMapsFragment, bundle)
        }

        show_attractions.setOnClickListener {
            mNavController.navigate(R.id.action_cityFragment_to_chooseAttractionFragment, bundle)
        }
    }

    private fun setSpan(start: Int, end: Int) {
        val span = SpannableString(resources.getString(R.string.units))
        span.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    activity?.applicationContext!!,
                    R.color.turquoise
                )
            ), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        units.text = span
    }

    private fun showToast(error: Int) {
        Toast.makeText(
            activity?.applicationContext,
            activity?.applicationContext?.resources?.getString(error),
            Toast.LENGTH_LONG
        ).show()
    }
}