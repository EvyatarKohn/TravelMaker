package com.evya.myweatherapp.ui.fragments

import android.os.Bundle
import android.content.Intent
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.evya.myweatherapp.Constants.ALERTS
import com.evya.myweatherapp.Constants.CITY_NAME
import com.evya.myweatherapp.Constants.FROM_ALERTS
import com.evya.myweatherapp.Constants.FROM_FAVORITES
import com.evya.myweatherapp.Constants.FROM_GOOGLE_MAPS
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
import com.evya.myweatherapp.MainData.cityName
import com.evya.myweatherapp.MainData.degreesUnits
import com.evya.myweatherapp.MainData.lat
import com.evya.myweatherapp.MainData.long
import com.evya.myweatherapp.MainData.weather
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.CityFragmentLayoutBinding
import com.evya.myweatherapp.firebaseanalytics.FireBaseEvents
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsNamesStrings.CHANGE_TEMP_UNITS
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsNamesStrings.CLICK_ON_INTERSTITIAL_AD
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsNamesStrings.ON_INTERSTITIAL_AD_DISMISSED_FULL_SCREEN_CONTENT
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsNamesStrings.ON_INTERSTITIAL_AD_FAILED_TO_LOAD
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsNamesStrings.ON_INTERSTITIAL_AD_FAILED_TO_SHOW_FULL_SCREEN_CONTENT
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsNamesStrings.ON_INTERSTITIAL_AD_IMPRESSION
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsNamesStrings.ON_INTERSTITIAL_AD_LOADED
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsNamesStrings.ON_INTERSTITIAL_AD_SHOWED_FULL_SCREEN_CONTENT
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsParamsStrings.PARAMS_FAILED_TO_LOAD_INTERSTITIAL_AD
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsParamsStrings.PARAMS_TEMPERATURE_UNITS
import com.evya.myweatherapp.model.citiesaroundmodel.CitiesAroundData
import com.evya.myweatherapp.model.dailyweathermodel.DailyWeather
import com.evya.myweatherapp.model.weathermodel.Daily
import com.evya.myweatherapp.model.weathermodel.Weather
import com.evya.myweatherapp.ui.MainActivity
import com.evya.myweatherapp.ui.adapters.CitiesAroundAdapter
import com.evya.myweatherapp.ui.adapters.DailyWeatherAdapter
import com.evya.myweatherapp.ui.adapters.HourlyWeatherAdapter
import com.evya.myweatherapp.ui.DayPlanActivity
import com.evya.myweatherapp.util.bestOutdoorWindow
import com.evya.myweatherapp.util.dashboardMetric
import com.evya.myweatherapp.util.forecastTime
import com.evya.myweatherapp.util.relativeTimeEnglish
import com.evya.myweatherapp.util.weatherIcon
import com.google.android.material.snackbar.Snackbar
import kotlin.math.roundToInt
import com.evya.myweatherapp.ui.dialogs.DailyDialog
import com.evya.myweatherapp.util.forecastScrollTarget
import com.evya.myweatherapp.util.UtilsFunctions.Companion.setColorSpan
import com.evya.myweatherapp.util.UtilsFunctions.Companion.showToast
import com.evya.myweatherapp.viewmodels.CitiesViewModel
import com.evya.myweatherapp.viewmodels.NewWeatherViewModel
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CityFragment : Fragment(R.layout.city_fragment_layout) {

    companion object {
        private const val FIVE_HOURS = 18_000_000  // every 5 hour (18,000,000 millisecond) make a new call
    }

    private val mWeatherViewModel: NewWeatherViewModel by viewModels()
    private val mCitiesViewModel: CitiesViewModel by viewModels()
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
    var getSpecificDayWeather: ((time: Int) -> Unit)? = null
    private var mInterstitialAd: InterstitialAd? = null
    private var showAd = 0
    private var errorSnackbar: Snackbar? = null
    private val scrollListener = object : OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val manager = recyclerView.layoutManager as? LinearLayoutManager ?: return
            val itemCount = recyclerView.adapter?.itemCount ?: 0
            val canGoTowardStart = itemCount > 0 &&
                manager.findFirstVisibleItemPosition() != RecyclerView.NO_POSITION &&
                manager.findFirstCompletelyVisibleItemPosition() != 0
            val canGoTowardEnd = itemCount > 0 &&
                manager.findLastVisibleItemPosition() != RecyclerView.NO_POSITION &&
                manager.findLastCompletelyVisibleItemPosition() != itemCount - 1
            // RTL list: index 0 is on the right, so left/right arrows map opposite to LTR.
            if (recyclerView.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                mBinding.leftScrollArrow.isVisible = canGoTowardEnd
                mBinding.rightScrollArrow.isVisible = canGoTowardStart
            } else {
                mBinding.leftScrollArrow.isVisible = canGoTowardStart
                mBinding.rightScrollArrow.isVisible = canGoTowardEnd
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = CityFragmentLayoutBinding.bind(view)
        // Layout managers belong to an individual RecyclerView and its view lifecycle.
        mBinding.mainCitiesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mBinding.dailyWeatherRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mBinding.dailyWeatherRecyclerView.addOnScrollListener(scrollListener)
        mBinding.hourlyList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mBinding.refreshForecast.setOnRefreshListener { getWeatherByLocation(lat, long, degreesUnits) }
        mBinding.retryForecast.setOnClickListener { getWeatherByLocation(lat, long, degreesUnits) }
        mBinding.openDayPlan.setOnClickListener { startActivity(Intent(requireContext(), DayPlanActivity::class.java)) }
        mBinding.explorePlaces.setOnClickListener {
            (requireActivity() as MainActivity).changeNavBarIndex(R.id.chooseAttractionFragment, R.id.attractions)
        }
        mBinding.favoriteImg.isEnabled = false
        mBinding.alertSignImg.isVisible = false
        mNavController = Navigation.findNavController(view)
        onClickListener()
        setColorSpan(
            0,
            1,
            R.color.turquoise,
            R.string.units,
            mBinding.units,
        )
        if (arguments?.getBoolean(FROM_TOP_ADAPTER) == true) {
            mCityName = arguments?.getString(CITY_NAME).toString()
           /* getWeather(mCityName, degreesUnits)
            getDailyWeather(mCityName, mCountryCode, degreesUnits)*/
        }

        if (arguments?.getBoolean(FROM_FAVORITES) == true ||
            arguments?.getBoolean(FROM_GOOGLE_MAPS) == true
        ) {
            mFromFavorites = true
//            (activity as MainActivity).setItemSelected(R.id.cityFragment, R.id.weather, false)
            (activity as MainActivity).changeNavBarIndex(R.id.cityFragment, R.id.weather)
            lat = arguments?.getFloat(LAT).toString()
            long = arguments?.getFloat(LONG).toString()
            mBinding.cityName.text = arguments?.getString("cityName") ?: ""
            mFavWeather?.cityName = arguments?.getString("cityName") ?: ""
            weather?.cityName = arguments?.getString("cityName") ?: ""
            cityName = arguments?.getString("cityName") ?: ""
            mCitiesViewModel.setCityName(arguments?.getString("cityName") ?: "")
        }

        if (arguments?.getBoolean(FROM_ALERTS) == true) {
            lat = arguments?.getFloat(LAT).toString()
            long = arguments?.getFloat(LONG).toString()
            getWeatherByLocation(lat, long, degreesUnits)
        }

        mBinding.leftScrollArrow.visibility = View.VISIBLE
        mBinding.rightScrollArrow.visibility = View.GONE
        liveDataObservers()
        loadInterstitialAd()
    }

    private fun getWeatherByLocation(lat: String, long: String, degreesUnits: String) {
        if (lat.toDoubleOrNull() == null || long.toDoubleOrNull() == null) {
            mBinding.refreshForecast.isRefreshing = false
            mBinding.forecastStatus.setText(R.string.dashboard_no_data)
            return
        }
        mBinding.refreshForecast.isRefreshing = true
        mBinding.forecastStatus.setText(R.string.dashboard_loading)
        mWeatherViewModel.getWeatherByLocation(lat, long, degreesUnits)
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
                    getWeatherByLocation(lat, long, degreesUnits)
                }
            }
        }
    }

    private fun liveDataObservers() {
        mWeatherViewModel.weatherData.observe(viewLifecycleOwner) {
            mBinding.refreshForecast.isRefreshing = false
            if (it.first != null) {
                errorSnackbar?.dismiss()
                errorSnackbar = null
                mBinding.retryForecast.isVisible = false
                weather = it.first
                mFavWeather = it.first
                weather?.cityName = cityName
                mFavWeather?.cityName = cityName
//                mWeatherViewModel.getCityNameByLocation(lat, long)
                mFavWeather?.callTime = System.currentTimeMillis()
                weather?.callTime = System.currentTimeMillis()
                setWeatherData(mFavWeather)
                mFavWeather?.let { forecast -> com.evya.myweatherapp.ui.WeatherWidget.publish(requireContext(), forecast) }
                weather?.let { it1 -> mCitiesViewModel.addCityDataToDB(it1) }
                weather?.cityName?.let { cityName ->
                    checkIfAlreadyInFav(cityName)
                }
            } else {
//                getCityByLocation(lat, long, degreesUnits)
                it.second?.let { it1 ->
                    mBinding.retryForecast.isVisible = true
                    mBinding.forecastStatus.setText(if (mFavWeather == null) R.string.dashboard_no_data else R.string.dashboard_cached)
                    errorSnackbar?.dismiss()
                    errorSnackbar = Snackbar.make(
                        mBinding.root, getString(it1, mCityName),
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction(R.string.weather_retry) {
                        getWeatherByLocation(lat, long, degreesUnits)
                    }.also { bar -> bar.show() }
                }
            }
        }

        mWeatherViewModel.dailyWeatherData.observe(viewLifecycleOwner) { response ->
            if (response.first == null) {
                response.second?.let { showToast(getString(it)) }
                return@observe
            }
            mFavWeather?.dailyWeather = response.first
            weather?.dailyWeather = response.first
            handleInterstitialAd(response.first)
        }

        mWeatherViewModel.cityNameData.observe(viewLifecycleOwner) {
            it.first?.let { cityData ->
                if (cityData.size > 0) {
                    mBinding.cityName.text = cityData[0].localNames.en
                    mFavWeather?.cityName = cityData[0].localNames.en
                    weather?.cityName = cityData[0].localNames.en
                    viewLifecycleOwner.lifecycleScope.launch {
                        if (mCitiesViewModel.fetchSpecificCity(cityData[0].localNames.en) == null) {
                            weather?.let { mCitiesViewModel.addCityDataToDB(it) }
                        }
                    }
//                    checkIfAlreadyInDB(cityData[0].localNames.en)
//                    mFavoritesViewModel.setCityName(cityData[0].localNames.en)
                } else {
                    showToast(context?.getString(R.string.didnt_choose_city_error))
                    (activity as MainActivity).getLastLocation()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            val tempWeather = mCitiesViewModel.fetchSpecificCity(weather?.cityName ?: arguments?.getString("cityName") ?:  "")
            if (tempWeather == null) {
                getWeatherByLocation(lat, long, degreesUnits)
            } else {
                mFavWeather = tempWeather
                weather = tempWeather
                // every 2 hour (7200000 milisec) make a new call
                if ((System.currentTimeMillis() - tempWeather.callTime) > FIVE_HOURS) {
                    // Keep the last successful forecast visible if refreshing fails.
                    setWeatherData(tempWeather)
                    getWeatherByLocation(lat, long, degreesUnits)
//                    mFavoritesViewModel.addCityDataToDB(tempWeather)
                } else {
                    checkIfAlreadyInFav(tempWeather.cityName)
                    setWeatherData(tempWeather)
                }
            }
        }
        /*.observe(requireActivity()) { weather ->

        if (weather == null) {
                mWeatherViewModel.getWeatherByLocation(lat, long, degreesUnits)
//                            checkIfAlreadyInFav(cityData[0].name)
            } else {
                // every 2 hour (7200000 milisec) make a call
                if ((System.currentTimeMillis() - weather.callTime) > 7200000) {
//                                checkIfAlreadyInFav(cityData[0].name)
                    mWeatherViewModel.getWeatherByLocation(lat, long, degreesUnits)
                } else {
                    setWeatherData(weather)
                }
            }
        }*/
    }

    private fun setWeatherData(weather: Weather?) {
        mBinding.alertSignImg.isVisible = !mFavWeather?.alerts.isNullOrEmpty()

//        mBinding.cityName.text = mFavWeather?.timezone?.substringAfter("/")
        mBinding.dailyExpectation.text = mFavWeather?.daily?.firstOrNull()?.summary ?: ""
        lat = mFavWeather?.lat.toString()
        long = mFavWeather?.lon.toString()
//                mWeatherViewModel.getCitiesAround(lat, long, degreesUnits)
        weather?.let { syncUnitsToggle(it.responseUnits) }
        weather?.let { it1 -> showWeather(it1) }
        setDailyAdapter(weather?.daily.orEmpty())
        weather?.let { it1 -> setWeatherDataInTextViews(it1) }
    }

    /** Keep labels, toggle, and MainData aligned with the units of the values on screen. */
    private fun syncUnitsToggle(units: String) {
        val normalized = if (units == IMPERIAL) IMPERIAL else METRIC
        degreesUnits = normalized
        mCelsius = normalized == METRIC
        val start = if (mCelsius) 0 else mBinding.units.text.length - 1
        val end = if (mCelsius) 1 else mBinding.units.text.length
        setColorSpan(start, end, R.color.turquoise, R.string.units, mBinding.units)
    }

    private fun isRaining(description: String) =
        description == RAIN || description == LIGHT_RAIN || description == SNOW || description == LIGHT_SNOW

    private fun isWinter(temp: Double, units: String = degreesUnits) = if (units == METRIC) {
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
        mBinding.mainCitiesRecyclerView.adapter = mMainCitiesAdapter
    }

    private fun setDailyAdapter(dailyWeatherList: List<Daily>) {
        mDailyAdapter = DailyWeatherAdapter(dailyWeatherList, mFavWeather?.timezone ?: "UTC") {
            getSpecificDayWeather?.invoke(it)
        }
        mBinding.dailyWeatherRecyclerView.adapter = mDailyAdapter

        getSpecificDayWeather = { time ->
            val date = forecastTime(time.toLong(), mFavWeather?.timezone ?: "UTC", "yyyy-MM-dd")
            if (weather?.dailyWeather?.date != date) {
                mWeatherViewModel.getWeatherForSpecificDay(lat, long, date, degreesUnits)
            } else {
                handleInterstitialAd(weather?.dailyWeather)
            }
        }
    }

    private fun setWeatherDataInTextViews(weather: Weather) {
        mBinding.mainImage.setImageResource(weatherIcon(weather.current.weather.firstOrNull()?.id))
        mBinding.weather = weather
        mCityName = weather.cityName.ifBlank { weather.timezone.substringAfterLast("/").replace('_', ' ') }
        mCountryCode = weather.timezone.substringBefore("/")
        with(mBinding) {
            cityName.text = mCityName
            temp.text = weather.current.temp.roundToInt().toString()
            feelsLike.text = getString(R.string.dashboard_feels_like, weather.getDegreeUnits(weather.current.feelsLike))
            favoriteImg.isEnabled = true
            forecastStatus.text = getString(R.string.dashboard_updated, relativeTimeEnglish(weather.callTime))
            val current = weather.current
            sunrise.text = dashboardMetric(requireContext(),
                getString(R.string.metric_sunrise, forecastTime(current.sunrise.toLong(), weather.timezone)))
            sunset.text = dashboardMetric(requireContext(),
                getString(R.string.metric_sunset, forecastTime(current.sunset.toLong(), weather.timezone)))
            humidity.text = dashboardMetric(requireContext(),
                getString(R.string.metric_humidity, current.humidity))
            windSpeed.text = dashboardMetric(requireContext(),
                getString(R.string.metric_wind, "${current.windSpeed} ${weather.getWindSpeedDegree()}"))
            description.text = dashboardMetric(requireContext(),
                getString(R.string.metric_conditions, current.weather.firstOrNull()?.description ?: "—"))
            visibility.text = dashboardMetric(requireContext(),
                getString(R.string.metric_visibility, weather.getVisibilityUnits(current.visibility)))
            probabilityOfPrecipitation.text = weather.daily?.firstOrNull()?.let {
                dashboardMetric(requireContext(),
                    getString(R.string.metric_rain_chance, (it.pop.coerceIn(0.0, 1.0) * 100).roundToInt()))
            } ?: dashboardMetric(requireContext(), getString(R.string.metric_rain_chance, 0))
            rain3h.text = dashboardMetric(requireContext(),
                getString(R.string.metric_rain, weather.precipitationAmount()))
            airPollution.text = dashboardMetric(requireContext(),
                getString(R.string.metric_uv, current.uvi.toString()))
            val now = System.currentTimeMillis() / 1000
            val hours = weather.hourly.orEmpty().filter { it.dt.toLong() >= now - 3600 }
                .sortedBy { it.dt }.take(24)
            hourlyList.adapter = HourlyWeatherAdapter(hours, weather.timezone)
            hourlyEmpty.isVisible = hours.isEmpty()
            val window = bestOutdoorWindow(hours, weather.daily.orEmpty().map {
                it.sunrise.toLong()..it.sunset.toLong()
            }, weather.isImperial(), now)
            bestTimeTitle.setText(R.string.dashboard_outdoors)
            when {
                !weather.alerts.isNullOrEmpty() -> bestTimeReason.setText(R.string.best_window_alert)
                hours.isEmpty() -> bestTimeReason.setText(R.string.best_window_missing)
                window == null -> bestTimeReason.setText(R.string.best_window_empty)
                else -> {
                    bestTimeTitle.text = getString(R.string.best_window,
                        forecastTime(window.first.dt.toLong(), weather.timezone, "EEE HH:mm"),
                        forecastTime(window.second.dt.toLong() + 3600, weather.timezone))
                    bestTimeReason.text = getString(R.string.best_window_reason,
                        weather.getDegreeUnits((window.first.temp + window.second.temp) / 2),
                        (maxOf(window.first.pop, window.second.pop) * 100).roundToInt(),
                        "${maxOf(window.first.windSpeed, window.second.windSpeed)} ${weather.getWindSpeedDegree()}")
                }
            }
        }
    }

    private fun scrollForecast(offset: Int) {
        val recyclerView = mBinding.dailyWeatherRecyclerView
        val manager = recyclerView.layoutManager as? LinearLayoutManager ?: return
        val position = if (offset < 0) manager.findFirstVisibleItemPosition()
            else manager.findLastVisibleItemPosition()
        val target = forecastScrollTarget(position, recyclerView.adapter?.itemCount ?: 0, offset)
            ?: return
        recyclerView.smoothScrollToPosition(target)
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
                )
                units.text
                /*            getWeather(mCityName, degreesUnits)
            getDailyWeather(mCityName, mCountryCode, degreesUnits)*/
                getWeatherByLocation(lat, long, degreesUnits)
            }

            locationIcon.setOnClickListener {
                val bundle =
                    bundleOf(
                        LAT to lat.toFloat(),
                        LONG to long.toFloat(),
                        CITY_NAME to mCityName
                    )
                mNavController.navigate(R.id.action_cityFragment_to_googleMapsFragment, bundle)
                (activity as MainActivity).changeNavBarIndex(R.id.googleMapsFragment, R.id.map)
            }

            favoriteImg.setOnClickListener {
                if (!addedToFav) {
                    favoriteImg.setImageResource(R.drawable.ic_red_heart)
                    addedToFav = true
                    mFavWeather?.cityName = mBinding.cityName.text.toString()
                    weather?.cityName = mBinding.cityName.text.toString()
                    mFavWeather?.isInFavorites = true
                    weather?.isInFavorites = true
                    mFavWeather?.let { it1 -> mCitiesViewModel.updateFavorites(true, mBinding.cityName.text.toString()) }
                } else {
                    favoriteImg.setImageResource(R.drawable.ic_empty_heart)
                    addedToFav = false
                    mFavWeather?.isInFavorites = false
                    weather?.isInFavorites = false
                    mFavWeather?.let { it1 -> mCitiesViewModel.updateFavorites(false, mBinding.cityName.text.toString()) }
                }
            }
            leftScrollArrow.setOnClickListener {
                // RTL: left side scrolls toward later days (higher indices).
                scrollForecast(if (dailyWeatherRecyclerView.layoutDirection == View.LAYOUT_DIRECTION_RTL) 3 else -3)
            }

            rightScrollArrow.setOnClickListener {
                scrollForecast(if (dailyWeatherRecyclerView.layoutDirection == View.LAYOUT_DIRECTION_RTL) -3 else 3)
            }

            alertSignImg.setOnClickListener {
                mFavWeather?.alerts?.let { alertsList ->
                    val bundle = bundleOf(
                        CITY_NAME to mCityName,
                        ALERTS to alertsList
                    )
                    mNavController.navigate(R.id.action_cityFragment_to_alertsFragment, bundle)

                } ?: showToast("No alerts in this area")
            }
        }
    }

    private fun checkIfAlreadyInFav(cityName: String) {
        try {
            mCitiesViewModel.setCityName(cityName)
            mCitiesViewModel.checkIfAlreadyInFav.observe(viewLifecycleOwner) {
                addedToFav = it
                if (it) {
                    mBinding.favoriteImg.setImageResource(R.drawable.ic_red_heart)
                } else {
                    mBinding.favoriteImg.setImageResource(R.drawable.ic_empty_heart)
                }
            }
        } catch (_: Exception) {

        }
    }

