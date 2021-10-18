package com.evya.myweatherapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.evya.myweatherapp.MainData
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.CityFragmentLayoutBinding
import com.evya.myweatherapp.model.citiesaroundmodel.CitiesAroundData
import com.evya.myweatherapp.model.dailyweathermodel.DailyWeatherData
import com.evya.myweatherapp.model.weathermodel.Weather
import com.evya.myweatherapp.ui.MainActivity
import com.evya.myweatherapp.ui.adapters.CitiesAroundAdapter
import com.evya.myweatherapp.ui.adapters.DailyWeatherAdapter
import com.evya.myweatherapp.util.UtilsFunctions
import com.evya.myweatherapp.viewmodels.WeatherViewModel
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
    private lateinit var mMainCitiesAdapter: CitiesAroundAdapter
    private lateinit var mDailyAdapter: DailyWeatherAdapter
    private var mWeather: Weather? = null
    private var mDegreeUnit = "\u2103"
    private var mWindSpeed = " m/s"
    private var mCelsius: Boolean = true
    private lateinit var mNavController: NavController
    private lateinit var mBinding: CityFragmentLayoutBinding
    private var mLoadGraph = true


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
        onClickListener()
        UtilsFunctions.setColorSpan(
            0,
            1,
            R.color.turquoise,
            R.string.units,
            mBinding.units,
            activity?.applicationContext
        )

        if (mUnits == "imperial") {
            mDegreeUnit = "\u2109"
            mWindSpeed = IMPERIAL_DEGREE
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
                if (MainData.mLat.isEmpty() || MainData.mLong.isEmpty()) {
                    getWeather(mCityName, mUnits)
                    getDailyWeather(mCityName, mCountryCode, mUnits)
                } else {
                    getCityByLocation(MainData.mLat, MainData.mLong, mUnits)
                    getDailyWeatherByLocation(MainData.mLat, MainData.mLong, mUnits)
                }
                mWeatherViewModel.getCitiesAround(MainData.mLat, MainData.mLong, mUnits)
            }
        }
    }

    private fun successObservers() {
        mWeatherViewModel.weatherRepo.observe(viewLifecycleOwner, { weather ->
            MainData.mLat = weather.coord.lat.toString()
            MainData.mLong = weather.coord.lon.toString()
            mWeatherViewModel.getCitiesAround(MainData.mLat, MainData.mLong, mUnits)
            showWeather(weather)
        })

        mWeatherViewModel.dailyWeatherRepo.observe(viewLifecycleOwner, { dailyWeather ->
            setDailyAdapter(dailyWeather.list)
            setGraph(dailyWeather.list)

            mBinding.probabilityOfPrecipitation.text = getString(
                R.string.probability_of_precipitation,
                (dailyWeather.list[0].pop * 100).toString() + " %"
            )
            UtilsFunctions.setSpanBold(
                0,
                26,
                mBinding.probabilityOfPrecipitation,
                activity?.applicationContext
            )
            var rainHeight = "0"
            var text = R.string.rain_last_3h
            if (dailyWeather.list[0].rain != null) {
                rainHeight = dailyWeather.list[0].rain.h.toString()
            } else if (dailyWeather.list[0].snow != null) {
                rainHeight = dailyWeather.list[0].snow.h.toString()
                text = R.string.snow_last_3h
            }
            mBinding.rain3h.text = getString(text, "$rainHeight mm")
            UtilsFunctions.setSpanBold(0, 13, mBinding.rain3h, activity?.applicationContext)
            mBinding.windDirection.text = getString(
                R.string.wind_direction,
                dailyWeather.list[0].wind.deg.toString() + " deg"
            )
            UtilsFunctions.setSpanBold(0, 15, mBinding.windDirection, activity?.applicationContext)
        })

        mWeatherViewModel.citiesAroundRepo.observe(viewLifecycleOwner, { citiesWeather ->
            setTopAdapter(citiesWeather.list.distinctBy {
                it.name
            })
        })
    }

    private fun isRaining(description: String): Boolean {
        return (description == "rain" || description == "light rain" || description == "snow" || description == "light snow")
    }

    private fun isWinter(temp: Double): Boolean {
        return if (mUnits == METRIC) {
            temp <= 10
        } else {
            temp <= 50
        }
    }

    private fun errorObservers() {
        mWeatherViewModel.repoWeatherError.observe(viewLifecycleOwner, {
            getCityByLocation(MainData.mLat, MainData.mLong, mUnits)
            if (it.second) {
                UtilsFunctions.showToast(it.first, activity?.applicationContext)
            }
        })

        mWeatherViewModel.repoDailyWeatherError.observe(viewLifecycleOwner, {
            getDailyWeatherByLocation(MainData.mLat, MainData.mLong, mUnits)
            if (it.second) {
                UtilsFunctions.showToast(it.first, activity?.applicationContext)
            }
        })

        mWeatherViewModel.repoCitiesAroundError.observe(viewLifecycleOwner, {
            UtilsFunctions.showToast(it, activity?.applicationContext)
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

    private fun setGraph(dailyWeatherList: List<DailyWeatherData>) {

/*        val series = ValueLineSeries()
        series.color = resources.getColor(R.color.turquoise, null)

        dailyWeatherList.forEach {
            series.addPoint(
                ValueLinePoint(
                    resources.getString(
                        R.string.graph_texts,
                        it.dtTxt.substring(8, 10),
                        it.dtTxt.substring(5, 7)
                    ), it.main.temp.toFloat()
                )
            )
        }
        mBinding.lineChart.apply {
            addSeries(series)
            isShowStandardValues = true
            isShowIndicator = true
            indicatorLineColor = resources.getColor(R.color.black, null)
            maxZoomX = 50.0F
            maxZoomY = 50.0F
            indicatorTextUnit = mDegreeUnit
            startAnimation()
        }*/
    }

    private fun setWeatherDataInTextViews(weather: Weather) {
        if (isWinter(weather.main.temp)) mBinding.mainImage.setImageResource(R.drawable.ic_winter) else mBinding.mainImage.setImageResource(
            R.drawable.ic_summer
        )
        mBinding.mainImageRain.visibility =
            if (isRaining(weather.weather[0].description)) View.VISIBLE else View.GONE

        mCityName = weather.name
        mCountryCode = weather.sys.country
        mBinding.cityName.text =
            getString(R.string.city_name_country, weather.name, weather.sys.country)
        mBinding.temp.text = getString(R.string.temp, weather.main.temp.toInt().toString())
        mBinding.feelsLike.text = getString(
            R.string.feels_like_temp,
            weather.main.feelsLike.toInt().toString() + mDegreeUnit
        )
        mBinding.humidity.text =
            getString(R.string.humidity_new, weather.main.humidity.toString() + "%")
        UtilsFunctions.setSpanBold(0, 8, mBinding.humidity, activity?.applicationContext)
        mBinding.windSpeed.text =
            getString(R.string.wind_speed_new, weather.wind.speed.toString() + mWindSpeed)
        UtilsFunctions.setSpanBold(0, 11, mBinding.windSpeed, activity?.applicationContext)
        mBinding.feelsLike.text = getString(
            R.string.feels_like_temp,
            weather.main.feelsLike.toInt().toString() + mDegreeUnit
        )
        mBinding.sunrise.text = getString(R.string.sunrise_time, setTimeToHour(weather.sys.sunrise))
        UtilsFunctions.setSpanBold(0, 7, mBinding.sunrise, activity?.applicationContext)
        mBinding.sunset.text = getString(R.string.sunset_time, setTimeToHour(weather.sys.sunset))
        UtilsFunctions.setSpanBold(0, 6, mBinding.sunset, activity?.applicationContext)

        mBinding.description.text = getString(R.string.description, weather.weather[0].description)
        UtilsFunctions.setSpanBold(0, 7, mBinding.description, activity?.applicationContext)
        var distanceUnits = "m"
        var visibilityValue = weather.visibility
        if (weather.visibility > 999) {
            visibilityValue = weather.visibility / 1000
            distanceUnits = "Km"
        }
        mBinding.visibility.text =
            getString(R.string.visibility, visibilityValue.toString(), distanceUnits)
        UtilsFunctions.setSpanBold(0, 10, mBinding.visibility, activity?.applicationContext)

    }

    private fun onClickListener() {
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

            UtilsFunctions.setColorSpan(
                start,
                end,
                R.color.turquoise,
                R.string.units,
                mBinding.units,
                activity?.applicationContext
            )
            mBinding.units.text
            getWeather(mCityName, mUnits)
            getDailyWeather(mCityName, mCountryCode, mUnits)
        }

        mBinding.locationIcon.setOnClickListener {
            val bundle =
                bundleOf("lat" to MainData.mLat.toFloat(), "long" to MainData.mLong.toFloat())
            mNavController.navigate(R.id.action_cityFragment_to_googleMapsFragment, bundle)
            (activity as MainActivity).changeNavBarIndex(R.id.googleMapsFragment, R.id.map)
        }
    }

    override fun onResume() {
        super.onResume()
        if ((activity as MainActivity).mApprovePermissions) {
            (activity as MainActivity).mApprovePermissions = false
            getWeatherData()
        }
    }
}