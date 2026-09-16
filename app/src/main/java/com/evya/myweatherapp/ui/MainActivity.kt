package com.evya.myweatherapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.evya.myweatherapp.Constants.PERMISSIONS_REQUEST_ID
import com.evya.myweatherapp.Constants.REQUEST_CODE_LOCATION_SETTING
import com.evya.myweatherapp.Constants.THREE_SEC
import com.evya.myweatherapp.MainData
import com.evya.myweatherapp.MainData.approvedPermissions
import com.evya.myweatherapp.MainData.cityName
import com.evya.myweatherapp.MainData.lat
import com.evya.myweatherapp.MainData.long
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.ActivityMainBinding
import com.evya.myweatherapp.firebaseanalytics.FireBaseEvents
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsNamesStrings.*
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsParamsStrings.*
import com.evya.myweatherapp.ui.dialogs.InfoDialog
import com.evya.myweatherapp.ui.dialogs.PermissionDeniedDialog
import com.evya.myweatherapp.util.UtilsFunctions
import com.evya.myweatherapp.util.UtilsFunctions.Companion.setContext
import com.evya.myweatherapp.viewmodels.CitiesViewModel
import com.evya.myweatherapp.viewmodels.NewWeatherViewModel
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mWeatherViewModel: NewWeatherViewModel by viewModels()
    private val mCitiesViewModel: CitiesViewModel by viewModels()
    private var showAd: Int = 0
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mLocationRequest: LocationRequest
    private var mGpsIsOn = false
    private var mThreeSec = false
    private var mFlowStarted = false
    private var mUpdatingNavigation = false
    private lateinit var mNavHostFragment: NavHostFragment
    private lateinit var mBinding: ActivityMainBinding
    private var mFirsTimeBack = true
    val adRequest = AdRequest.Builder().build()
    private var mInterstitialAd: InterstitialAd? = null

    companion object {
        private val TAG = MainActivity::class.toString()

        private val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, mBinding.root).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
        ViewCompat.setOnApplyWindowInsetsListener(mBinding.root) { view, insets ->
            val safeArea = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            val keyboard = insets.getInsets(WindowInsetsCompat.Type.ime())
            view.setPadding(safeArea.left, safeArea.top, safeArea.right, maxOf(safeArea.bottom, keyboard.bottom))
            WindowInsetsCompat.CONSUMED
        }
        ViewCompat.requestApplyInsets(mBinding.root)
        setContext(this)
        MobileAds.initialize(this) {}
        loadInterstitialAd()
        initObservers()
       /* val testDeviceIds = Arrays.asList("ca-app-pub-3940256099942544/6300978111\n")
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)*/

        handleOnBackPressed()

        mGpsIsOn = isLocationEnabled()
        UtilsFunctions.setColorSpan(
            6,
            11,
            R.color.black,
            R.string.app_name_title,
            mBinding.appName,
        )

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mNavHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)

        lifecycleScope.launch {
            delay(THREE_SEC)
            mThreeSec = true
            getLastLocation()
        }

        mBinding.bottomNavigationBar.setOnItemSelectedListener { id ->
            if (!mUpdatingNavigation) navigateToRelevantScreen(id)
        }
    }

    private fun navigateToRelevantScreen(id: Int, shouldCallApiAgain: Boolean = true) {
        var firebaseEvent = NAVIGATE_TO_WEATHER
        var navigateTo = "weather"

        when (id) {
            R.id.weather -> {
                approvedPermissions = true
                changeNavBarIndex(R.id.cityFragment, R.id.weather, shouldCallApiAgain)
                firebaseEvent = NAVIGATE_TO_WEATHER
                navigateTo = "weather"
            }
            R.id.map -> {
                changeNavBarIndex(R.id.googleMapsFragment, R.id.map, shouldCallApiAgain)
                firebaseEvent = NAVIGATE_TO_GOOGLE_MAP
                navigateTo = "map"
            }
            R.id.attractions -> {
                changeNavBarIndex(R.id.chooseAttractionFragment, R.id.attractions, shouldCallApiAgain)
                firebaseEvent = NAVIGATE_TO_ATTRACTIONS
                navigateTo = "attractions"
            }

            R.id.favorites -> {
                changeNavBarIndex(R.id.favoritesFragment, R.id.favorites, shouldCallApiAgain)
                firebaseEvent = NAVIGATE_TO_FAVORITES
                navigateTo = "favorites"
            }

            R.id.info -> {
                InfoDialog.newInstance().show(supportFragmentManager, "INFO_DIALOG")
                firebaseEvent = NAVIGATE_TO_SHOW_INFO
                navigateTo = "info"
            }
            else -> {
            }
        }
        val params = bundleOf(
            PARAMS_NAVIGATE_TO.paramsName to navigateTo
        )
        FireBaseEvents.sendFireBaseCustomEvents(firebaseEvent.eventName, params)
    }


    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    val location = task.result
                    if (location == null) {
                        getNewLocation()
                    } else {
                        approvedPermissions = true
                        lat = location.latitude.toString()
                        long = location.longitude.toString()
                        lifecycleScope.launch(Dispatchers.IO) {
                            val resolvedCity = runCatching {
                                Geocoder(applicationContext, Locale.ENGLISH)
                                    .getFromLocation(location.latitude, location.longitude, 1)
                                    ?.firstOrNull()
                                    ?.locality
                            }.getOrNull()
                            if (!resolvedCity.isNullOrBlank()) cityName = resolvedCity
                            val weather = cityName.takeIf { it.isNotBlank() }
                                ?.let { mCitiesViewModel.fetchSpecificCity(it) }
                            withContext(Dispatchers.Main) {
                                if (weather != null) {
                                    MainData.weather = weather
                                }
                                startFlow()
                            }
                        }
                    }
                }
            } else {
                if (!mGpsIsOn) {
                    PermissionDeniedDialog.newInstance(false)
                        .show(supportFragmentManager, "PERMISSION_DENIED_DIALOG")
                } else {
                    getLastLocation()
                }
            }
        } else {
            requestPermissions()
        }
    }

    private fun initObservers() {
        mWeatherViewModel.cityNameData.observe(this) {
            it.first?.let { cityData ->
                if (cityData.size > 0) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val weather = mCitiesViewModel.fetchSpecificCity(cityData[0].localNames.en)
                        withContext(Dispatchers.Main) {
                            if (weather != null) {
                                MainData.weather = weather
                            }
                            startFlow()
                        }
                    }
                }
            }
        }
    }

    private fun startFlow() {
        if (mGpsIsOn && mThreeSec && !mFlowStarted) {
            // Location and city lookups can complete more than once. Never reset
            // the user's selected tab after the initial screen has been shown.
            mFlowStarted = true
            selectNavigationItem(R.id.weather)
            mBinding.bottomNavigationBar.visibility = View.VISIBLE
            mBinding.navHostFragment.visibility = View.VISIBLE
            startDestination(R.id.cityFragment)
            handleBannerAd()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getNewLocation() {
        mLocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0).apply {
            setMaxUpdates(1)

        }.build()
        mFusedLocationProviderClient.requestLocationUpdates(
            mLocationRequest,
            locationCallback,
            Looper.myLooper()!!
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation = locationResult.lastLocation ?: return
            mFusedLocationProviderClient.removeLocationUpdates(this)
            lat = lastLocation.latitude.toString()
            long = lastLocation.longitude.toString()
            getLastLocation()
        }
    }

    override fun onDestroy() {
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onDestroy()
    }

    private fun checkPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST_ID)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService((Context.LOCATION_SERVICE)) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != PERMISSIONS_REQUEST_ID) return
        if (checkPermissions()) {
            getLastLocation()
        } else {
            PermissionDeniedDialog.newInstance(true)
                .show(supportFragmentManager, "PERMISSION_DENIED_DIALOG")
        }
    }

    fun goToPermissionSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        approvedPermissions = true
    }

    fun turnGPSOn() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 30000).apply {
            setMinUpdateIntervalMillis(5000)

        }.build()


        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val result =
            LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                task.getResult(ApiException::class.java)
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        approvedPermissions = true
                        mGpsIsOn = true
                        val resolvable = exception as ResolvableApiException
                        resolvable.startResolutionForResult(
                            this@MainActivity,
                            REQUEST_CODE_LOCATION_SETTING
                        )
                        resolvable.status
                    } catch (e: SendIntentException) {
                        e.message?.let { Log.d(TAG, it) }
                    } catch (e: ClassCastException) {
                        e.message?.let { Log.d(TAG, it) }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_LOCATION_SETTING -> getLastLocation()
        }
    }

    override fun onResume() {
        super.onResume()
        if (approvedPermissions && !mGpsIsOn) {
            getLastLocation()
        }
    }


    fun changeNavBarIndex(destination: Int, bottomNavId: Int, shouldCallApiAgain: Boolean = true) {
        mFirsTimeBack = true
        selectNavigationItem(bottomNavId)
        if (mNavHostFragment.navController.currentDestination?.id == destination) return
        if (shouldCallApiAgain) {
            handleInterstitialAd(destination)
        } else {
            startDestination(destination)
        }
    }

    private fun selectNavigationItem(bottomNavId: Int) {
        mUpdatingNavigation = true
        try {
            mBinding.bottomNavigationBar.setItemSelected(bottomNavId, true)
        } finally {
            mUpdatingNavigation = false
        }
    }

    private fun startDestination(id: Int) {
        val controller = mNavHostFragment.navController
        if (controller.currentDestination?.id == id) {
            return
        }
        if (controller.currentDestination == null) {
            controller.graph = controller.navInflater.inflate(R.navigation.nav_graph).apply {
                setStartDestination(id)
            }
            return
        }
        // FragmentNavigator commits asynchronously. A second navigation while the
        // current entry is still transitioning can leave FragmentManager and the
        // navigator back stack out of sync.
        if (controller.currentBackStackEntry?.lifecycle?.currentState != Lifecycle.State.RESUMED) {
            syncBottomNavigation(controller.currentDestination?.id)
            return
        }
        controller.navigate(id, null, NavOptions.Builder()
            // Keep the graph's root entry. Popping the graph itself removes every
            // Fragment and can make FragmentManager optimize a remove/add pair
            // that FragmentNavigator can no longer associate with its back stack.
            .setPopUpTo(controller.graph.startDestinationId, false)
            .setLaunchSingleTop(true)
            .build())
    }

    private fun syncBottomNavigation(destinationId: Int?) {
        val bottomId = when (destinationId) {
            R.id.cityFragment, R.id.alertsFragment -> R.id.weather
            R.id.googleMapsFragment -> R.id.map
            R.id.chooseAttractionFragment -> R.id.attractions
            R.id.favoritesFragment -> R.id.favorites
            else -> return
        }
        selectNavigationItem(bottomId)
    }

    private fun handleBannerAd() {

       /* val testDeviceIds = listOf("33BE2250B43518CCDA7DE426D04EE231")
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)
*/
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder().setTestDeviceIds(listOf("ABCDEF012345")).build()
        )

        mBinding.adView.loadAd(adRequest)

        mBinding.adView.adListener = object: AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                val params = bundleOf()
                FireBaseEvents.sendFireBaseCustomEvents(CLICK_ON_BANNER_AD.eventName, params)
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                // Code to be executed when an ad request fails.
                val params = bundleOf(
                    PARAMS_FAILED_TO_LOAD_BANNER_AD.paramsName to adError.message
                )
                FireBaseEvents.sendFireBaseCustomEvents(ON_BANNER_AD_FAILED_TO_LOAD.eventName, params)
            }

            override fun onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
                val params = bundleOf()
                FireBaseEvents.sendFireBaseCustomEvents(ON_BANNER_AD_IMPRESSION.eventName, params)
            }
        }
    }

    private fun loadInterstitialAd() {
        InterstitialAd.load(
            this,
            "ca-app-pub-9058418744370338/1048685069",
            adRequest,
            object : InterstitialAdLoadCallback() {

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

    private fun handleInterstitialAd(destination: Int) {
        val ad = mInterstitialAd
        if (ad == null || showAd < 3 || destination == R.id.cityFragment) {
            showAd++
            startDestination(destination)
            if (ad == null) loadInterstitialAd()
            return
        }
        showAd = 0
        mInterstitialAd = null
        ad.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                val params = bundleOf()
                FireBaseEvents.sendFireBaseCustomEvents(
                    CLICK_ON_INTERSTITIAL_AD.eventName,
                    params
                )
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                val params = bundleOf()
                FireBaseEvents.sendFireBaseCustomEvents(
                    ON_INTERSTITIAL_AD_DISMISSED_FULL_SCREEN_CONTENT.eventName,
                    params
                )
                mInterstitialAd = null
                startDestination(destination)
                loadInterstitialAd()
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
                startDestination(destination)
                loadInterstitialAd()
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
        ad.show(this)
    }


    private fun handleOnBackPressed() {
        onBackPressedDispatcher.addCallback(
            this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    when {
                        mFirsTimeBack -> {
                            Toast.makeText(
                                applicationContext,
                                resources.getString(R.string.again_to_exit),
                                Toast.LENGTH_LONG
                            ).show()
                            mFirsTimeBack = false
                        }
                        else -> {
                            finish()
                        }
                    }
                }
            }
        )
    }
}
