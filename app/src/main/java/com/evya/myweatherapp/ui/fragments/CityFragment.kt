package com.evya.myweatherapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.evya.myweatherapp.Constants.CITY_NAME
import com.evya.myweatherapp.Constants.FROM_FAVORITES
import com.evya.myweatherapp.Constants.FROM_TOP_ADAPTER
import com.evya.myweatherapp.Constants.IMPERIAL
import com.evya.myweatherapp.Constants.LAT
import com.evya.myweatherapp.Constants.LIGHT_RAIN
import com.evya.myweatherapp.Constants.LIGHT_SNOW
import com.evya.myweatherapp.Constants.LONG
import com.evya.myweatherapp.Constants.METRIC
import com.evya.myweatherapp.Constants.RAIN
import com.evya.myweatherapp.Constants.SNOW
import com.evya.myweatherapp.MainData.addedToFav
import com.evya.myweatherapp.MainData.approvedPermissions
import com.evya.myweatherapp.MainData.degreesUnits
import com.evya.myweatherapp.MainData.lat
import com.evya.myweatherapp.MainData.long
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.CityFragmentLayoutBinding
import com.evya.myweatherapp.firebaseanalytics.FireBaseEvents
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsNamesStrings.CHANGE_TEMP_UNITS
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsParamsStrings.PARAMS_TEMPERATURE_UNITS
import com.evya.myweatherapp.model.citiesaroundmodel.CitiesAroundData
import com.evya.myweatherapp.model.weathermodel.Daily
import com.evya.myweatherapp.model.weathermodel.Weather
import com.evya.myweatherapp.ui.MainActivity
import com.evya.myweatherapp.ui.adapters.CitiesAroundAdapter
import com.evya.myweatherapp.ui.adapters.DailyWeatherAdapter
import com.evya.myweatherapp.util.UtilsFunctions.Companion.setColorSpan
import com.evya.myweatherapp.util.UtilsFunctions.Companion.setSpanBold
import com.evya.myweatherapp.util.UtilsFunctions.Companion.showToast
import com.evya.myweatherapp.viewmodels.FavoritesViewModel
import com.evya.myweatherapp.viewmodels.NewWeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CityFragment : Fragment(R.layout.city_fragment_layout) {
    private val mWeatherViewModel: NewWeatherViewModel by viewModels()
    private val mFavoritesViewModel: FavoritesViewModel by viewModels()
    private var mCityName = "Ramat Gan"
    private var mCountryCode = "IL"
    private lateinit var mMainCitiesAdapter: CitiesAroundAdapter
    private lateinit var mDailyAdapter: DailyWeatherAdapter
    private var mWeather: Weather? = null
    private var mFavWeather: Weather? = null
    private var mCelsius = true
    private lateinit var mNavController: NavController
    private lateinit var mBinding: CityFragmentLayoutBinding
    private var mFromFavorites = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = CityFragmentLayoutBinding.bind(view)

        liveDataObservers()

        mNavController = Navigation.findNavController(view)
        onClickListener()
        setColorSpan(
            0,
            1,
            R.color.turquoise,
            R.string.units,
            mBinding.units,
            activity?.applicationContext
        )

        if (arguments?.getBoolean(FROM_TOP_ADAPTER) == true) {
            mCityName = arguments?.getString(CITY_NAME).toString()
           /* getWeather(mCityName, degreesUnits)
            getDailyWeather(mCityName, mCountryCode, degreesUnits)*/
        }

        if (arguments?.getBoolean(FROM_FAVORITES) == true) {
            mFromFavorites = true
            (activity as MainActivity).changeNavBarIndex(R.id.cityFragment, R.id.weather)
            lat = arguments?.getFloat(LAT).toString()
            long = arguments?.getFloat(LONG).toString()
            mWeatherViewModel.getWeatherByLocation(lat, long, degreesUnits)
        }
    }

    private fun getWeatherData() {
        when {
            mWeather != null -> {
                showWeather(mWeather!!)
            }
            else -> {
                if (lat.isEmpty() || long.isEmpty()) {
                    (activity as MainActivity).getLastLocation()
//                    mWeatherViewModel.getWeatherByLocation("32.083333", "34.7999968", degreesUnits)
                } else {
                    mWeatherViewModel.getWeatherByLocation(lat, long, degreesUnits)
                }
            }
        }
    }

    private fun liveDataObservers() {
        mWeatherViewModel.weatherData.observe(viewLifecycleOwner) {
            if (it.first != null) {
                mFavWeather = it.first
                mFavWeather?.lat?.let { lat ->
                    mFavWeather?.lon?.let { long ->
                        mWeatherViewModel.getCityNameByLocation(lat.toString(), long.toString())
                    }
                }
                mBinding.cityName.text = mFavWeather?.timezone?.substringAfter("/")
                mBinding.dailyExpectation.text = mFavWeather?.daily?.get(0)?.summary ?: ""
                lat = mFavWeather?.lat.toString()
                long = mFavWeather?.lon.toString()
//                mWeatherViewModel.getCitiesAround(lat, long, degreesUnits)
                mFavWeather?.let { it1 -> showWeather(it1) }
                mFavWeather?.daily?.let { it1 -> setDailyAdapter(it1) }
                mFavWeather?.let { it1 -> setWeatherDataInTextViews(it1) }
                checkIfAlreadyInFav()
            } else {
//                getCityByLocation(lat, long, degreesUnits)
                it.second?.let { it1 ->
                    showToast(context?.getString(it1, mCityName), activity?.applicationContext)
                }
            }
        }

        mWeatherViewModel.cityNameData.observe(viewLifecycleOwner) {
            mBinding.cityName.text = it.first?.get(0)?.name
        }

    }

    private fun setBoldSpan() {
        setSpanBold(0, 8, mBinding.humidity, activity?.applicationContext)
        setSpanBold(0, 11, mBinding.windSpeed, activity?.applicationContext)
        setSpanBold(0, 7, mBinding.sunrise, activity?.applicationContext)
        setSpanBold(0, 6, mBinding.sunset, activity?.applicationContext)
        setSpanBold(0, 7, mBinding.description, activity?.applicationContext)
        setSpanBold(0, 10, mBinding.visibility, activity?.applicationContext)
        setSpanBold(
            0, 26, mBinding.probabilityOfPrecipitation, activity?.applicationContext
        )
        setSpanBold(0, 13, mBinding.rain3h, activity?.applicationContext)
        setSpanBold(0, 14, mBinding.airPollution, activity?.applicationContext)
    }

    private fun isRaining(description: String) =
        description == RAIN || description == LIGHT_RAIN || description == SNOW || description == LIGHT_SNOW

    private fun isWinter(temp: Double) = if (degreesUnits == METRIC) {
        temp <= 10
    } else {
        temp <= 50
    }

    private fun showWeather(weather: Weather) {
        if (weather.lat == 0.0 || weather.lon == 0.0) {
            (activity as MainActivity).getLastLocation()
        } else {
            setWeatherDataInTextViews(weather)
        }
    }

    private fun setTopAdapter(list: List<CitiesAroundData>) {
        mMainCitiesAdapter = CitiesAroundAdapter(activity?.applicationContext, list, mNavController)
        val layoutManager =
            LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.HORIZONTAL, false)
        mBinding.mainCitiesRecyclerView.layoutManager = layoutManager
        mBinding.mainCitiesRecyclerView.adapter = mMainCitiesAdapter
    }

    private fun setDailyAdapter(dailyWeatherList: List<Daily>) {

        val minTempRawArray: ArrayList<Int> = ArrayList()
        dailyWeatherList.forEach { dailyWeatherData ->
            minTempRawArray.add(dailyWeatherData.temp.min.toInt())
        }
        val minTempArray = minTempRawArray.sorted().take(5)

        val maxTempRawArray: ArrayList<Int> = ArrayList()
        dailyWeatherList.forEach { dailyWeatherData ->
            maxTempRawArray.add(dailyWeatherData.temp.max.toInt())
        }
        val maxTempArray = maxTempRawArray.sortedDescending().take(5)

//        val newList = dailyWeatherList.filterIndexed { index, _ -> index % 8 == 0 }
        val newList = dailyWeatherList.subList(0, 5)

        mDailyAdapter =
            DailyWeatherAdapter(newList, minTempArray, maxTempArray, activity?.applicationContext)
        val layoutManager =
            LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.HORIZONTAL, false)
        mBinding.dailyWeatherRecyclerView.layoutManager = layoutManager
        mBinding.dailyWeatherRecyclerView.adapter = mDailyAdapter
    }

    private fun setWeatherDataInTextViews(weather: Weather) {
        if (isWinter(weather.current.temp)) {
            mBinding.mainImage.setImageResource(R.drawable.ic_winter)
        } else {
            mBinding.mainImage.setImageResource(R.drawable.ic_summer)
        }

        mBinding.weather = weather

        mCityName = weather.timezone.substringAfter("/")
        mCountryCode = weather.timezone.substringBefore("/")

        mBinding.mainImageRain.isVisible = isRaining(weather.current.weather[0].description)
    }

    private fun onClickListener() {
        mBinding.apply {
            units.setOnClickListener {
                val params = bundleOf(
                    PARAMS_TEMPERATURE_UNITS.paramsName to if (!mCelsius) "Celsius" else "Fahrenheit"
                )
                FireBaseEvents.sendFireBaseCustomEvents(CHANGE_TEMP_UNITS.eventName, params)
                val start: Int
                val end: Int
                if (mCelsius) {
                    start = units.text.length - 1
                    end = units.text.length
                    degreesUnits = IMPERIAL
                    mCelsius = false

                } else {
                    start = 0
                    end = 1
                    degreesUnits = METRIC
                    mCelsius = true
                }

                setColorSpan(
                    start,
                    end,
                    R.color.turquoise,
                    R.string.units,
                    units,
                    activity?.applicationContext
                )
                units.text
                /*            getWeather(mCityName, degreesUnits)
            getDailyWeather(mCityName, mCountryCode, degreesUnits)*/
                mWeatherViewModel.getWeatherByLocation(lat, long, degreesUnits)
            }

            locationIcon.setOnClickListener {
                val bundle =
                    bundleOf(
                        LAT to lat.toFloat(),
                        LONG to long.toFloat(),
                        "currentCity" to mCityName
                    )
                mNavController.navigate(R.id.action_cityFragment_to_googleMapsFragment, bundle)
                (activity as MainActivity).changeNavBarIndex(R.id.googleMapsFragment, R.id.map)
            }

            favoriteImg.setOnClickListener {
                if (!addedToFav) {
                    favoriteImg.setBackgroundResource(R.drawable.ic_red_heart)
                    addedToFav = true
                    mFavWeather?.cityName = mBinding.cityName.text.toString()
                    mFavWeather?.let { it1 -> mFavoritesViewModel.addCityDataToDB(it1) }
                } else {
                    favoriteImg.setBackgroundResource(R.drawable.ic_empty_heart)
                    addedToFav = false
                    mFavWeather?.cityName?.let { cityName ->
                        mFavoritesViewModel.removeCityDataFromDB(cityName)
                    }
                }
            }
        /*    alertSignImg.setOnClickListener {
                mFavWeather?.alerts?.let { alertsList ->
                    (activity as MainActivity).openAlertFragment(alertsList)
                } ?: showToast("No alerts in this area", (activity as MainActivity))
            }*/
        }
    }

    private fun checkIfAlreadyInFav() {
        mFavWeather?.let { mFavoritesViewModel.setWeather(it) }
        mFavoritesViewModel.checkIfAlreadyAddedToDB.observe(viewLifecycleOwner) {
            if (it) {
                mBinding.favoriteImg.setBackgroundResource(R.drawable.ic_red_heart)
            } else {
                mBinding.favoriteImg.setBackgroundResource(R.drawable.ic_empty_heart)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (approvedPermissions && !mFromFavorites) {
            approvedPermissions = false
            getWeatherData()
        }
    }
}