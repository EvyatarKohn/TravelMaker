package com.evya.myweatherapp.ui.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.CityFragmentLayoutBinding
import com.evya.myweatherapp.model.citiesaroundmodel.CitiesAroundData
import com.evya.myweatherapp.model.dailyweathermodel.DailyWeatherData
import com.evya.myweatherapp.model.weathermodel.Weather
import com.evya.myweatherapp.ui.MainActivity
import com.evya.myweatherapp.ui.adapters.CitiesAroundAdapter
import com.evya.myweatherapp.ui.adapters.DailyWeatherAdapter
import com.evya.myweatherapp.viewmodels.WeatherViewModel
import com.evya.myweatherapp.util.CustomTypeFaceSpan
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CityFragment : Fragment(R.layout.city_fragment_layout) {
    private val mWeatherViewModel: WeatherViewModel by viewModels()
    private var mCityName = ""
    private var mCountryCode = "IL"
    private var mUnits: String = METRIC
    private var mLat: String = "" //"32.083333"
    private var mLong: String = "" //"34.7999968"
    private lateinit var mMainCitiesAdapter: CitiesAroundAdapter
    private lateinit var mDailyAdapter: DailyWeatherAdapter
    private var mWeather: Weather? = null
    private var mDegreeUnit = "\u2103"
    private var mWindSpeed = " m/s"
    private var mCelsius: Boolean = true
    private lateinit var mNavController: NavController
    private lateinit var mBinding: CityFragmentLayoutBinding


    companion object {
        const val IMPERIAL = "imperial"
        const val METRIC = "metric"
        const val IMPERIAL_DEGREE = " miles/hr"
        const val METRIC_DEGREE = " m/s"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = CityFragmentLayoutBinding.bind(view)

        successObservers()
        errorObservers()

        mNavController = Navigation.findNavController(view)
        setOnClickListener()
        setSpan(0, 1)

        if (mUnits == "imperial") {
            mDegreeUnit = "\u2109"
            mWindSpeed = " miles/hr"
        }

        if (arguments?.get("fromMaps") == true) {
            mLat = arguments?.get("lat").toString()
            mLong = arguments?.get("long").toString()
            getWeatherData()
        }

        if (arguments?.get("fromTopAdapter") == true) {
            mCityName = arguments?.get("cityName").toString()
            getWeather(mCityName, mUnits)
            getDailyWeather(mCityName, mCountryCode, mUnits)
        }
    }

    private fun getWeatherData() {
        when {
            mWeather != null -> {
                showWeather(mWeather!!)
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
    
    private fun successObservers() {
        mWeatherViewModel.weatherRepo.observe(viewLifecycleOwner, { weather ->
            mLat = weather.coord.lat.toString()
            mLong = weather.coord.lon.toString()
            mWeatherViewModel.getCitiesAround(mLat, mLong, mUnits)
            showWeather(weather)
        })

        mWeatherViewModel.dailyWeatherRepo.observe(viewLifecycleOwner, { dailyWeather ->
            setDailyAdapter(dailyWeather.list)
            mBinding.probabilityOfPrecipitation.text = getString(
                R.string.probability_of_precipitation,
                (dailyWeather.list[0].pop * 100).toString() + " %"
            )
            setBold(0, 26, mBinding.probabilityOfPrecipitation)
            var rainHeight = "0"
            if (dailyWeather.list[0].rain != null) {
                rainHeight = dailyWeather.list[0].rain.h.toString()
            }
            mBinding.rain3h.text = getString(R.string.rain_last_3h, "$rainHeight mm")
            setBold(0, 13, mBinding.rain3h)
            mBinding.windDirection.text = getString(
                R.string.wind_direction,
                dailyWeather.list[0].wind.deg.toString() + " deg"
            )
            setBold(0, 15, mBinding.windDirection)
        })

        mWeatherViewModel.citiesAroundRepo.observe(viewLifecycleOwner, { citiesWeather ->
            setTopAdapter(citiesWeather.list.distinctBy {
                it.name
            })
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
        mMainCitiesAdapter = CitiesAroundAdapter(activity?.applicationContext, list, mNavController)
        val layoutManager =
            LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.HORIZONTAL, false)
        mBinding.mainCitiesRecyclerView.layoutManager = layoutManager
        mBinding.mainCitiesRecyclerView.adapter = mMainCitiesAdapter
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

        mDailyAdapter =
            DailyWeatherAdapter(newList, minTempArray, maxTempArray, activity?.applicationContext)
        val layoutManager =
            LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.HORIZONTAL, false)
        mBinding.dailyWeatherRecyclerView.layoutManager = layoutManager
        mBinding.dailyWeatherRecyclerView.adapter = mDailyAdapter
    }


    private fun setWeatherDataInTextViews(weather: Weather) {
        mBinding.mainImage.setImageResource(R.drawable.ic_summer)
        if ((weather.main.temp <= 10 && mUnits == "metric") || (weather.main.temp <= 50 && mUnits == "imperial")) {
            mBinding.mainImage.setImageResource(R.drawable.ic_winter)
        }
        mCityName = weather.name
        mCountryCode = weather.sys.country
        mBinding.cityName.text = getString(R.string.city_name_country, weather.name, weather.sys.country)
        mBinding.temp.text = getString(R.string.temp, weather.main.temp.toInt().toString())
        mBinding.feelsLike.text = getString(
            R.string.feels_like_temp,
            weather.main.feelsLike.toInt().toString() + mDegreeUnit
        )
        mBinding.humidity.text = getString(R.string.humidity_new, weather.main.humidity.toString() + "%")
        setBold(0, 8, mBinding.humidity)
        mBinding.windSpeed.text =
            getString(R.string.wind_speed_new, weather.wind.speed.toString() + mWindSpeed)
        setBold(0, 11, mBinding.windSpeed)
        mBinding.feelsLike.text = getString(
            R.string.feels_like_temp,
            weather.main.feelsLike.toInt().toString() + mDegreeUnit
        )
        mBinding.sunrise.text = getString(R.string.sunrise_time, setTimeToHour(weather.sys.sunrise))
        setBold(0, 7, mBinding.sunrise)
        mBinding.sunset.text = getString(R.string.sunset_time, setTimeToHour(weather.sys.sunset))
        setBold(0, 6, mBinding.sunset)

        mBinding.description.text = getString(R.string.description, weather.weather[0].description)
        setBold(0, 7, mBinding.description)
        var distanceUnits = "m"
        var visibilityValue = weather.visibility
        if (weather.visibility > 999) {
            visibilityValue = weather.visibility / 1000
            distanceUnits = "Km"
        }
        mBinding.visibility.text = getString(R.string.visibility, visibilityValue.toString(), distanceUnits)
        setBold(0, 10, mBinding.visibility)

    }

    private fun setOnClickListener() {
        mBinding.units.setOnClickListener {
            val start: Int
            val end: Int
            if (mCelsius) {
                start = mBinding.units.text.length - 1
                end = mBinding.units.text.length
                mUnits = IMPERIAL
                mDegreeUnit = "\u2109"
                mWindSpeed = IMPERIAL_DEGREE
                mCelsius = false
            } else {
                start = 0
                end = 1
                mUnits = METRIC
                mDegreeUnit = "\u2103"
                mWindSpeed = METRIC_DEGREE
                mCelsius = true
            }

            setSpan(start, end)
            getWeather(mCityName, mUnits)
            getDailyWeather(mCityName, mCountryCode, mUnits)
        }

        mBinding.locationIcon.setOnClickListener {
            val bundle = bundleOf("lat" to mLat.toFloat(), "long" to mLong.toFloat())
            mNavController.navigate(R.id.action_cityFragment_to_googleMapsFragment, bundle)
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
        mBinding.units.text = span
    }

    private fun setBold(start: Int, end: Int, textView: TextView) {
        val span = SpannableString(textView.text.toString())
        val font = Typeface.createFromAsset(context?.assets, "font/product_sans_bold.ttf")
        span.setSpan(CustomTypeFaceSpan("", font), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        textView.text = span
    }

    private fun showToast(error: Int) {
        Toast.makeText(
            activity?.applicationContext,
            activity?.applicationContext?.resources?.getString(error),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onResume() {
        super.onResume()
        if ((activity as MainActivity).mApprovePermissions) {
            (activity as MainActivity).mApprovePermissions = false
            mLat = (activity as MainActivity).mLat
            mLong = (activity as MainActivity).mLong
            getWeatherData()
        }
    }
}