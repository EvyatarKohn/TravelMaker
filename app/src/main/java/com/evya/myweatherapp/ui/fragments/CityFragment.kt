package com.evya.myweatherapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.evya.myweatherapp.Constants
import com.evya.myweatherapp.Constants.IMPERIAL
import com.evya.myweatherapp.Constants.METRIC
import com.evya.myweatherapp.MainData
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.CityFragmentLayoutBinding
import com.evya.myweatherapp.model.citiesaroundmodel.CitiesAroundData
import com.evya.myweatherapp.model.dailyweathermodel.DailyWeatherData
import com.evya.myweatherapp.model.weathermodel.Weather
import com.evya.myweatherapp.ui.MainActivity
import com.evya.myweatherapp.ui.adapters.CitiesAroundAdapter
import com.evya.myweatherapp.ui.adapters.DailyWeatherAdapter
import com.evya.myweatherapp.util.FireBaseEvents
import com.evya.myweatherapp.util.UtilsFunctions
import com.evya.myweatherapp.viewmodels.FavoritesViewModel
import com.evya.myweatherapp.viewmodels.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.grpc.okhttp.internal.Util
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CityFragment : Fragment(R.layout.city_fragment_layout) {
    private val mWeatherViewModel: WeatherViewModel by viewModels()
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

        if (arguments?.get(Constants.FROM_ADAPTER) == true) {
            mCityName = arguments?.get(Constants.CITY_NAME).toString()
            getWeather(mCityName, MainData.units)
            getDailyWeather(mCityName, mCountryCode, MainData.units)
        }

        if (arguments?.get(Constants.FROM_FAVORITES) == true) {
            mFromFavorites = true
            (activity as MainActivity).changeNavBarIndex(R.id.cityFragment, R.id.weather)
            MainData.lat = arguments?.get(Constants.LAT).toString()
            MainData.long = arguments?.get(Constants.LONG).toString()
            getCityByLocation(MainData.lat, MainData.long, MainData.units)
            getDailyWeatherByLocation(MainData.lat, MainData.long, MainData.units)
        }
    }

    private fun getWeatherData() {
        when {
            mWeather != null -> {
                showWeather(mWeather!!)
                getDailyWeather(mCityName, mCountryCode, MainData.units)
            }
            else -> {
                if (MainData.lat.isEmpty() || MainData.long.isEmpty()) {
                    getWeather(mCityName, MainData.units)
                    getDailyWeather(mCityName, mCountryCode, MainData.units)
                } else {
                    getCityByLocation(MainData.lat, MainData.long, MainData.units)
                    getDailyWeatherByLocation(MainData.lat, MainData.long, MainData.units)
                }
                mWeatherViewModel.getCitiesAround(MainData.lat, MainData.long, MainData.units)
            }
        }
    }

    private fun liveDataObservers() {
        mWeatherViewModel.weatherRepo.observe(viewLifecycleOwner, {
            if (it.first != null) {
                mFavWeather = it.first
                MainData.lat = it.first?.coord?.lat.toString()
                MainData.long = it.first?.coord?.lon.toString()
                mWeatherViewModel.getCitiesAround(MainData.lat, MainData.long, MainData.units)
                showWeather(it.first!!)
                checkIfAlreadyInFav()
            } else if (it.second.second) {
                getCityByLocation(MainData.lat, MainData.long, MainData.units)
                it.second.first?.let { it1 -> UtilsFunctions.showToast(it1, activity?.applicationContext) }
            }
        })

        mWeatherViewModel.dailyWeatherRepo.observe(viewLifecycleOwner, {
            if (it.first != null) {
                setDailyAdapter(it.first!!.list)
                mBinding.dailyWeather = it.first
            } else if (it.second.second) {
                getDailyWeatherByLocation(MainData.lat, MainData.long, MainData.units)
                it.second.first?.let { it1 -> UtilsFunctions.showToast(it1, activity?.applicationContext) }
            }

        })

        mWeatherViewModel.citiesAroundRepo.observe(viewLifecycleOwner, {
            if(it.first != null) {
                setTopAdapter(it.first!!.list.distinctBy { citiesAroundData ->
                    citiesAroundData.name
                })
            } else if (it.second.second) {
                it.second.first?.let { it1 -> UtilsFunctions.showToast(it1, activity?.applicationContext) }
            }
        })

        mWeatherViewModel.pollutionRepo.observe(viewLifecycleOwner, {
            if (it.first != null) {
                mBinding.pollution = it.first
            } else if (it.second.second) {
                it.second.first?.let { it1 -> UtilsFunctions.showToast(it1, activity?.applicationContext) }
            }
        })
    }

    private fun setBoldSpan() {
        UtilsFunctions.setSpanBold(0, 8, mBinding.humidity, activity?.applicationContext)
        UtilsFunctions.setSpanBold(0, 11, mBinding.windSpeed, activity?.applicationContext)
        UtilsFunctions.setSpanBold(0, 7, mBinding.sunrise, activity?.applicationContext)
        UtilsFunctions.setSpanBold(0, 6, mBinding.sunset, activity?.applicationContext)
        UtilsFunctions.setSpanBold(0, 7, mBinding.description, activity?.applicationContext)
        UtilsFunctions.setSpanBold(0, 10, mBinding.visibility, activity?.applicationContext)
        UtilsFunctions.setSpanBold(
            0,
            26,
            mBinding.probabilityOfPrecipitation,
            activity?.applicationContext
        )
        UtilsFunctions.setSpanBold(0, 13, mBinding.rain3h, activity?.applicationContext)
        UtilsFunctions.setSpanBold(0, 14, mBinding.airPollution, activity?.applicationContext)
    }

    private fun isRaining(description: String): Boolean {
        return (description == Constants.RAIN || description == Constants.LIGHT_RAIN ||
                description == Constants.SNOW || description == Constants.LIGHT_SNOW)
    }

    private fun isWinter(temp: Double): Boolean {
        return if (MainData.units == METRIC) {
            temp <= 10
        } else {
            temp <= 50
        }
    }

    private fun showWeather(weather: Weather) {
        if (weather.name.isEmpty() || weather.sys.country.isEmpty()) {
            (activity as MainActivity).getLastLocation()
        } else {
            setWeatherDataInTextViews(weather)
        }
    }

    private fun getCityByLocation(lat: String, long: String, units: String) {
        MainData.units = units
        mWeatherViewModel.getWeatherByLocation(lat, long, units)
    }

    private fun getWeather(cityName: String, units: String) {
        MainData.units = units
        mWeatherViewModel.getWeather(cityName, units)
    }

    private fun getDailyWeatherByLocation(lat: String, long: String, units: String) {
        MainData.units = units
        mWeatherViewModel.getDailyWeatherByLocation(lat, long, units)
    }

    private fun getDailyWeather(cityName: String, countryCode: String, units: String) {
        MainData.units = units
        mWeatherViewModel.getDailyWeather(cityName, countryCode, units)
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
        dailyWeatherList.forEach { dailyWeatherData ->
            minTempRawArray.add(dailyWeatherData.main.tempMin.toInt())
        }
        val minTempArray = minTempRawArray.sorted().take(5)

        val maxTempRawArray: ArrayList<Int> = ArrayList()
        dailyWeatherList.forEach { dailyWeatherData ->
            maxTempRawArray.add(dailyWeatherData.main.tempMax.toInt())
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

        mBinding.weather = weather

        mCityName = weather.name
        mCountryCode = weather.sys.country

        mBinding.mainImageRain.visibility =
            if (isRaining(weather.weather[0].description)) View.VISIBLE else View.GONE
    }

    private fun onClickListener() {
        mBinding.units.setOnClickListener {
            FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.ChangeTempUnits)
            val start: Int
            val end: Int
            if (mCelsius) {
                start = mBinding.units.text.length - 1
                end = mBinding.units.text.length
                MainData.units = IMPERIAL
                mCelsius = false

            } else {
                start = 0
                end = 1
                MainData.units = METRIC
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
            getWeather(mCityName, MainData.units)
            getDailyWeather(mCityName, mCountryCode, MainData.units)
        }

        mBinding.locationIcon.setOnClickListener {
            val bundle =
                bundleOf(
                    Constants.LAT to MainData.lat.toFloat(),
                    Constants.LONG to MainData.long.toFloat()
                )
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