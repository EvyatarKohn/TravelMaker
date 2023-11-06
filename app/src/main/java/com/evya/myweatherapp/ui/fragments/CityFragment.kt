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
import com.evya.myweatherapp.ui.dialogs.DailyDialog
import com.evya.myweatherapp.util.UtilsFunctions.Companion.setColorSpan
import com.evya.myweatherapp.util.UtilsFunctions.Companion.setSpanBold
import com.evya.myweatherapp.util.UtilsFunctions.Companion.showToast
import com.evya.myweatherapp.viewmodels.FavoritesViewModel
import com.evya.myweatherapp.viewmodels.NewWeatherViewModel
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


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
    var getSpecificDayWeather: ((time: Int) -> Unit)? = null
    private var mInterstitialAd: InterstitialAd? = null
    private var showAd = 0
    private val linearLayoutManager =  LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.HORIZONTAL, false)
    private val scrollListener = object : OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            mBinding.apply {
                linearLayoutManager.let {
                    if (it.findFirstCompletelyVisibleItemPosition() == 0) {
                        leftScrollArrow.visibility = View.GONE
                    } else {
                        leftScrollArrow.visibility = View.VISIBLE
                    }

                    if (it.findLastCompletelyVisibleItemPosition() == (mDailyAdapter.itemCount - 1)) {
                        rightScrollArrow.visibility = View.GONE
                    } else {
                        rightScrollArrow.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = CityFragmentLayoutBinding.bind(view)
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
            mFavoritesViewModel.setCityName(arguments?.getString("cityName") ?: "")
        }

        if (arguments?.getBoolean(FROM_ALERTS) == true) {
            lat = arguments?.getFloat(LAT).toString()
            long = arguments?.getFloat(LONG).toString()
            getWeatherByLocation(lat, long, degreesUnits)
        }

        mBinding.rightScrollArrow.visibility = View.VISIBLE
        mBinding.leftScrollArrow.visibility = View.GONE
        liveDataObservers()
        loadInterstitialAd()
    }

    private fun getWeatherByLocation(lat: String, long: String, degreesUnits: String) {
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
            if (it.first != null) {
                weather = it.first
                mFavWeather = it.first
                mWeatherViewModel.getCityNameByLocation(lat, long)
                mFavWeather?.callTime = System.currentTimeMillis()
                weather?.callTime = System.currentTimeMillis()
                setWeatherData(mFavWeather)
                weather?.cityName?.let { cityName ->
                    checkIfAlreadyInFav(cityName)
                }
            } else {
//                getCityByLocation(lat, long, degreesUnits)
                it.second?.let { it1 ->
                    showToast(context?.getString(it1, mCityName))
                }
            }
        }

        mWeatherViewModel.dailyWeatherData.observe(viewLifecycleOwner) { response ->
           handleInterstitialAd(response.first)
        }

        mWeatherViewModel.cityNameData.observe(viewLifecycleOwner) {
            it.first?.let { cityData ->
                if (cityData.size > 0) {
                    mBinding.cityName.text = cityData[0].localNames.en
                    mFavWeather?.cityName = cityData[0].localNames.en
                    weather?.cityName = cityData[0].localNames.en
                    CoroutineScope(Dispatchers.IO).launch {
                        if (mFavoritesViewModel.fetchSpecificCity(cityData[0].localNames.en) == null) {
                            weather?.let { mFavoritesViewModel.addCityDataToDB(it) }
                        }
                    }
//                    checkIfAlreadyInDB(cityData[0].localNames.en)
                    setBoldSpan()
//                    mFavoritesViewModel.setCityName(cityData[0].localNames.en)
                } else {
                    showToast(context?.getString(R.string.didnt_choose_city_error))
                    (activity as MainActivity).getLastLocation()
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            val tempWeather = mFavoritesViewModel.fetchSpecificCity(arguments?.getString("cityName") ?: weather?.cityName ?: "")
            if (tempWeather == null) {
                getWeatherByLocation(lat, long, degreesUnits)
            } else {
                mFavWeather = tempWeather
                weather = tempWeather
                // every 2 hour (7200000 milisec) make a call
                if ((System.currentTimeMillis() - tempWeather.callTime) > 7200000) {
                    mFavoritesViewModel.removeCityDataFromDB(tempWeather.cityName)
                    getWeatherByLocation(lat, long, degreesUnits)
//                    mFavoritesViewModel.addCityDataToDB(tempWeather)
                } else {
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
        mBinding.dailyExpectation.text = mFavWeather?.daily?.get(0)?.summary ?: ""
        lat = mFavWeather?.lat.toString()
        long = mFavWeather?.lon.toString()
//                mWeatherViewModel.getCitiesAround(lat, long, degreesUnits)
        weather?.let { it1 -> showWeather(it1) }
        weather?.daily?.let { it1 -> setDailyAdapter(it1) }
        weather?.let { it1 -> setWeatherDataInTextViews(it1) }
    }

    private fun setBoldSpan() {
        val humidity = mBinding.humidity
        val windSpeed = mBinding.windSpeed
        val sunrise = mBinding.sunrise
        val sunset = mBinding.sunset
        val description = mBinding.description
        val visibility = mBinding.visibility
        val probabilityOfPrecipitation = mBinding.probabilityOfPrecipitation
        val rain3h = mBinding.rain3h
        val airPollution = mBinding.airPollution
        setSpanBold(0, humidity, humidity.text.toString().substringBefore(":"))
        setSpanBold(0, windSpeed, windSpeed.text.toString().substringBefore(":"))
        setSpanBold(0, sunrise, sunrise.text.toString().substringBefore(":"))
        setSpanBold(0, sunset, sunset.text.toString().substringBefore(":"))
        setSpanBold(0, description, description.text.toString().substringBefore(":"))
        setSpanBold(0, visibility, visibility.text.toString().substringBefore(":"))
        setSpanBold(0, probabilityOfPrecipitation, probabilityOfPrecipitation.text.toString().substringBefore(":"))
        setSpanBold(0, rain3h, rain3h.text.toString().substringBefore(":"))
        setSpanBold(0, airPollution, airPollution.text.toString().substringBefore(":"))
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
        mBinding.mainCitiesRecyclerView.layoutManager = linearLayoutManager
        mBinding.mainCitiesRecyclerView.adapter = mMainCitiesAdapter
    }

    private fun setDailyAdapter(dailyWeatherList: List<Daily>) {
        val minTempRawArray: ArrayList<Int> = ArrayList()
        dailyWeatherList.forEach { dailyWeatherData ->
            minTempRawArray.add(dailyWeatherData.temp.min.toInt())
        }
//        val minTempArray = minTempRawArray.sorted().take(5)
        val minTempArray = minTempRawArray.sorted()

        val maxTempRawArray: ArrayList<Int> = ArrayList()
        dailyWeatherList.forEach { dailyWeatherData ->
            maxTempRawArray.add(dailyWeatherData.temp.max.toInt())
        }
//        val maxTempArray = maxTempRawArray.sortedDescending().take(5)
        val maxTempArray = maxTempRawArray.sortedDescending()

/*//        val newList = dailyWeatherList.filterIndexed { index, _ -> index % 8 == 0 }
        val newList = dailyWeatherList.subList(0, 5)*/

        mDailyAdapter =
            DailyWeatherAdapter(this, dailyWeatherList, minTempArray, maxTempArray, activity?.applicationContext)
        mBinding.dailyWeatherRecyclerView.layoutManager = linearLayoutManager
        mBinding.dailyWeatherRecyclerView.adapter = mDailyAdapter
        mBinding.dailyWeatherRecyclerView.addOnScrollListener(scrollListener)

        getSpecificDayWeather = { time ->
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(time * 1000L)
            mWeatherViewModel.getWeatherForSpecificDay(lat, long, date, degreesUnits)
        }
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
                    favoriteImg.setBackgroundResource(R.drawable.ic_red_heart)
                    addedToFav = true
                    mFavWeather?.cityName = mBinding.cityName.text.toString()
                    weather?.cityName = mBinding.cityName.text.toString()
                    mFavWeather?.isInFavorites = true
                    weather?.isInFavorites = true
                    mFavWeather?.let { it1 -> mFavoritesViewModel.updateFavorites(true, mBinding.cityName.text.toString()) }
                } else {
                    favoriteImg.setBackgroundResource(R.drawable.ic_empty_heart)
                    addedToFav = false
                    mFavWeather?.isInFavorites = false
                    weather?.isInFavorites = false
                    mFavWeather?.let { it1 -> mFavoritesViewModel.updateFavorites(false, mBinding.cityName.text.toString()) }
                }
            }
            leftScrollArrow.setOnClickListener {
                mBinding.rightScrollArrow.visibility = View.VISIBLE
                if ((dailyWeatherRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() > 0) {
                    mBinding.dailyWeatherRecyclerView.smoothScrollToPosition((dailyWeatherRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() - 3)
                } else {
                    mBinding.dailyWeatherRecyclerView.smoothScrollToPosition(0)
                }

                if((dailyWeatherRecyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition() == 0) {
                    mBinding.leftScrollArrow.visibility = View.GONE
                }
            }

            rightScrollArrow.setOnClickListener{
                val layoutManager = (dailyWeatherRecyclerView.layoutManager as LinearLayoutManager)
                mBinding.leftScrollArrow.visibility = View.VISIBLE
                dailyWeatherRecyclerView.smoothScrollToPosition(
                    (dailyWeatherRecyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() + 3
                )
                if(layoutManager.findLastCompletelyVisibleItemPosition() == (dailyWeatherRecyclerView.adapter?.itemCount?.minus(1) ?: false)) {
                    mBinding.rightScrollArrow.visibility = View.GONE
                }
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
            mFavoritesViewModel.setCityName(cityName)
            mFavoritesViewModel.checkIfAlreadyInFav.observe(viewLifecycleOwner) {
                if (it) {
                    mBinding.favoriteImg.setBackgroundResource(R.drawable.ic_red_heart)
                } else {
                    mBinding.favoriteImg.setBackgroundResource(R.drawable.ic_empty_heart)
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