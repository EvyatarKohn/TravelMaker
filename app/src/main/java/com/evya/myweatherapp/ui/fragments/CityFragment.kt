package com.evya.myweatherapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.evya.myweatherapp.Constants.IMPERIAL
import com.evya.myweatherapp.Constants.IMPERIAL_DEGREE
import com.evya.myweatherapp.Constants.KM
import com.evya.myweatherapp.Constants.METRIC
import com.evya.myweatherapp.Constants.METRIC_DEGREE
import com.evya.myweatherapp.Constants.MILE
import com.evya.myweatherapp.MainData
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.CityFragmentLayoutBinding
import com.evya.myweatherapp.model.citiesaroundmodel.CitiesAroundData
import com.evya.myweatherapp.model.dailyweathermodel.DailyWeatherData
import com.evya.myweatherapp.model.pollution.Pollution
import com.evya.myweatherapp.model.weathermodel.Weather
import com.evya.myweatherapp.ui.MainActivity
import com.evya.myweatherapp.ui.adapters.CitiesAroundAdapter
import com.evya.myweatherapp.ui.adapters.DailyWeatherAdapter
import com.evya.myweatherapp.util.FireBaseEvents
import com.evya.myweatherapp.util.UtilsFunctions
import com.evya.myweatherapp.viewmodels.FavoritesViewModel
import com.evya.myweatherapp.viewmodels.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CityFragment : Fragment(R.layout.city_fragment_layout) {
    private val mWeatherViewModel: WeatherViewModel by viewModels()
    private val mFavoritesViewModel: FavoritesViewModel by viewModels()
    private var mCityName = ""
    private var mCountryCode = "IL"
    private var mUnits: String = METRIC
    private lateinit var mMainCitiesAdapter: CitiesAroundAdapter
    private lateinit var mDailyAdapter: DailyWeatherAdapter
    private var mWeather: Weather? = null
    private var mFavWeather: Weather? = null
    private var mDegreeUnit = "\u2103"
    private var mWindSpeed = METRIC_DEGREE
    private var mVisibilityUnits = KM
    private var mCelsius: Boolean = true
    private lateinit var mNavController: NavController
    private lateinit var mBinding: CityFragmentLayoutBinding
    private var mPollution = "Good"
    private var mFromFavorites = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = CityFragmentLayoutBinding.bind(view)

        successObservers()
        errorObservers()
        mWeatherViewModel.getAirPollution(MainData.lat, MainData.long)

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

        if (arguments?.get("fromFavorites") == true) {
            mFromFavorites = true
            (activity as MainActivity).changeNavBarIndex(R.id.cityFragment, R.id.weather)
            MainData.lat = arguments?.get("lat").toString()
            MainData.long = arguments?.get("long").toString()
            getCityByLocation(MainData.lat, MainData.long, mUnits)
            getDailyWeatherByLocation(MainData.lat, MainData.long, mUnits)
        }
    }

    private fun getWeatherData() {
        when {
            mWeather != null -> {
                showWeather(mWeather!!)
                getDailyWeather(mCityName, mCountryCode, mUnits)
            }
            else -> {
                if (MainData.lat.isEmpty() || MainData.long.isEmpty()) {
                    getWeather(mCityName, mUnits)
                    getDailyWeather(mCityName, mCountryCode, mUnits)
                } else {
                    getCityByLocation(MainData.lat, MainData.long, mUnits)
                    getDailyWeatherByLocation(MainData.lat, MainData.long, mUnits)
                }
                mWeatherViewModel.getCitiesAround(MainData.lat, MainData.long, mUnits)
            }
        }
    }

    private fun successObservers() {
        mWeatherViewModel.weatherRepo.observe(viewLifecycleOwner, { weather ->
            mFavWeather = weather
            MainData.lat = weather.coord.lat.toString()
            MainData.long = weather.coord.lon.toString()
            mWeatherViewModel.getCitiesAround(MainData.lat, MainData.long, mUnits)
            showWeather(weather)
            checkIfAlreadyInFav()
        })

        mWeatherViewModel.dailyWeatherRepo.observe(viewLifecycleOwner, { dailyWeather ->
            setDailyAdapter(dailyWeather.list)

            mBinding.probabilityOfPrecipitation.text = getString(
                R.string.probability_of_precipitation,
                (dailyWeather.list[0].pop * 100).roundToInt().toString() + "%"
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
        })

        mWeatherViewModel.citiesAroundRepo.observe(viewLifecycleOwner, { citiesWeather ->
            setTopAdapter(citiesWeather.list.distinctBy {
                it.name
            })
        })

        mWeatherViewModel.pollutionRepo.observe(viewLifecycleOwner, { pollution ->
            definePollution(pollution)
        })
    }


    private fun definePollution(pollution: Pollution) {

        if (pollution.list[0].components.no2 > 400 || pollution.list[0].components.pm10 > 180 ||
            pollution.list[0].components.o3 > 240 || pollution.list[0].components.pm25 > 110
        ) {
            mPollution = "Very Poor"
        } else if ((pollution.list[0].components.no2 > 200 && pollution.list[0].components.no2 < 399) ||
            (pollution.list[0].components.pm10 > 90 && pollution.list[0].components.pm10 < 179) ||
            (pollution.list[0].components.o3 > 180 && pollution.list[0].components.o3 < 239) ||
            (pollution.list[0].components.pm25 > 55 && pollution.list[0].components.pm25 < 109)
        ) {
            mPollution = "Poor"
        } else if ((pollution.list[0].components.no2 > 100 && pollution.list[0].components.no2 < 199) ||
            (pollution.list[0].components.pm10 > 50 && pollution.list[0].components.pm10 < 89) ||
            (pollution.list[0].components.o3 > 120 && pollution.list[0].components.o3 < 179) ||
            (pollution.list[0].components.pm25 > 30 && pollution.list[0].components.pm25 < 54)
        ) {
            mPollution = "Moderate"
        } else if ((pollution.list[0].components.no2 > 50 && pollution.list[0].components.no2 < 99) ||
            (pollution.list[0].components.pm10 > 25 && pollution.list[0].components.pm10 < 49) ||
            (pollution.list[0].components.o3 > 60 && pollution.list[0].components.o3 < 119) ||
            (pollution.list[0].components.pm25 > 15 && pollution.list[0].components.pm25 < 29)
        ) {
            mPollution = "Fair"
        } else if ((pollution.list[0].components.no2 > 0 && pollution.list[0].components.no2 < 49) ||
            (pollution.list[0].components.pm10 > 0 && pollution.list[0].components.pm10 < 24) ||
            (pollution.list[0].components.o3 > 0 && pollution.list[0].components.o3 < 59) ||
            (pollution.list[0].components.pm25 > 0 && pollution.list[0].components.pm25 < 14)
        ) {
            mPollution = "Good"
        }

        mBinding.airPollution.text = getString(
            R.string.air_pollution,
            mPollution
        )
        UtilsFunctions.setSpanBold(0, 14, mBinding.airPollution, activity?.applicationContext)
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
            getCityByLocation(MainData.lat, MainData.long, mUnits)
            if (it.second) {
                UtilsFunctions.showToast(it.first, activity?.applicationContext)
            }
        })

        mWeatherViewModel.repoDailyWeatherError.observe(viewLifecycleOwner, {
            getDailyWeatherByLocation(MainData.lat, MainData.long, mUnits)
            if (it.second) {
                UtilsFunctions.showToast(it.first, activity?.applicationContext)
            }
        })

        mWeatherViewModel.repoCitiesAroundError.observe(viewLifecycleOwner, {
            UtilsFunctions.showToast(it, activity?.applicationContext)
        })

        mWeatherViewModel.repoPollutionError.observe(viewLifecycleOwner, {
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

    private fun setWeatherDataInTextViews(weather: Weather) {
        if (isWinter(weather.main.temp)) {
            mBinding.mainImage.setImageResource(R.drawable.ic_winter)
        } else {
            mBinding.mainImage.setImageResource(R.drawable.ic_summer)
        }

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
        val visibilityValue = if (mUnits == IMPERIAL) {
            weather.visibility / 1609
        } else {
            weather.visibility / 1000
        }

        mBinding.visibility.text =
            getString(R.string.visibility, visibilityValue.toString(), mVisibilityUnits)
        UtilsFunctions.setSpanBold(0, 10, mBinding.visibility, activity?.applicationContext)

    }

    private fun onClickListener() {
        mBinding.units.setOnClickListener {
            FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.ChangeTempUnits)
            val start: Int
            val end: Int
            if (mCelsius) {
                start = mBinding.units.text.length - 1
                end = mBinding.units.text.length
                mUnits = IMPERIAL
                mDegreeUnit = "\u2109"
                mWindSpeed = IMPERIAL_DEGREE
                mCelsius = false
                mVisibilityUnits = MILE

            } else {
                start = 0
                end = 1
                mUnits = METRIC
                mDegreeUnit = "\u2103"
                mWindSpeed = METRIC_DEGREE
                mCelsius = true
                mVisibilityUnits = KM
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
                bundleOf("lat" to MainData.lat.toFloat(), "long" to MainData.long.toFloat())
            mNavController.navigate(R.id.action_cityFragment_to_googleMapsFragment, bundle)
            (activity as MainActivity).changeNavBarIndex(R.id.googleMapsFragment, R.id.map)
        }

        mBinding.favoriteImg.setOnClickListener {
            if (!MainData.addedToFav) {
                mBinding.favoriteImg.setBackgroundResource(R.drawable.ic_red_heart)
                MainData.addedToFav = true
                mFavoritesViewModel.addCityDataToDB(mFavWeather!!)
            } else {
                mBinding.favoriteImg.setBackgroundResource(R.drawable.ic_empty_heart)
                MainData.addedToFav = false
                mFavWeather?.name?.let { it1 -> mFavoritesViewModel.removeCityDataFromDB(it1) }
            }
        }
    }

    private fun checkIfAlreadyInFav() {
        mFavoritesViewModel.setWeather(mFavWeather!!)
        mFavoritesViewModel.checkIfAlreadyAddedToDB.observe(viewLifecycleOwner, {
            if (it) {
                mBinding.favoriteImg.setBackgroundResource(R.drawable.ic_red_heart)
            } else {
                mBinding.favoriteImg.setBackgroundResource(R.drawable.ic_empty_heart)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if ((activity as MainActivity).mApprovePermissions && !mFromFavorites) {
            (activity as MainActivity).mApprovePermissions = false
            getWeatherData()
        }
    }
}