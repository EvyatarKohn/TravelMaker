package com.evya.myweatherapp.ui.fragments

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.evya.myweatherapp.R
import com.evya.myweatherapp.model.citiesaroundmodel.CitiesAroundData
import com.evya.myweatherapp.model.dailyweathermodel.DailyWeatherData
import com.evya.myweatherapp.model.weathermodel.Weather
import com.evya.myweatherapp.ui.MainActivity
import com.evya.myweatherapp.ui.MainListener
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
    private val mViewModel: WeatherViewModel by viewModels()
    private var mCityName = "Tel-aviv"
    private var mCountryCode = "IL"
    private lateinit var mUnits: String
    private lateinit var mLat: String
    private lateinit var mLong: String
    private lateinit var mMainListener: MainListener
    private lateinit var mMainCitiesAdapter: MainCityAdapter
    private lateinit var mDailyAdapter: DailyWeatherAdapter
    private lateinit var mLocationBtn: TextView
    private var mWeather: Weather? = null
    private var mDegreeUnit = "\u2103"
    private var mWindSpeed = " m/s"
    private lateinit var mUnitsText: TextView
    private var mCelsius: Boolean = true

    companion object {
        fun newInstance(
            lat: String,
            long: String,
            cityName: String,
            units: String,
            mainListener: MainListener
        ) = CityFragment().apply {
            mCityName = cityName
            mUnits = units
            mLat = lat
            mLong = long
            mMainListener = mainListener
            mWeather = null
        }

        fun newInstance(weather: Weather, units: String, mainListener: MainListener) =
            CityFragment().apply {
                mWeather = weather
                mUnits = units
                mMainListener = mainListener
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (mUnits == "imperial") {
            mDegreeUnit = "\u2109"
            mWindSpeed = " miles/hr"
        }


        if (mWeather != null) {
            showWeather(mWeather!!)
            getDailyWeather(mCityName, mCountryCode, mUnits)
        } else {
            if (mLat.isEmpty() || mLong.isEmpty()) {
                getWeather(mCityName, mUnits)
                getDailyWeather(mCityName, mCountryCode, mUnits)
            } else {
                getCityByLocation(mLat, mLong, mUnits)
                getDailyWeatherByLocation(mLat, mLong, mUnits)
            }
            mViewModel.getCitiesAround(mLat, mLong, mUnits)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.city_fragment_layout, container, false)

        mUnitsText = v.findViewById(R.id.units)
        setSpan(0, 1)
        mUnitsText.setOnClickListener {
            val start: Int
            val end: Int
            if (mCelsius) {
                start = mUnitsText.text.length - 1
                end = mUnitsText.text.length
                mUnits = "imperial"
                mDegreeUnit = "\u2109"
                mWindSpeed = " miles/hr"
                mCelsius = false
            } else {
                start = 0
                end = 1
                mUnits = "metric"
                mDegreeUnit = "\u2103"
                mWindSpeed = " m/s"
                mCelsius = true
            }

            setSpan(start, end)
            getWeather(mCityName, mUnits)
            getDailyWeather(mCityName, mCountryCode, mUnits)
        }

        mLocationBtn = v.findViewById(R.id.location_icon)
        mLocationBtn.setOnClickListener {
            mMainListener.replaceToCustomCityFragment()
        }

        mViewModel.weatherRepo.observe(viewLifecycleOwner, { weather ->
            showWeather(weather)
        })

        mViewModel.dailyWeatherRepo.observe(viewLifecycleOwner, { dailyWeather ->
            setDailyAdapter(dailyWeather.list)
        })

        mViewModel.citiesAroundRepo.observe(viewLifecycleOwner, { citiesWeather ->
            setTopAdapter(citiesWeather.list)

        })

        return v
    }

    private fun setSpan(start: Int, end: Int) {
        val span = SpannableString(resources.getString(R.string.units))
        span.setSpan(ForegroundColorSpan(ContextCompat.getColor(activity?.applicationContext!!, R.color.turquoise)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        mUnitsText.text = span
    }

    private fun showWeather(weather: Weather) {
        if (weather.name.isEmpty() || weather.sys.country.isEmpty()) {
            (activity as MainActivity).getLastLocation()
        } else {
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

    private fun setTopAdapter(list: List<CitiesAroundData>) {
        mMainCitiesAdapter = MainCityAdapter(activity?.applicationContext, list, mMainListener)
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

        mDailyAdapter = DailyWeatherAdapter(newList, minTempArray, maxTempArray)
        val layoutManager =
            LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.HORIZONTAL, false)
        daily_weather_recycler_view.layoutManager = layoutManager
        daily_weather_recycler_view.adapter = mDailyAdapter
    }
}