/*    private fun checkIfAlreadyInDB(cityName: String) {
        try {
            mFavoritesViewModel.setCityName(cityName)
            mFavoritesViewModel.checkIfAlreadyAddedToDB.observe(viewLifecycleOwner) {
                if (it == false) {
                    weather?.let {mFavoritesViewModel.addCityDataToDB(it)}
                }
            }
        } catch (_: Exception) {

        }
    }*/



    override fun onDestroyView() {
        errorSnackbar?.dismiss()
        errorSnackbar = null
        mBinding.refreshForecast.isRefreshing = false
        mBinding.hourlyList.adapter = null
        mBinding.hourlyList.layoutManager = null
        mBinding.dailyWeatherRecyclerView.removeOnScrollListener(scrollListener)
        mBinding.dailyWeatherRecyclerView.adapter = null
        mBinding.dailyWeatherRecyclerView.layoutManager = null
        mBinding.mainCitiesRecyclerView.adapter = null
        mBinding.mainCitiesRecyclerView.layoutManager = null
        getSpecificDayWeather = null
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        if (approvedPermissions && !mFromFavorites) {
            approvedPermissions = false
//            getWeatherData()
        }
    }

    private fun loadInterstitialAd() {
        val adRequest = (activity as MainActivity).adRequest

        context?.let {
            InterstitialAd.load(it, "ca-app-pub-9058418744370338/1048685069", adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    val params = bundleOf(
                        PARAMS_FAILED_TO_LOAD_INTERSTITIAL_AD.paramsName to adError.message
                    )
                    FireBaseEvents.sendFireBaseCustomEvents(
                        ON_INTERSTITIAL_AD_FAILED_TO_LOAD.eventName,
                        params
                    )
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    val params = bundleOf()
                    FireBaseEvents.sendFireBaseCustomEvents(
                        ON_INTERSTITIAL_AD_LOADED.eventName,
                        params
                    )
                    mInterstitialAd = interstitialAd
                }
            })
        }
    }

    private fun handleInterstitialAd(dailyWeather: DailyWeather?) {
        if (mInterstitialAd != null && showAd >= 3) {
            showAd = 0
            activity?.let { mInterstitialAd?.show(it) }
        } else {
            showAd++
            showDialog(dailyWeather)
        }
        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                val params = bundleOf()
                FireBaseEvents.sendFireBaseCustomEvents(CLICK_ON_INTERSTITIAL_AD.eventName, params)
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                val params = bundleOf()
                FireBaseEvents.sendFireBaseCustomEvents(
                    ON_INTERSTITIAL_AD_DISMISSED_FULL_SCREEN_CONTENT.eventName,
                    params
                )
                mInterstitialAd = null
                showDialog(dailyWeather)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when ad fails to show.
                val params = bundleOf(
                    PARAMS_FAILED_TO_LOAD_INTERSTITIAL_AD.paramsName to adError.message
                )
                FireBaseEvents.sendFireBaseCustomEvents(
                    ON_INTERSTITIAL_AD_FAILED_TO_SHOW_FULL_SCREEN_CONTENT.eventName,
                    params
                )
                mInterstitialAd = null
                showDialog(dailyWeather)
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                val params = bundleOf()
                FireBaseEvents.sendFireBaseCustomEvents(
                    ON_INTERSTITIAL_AD_IMPRESSION.eventName,
                    params
                )
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                val params = bundleOf()
                FireBaseEvents.sendFireBaseCustomEvents(
                    ON_INTERSTITIAL_AD_SHOWED_FULL_SCREEN_CONTENT.eventName,
                    params
                )
            }
        }
    }

    private fun showDialog(dailyWeather: DailyWeather?) {
        activity?.supportFragmentManager?.let { fm ->
            DailyDialog.newInstance(
                dailyWeather,
                mBinding.cityName.text.toString()
            ).show(fm, "DAILY_WEATHER_DIALOG")
        }
    }

}
