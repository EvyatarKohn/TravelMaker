package com.evya.myweatherapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.evya.myweatherapp.R
import com.evya.myweatherapp.model.weathermodel.Weather
import com.evya.myweatherapp.ui.dialogs.PermissionDeniedDialog
import com.evya.myweatherapp.ui.fragments.*
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MainListener {

    private var mUnits = METRIC
    var mCityName = "Tel-aviv"
    var mCountryCode = "IL"
    private var mLat: String = "32.083333"
    private var mLong: String = "34.7999968"
    private var mBoundaryBox = "34,29.5,34.9,36.5,200"
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mLocationRequest: LocationRequest
    private var mFromTopAdapter = false

    companion object {
        private const val CELSIUS = "Celsius"
        private const val FAHRENHEIT = "Fahrenheit"
        private const val IMPERIAL = "imperial"
        private const val METRIC = "metric"
        private const val FIVE_SEC = 5000L

        private val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        private const val PERMISSIONS_REQUEST_ID = 1000

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_main)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        Handler(Looper.getMainLooper()).postDelayed({
            getLastLocation()
        }, FIVE_SEC)
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
                        mLat = location.latitude.toString()
                        mLong = location.longitude.toString()
                        showFragment(
                            CityFragment.newInstance(mLat, mLong, "", mUnits, this),
                            "CITY_FRAGMENT_LOCATION"
                        )
                    }
                }
            } else {
                PermissionDeniedDialog.newInstance(false)
                    .show(supportFragmentManager, "PERMISSION_DENIED_DIALOG")
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getNewLocation() {
        mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 2
        mFusedLocationProviderClient.requestLocationUpdates(
            mLocationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation = locationResult.lastLocation
            mLat = lastLocation.latitude.toString()
            mLong = lastLocation.longitude.toString()
            showFragment(
                CityFragment.newInstance(mLat, mLong, "", mUnits, this@MainActivity),
                "CITY_FRAGMENT_LOCATION"
            )
        }
    }

    private fun checkPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
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
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastLocation()
        } else {
            PermissionDeniedDialog.newInstance(true)
                .show(supportFragmentManager, "PERMISSION_DENIED_DIALOG")
        }
    }

    private fun refreshRelevantFragment() {
        when {
            supportFragmentManager.findFragmentByTag("CITY_FRAGMENT")?.isVisible == true -> {
                val currentFragment = supportFragmentManager.findFragmentByTag("CITY_FRAGMENT")
                (currentFragment as CityFragment).getWeather(mCityName, mUnits)
                currentFragment.getDailyWeather(mCityName, mCountryCode, mUnits)
            }
            supportFragmentManager.findFragmentByTag("CITIES_FRAGMENT")?.isVisible == true -> {
                val currentFragment = supportFragmentManager.findFragmentByTag("CITIES_FRAGMENT")
                (currentFragment as CitiesFragment).getWeather(mCityName, mUnits)
            }
            supportFragmentManager.findFragmentByTag("CITY_FRAGMENT_LOCATION")?.isVisible == true -> {
                val currentFragment =
                    supportFragmentManager.findFragmentByTag("CITY_FRAGMENT_LOCATION")
                if (mFromTopAdapter) {
                    (currentFragment as CityFragment).getWeather(mCityName, mUnits)
                    currentFragment.getDailyWeather(mCityName, mCountryCode, mUnits)
                    mFromTopAdapter = false
                } else {
                    (currentFragment as CityFragment).getCityByLocation(mLat, mLong, mUnits)
                    currentFragment.getDailyWeatherByLocation(mLat, mLong, mUnits)
                }
            }
            supportFragmentManager.findFragmentByTag("GOOGLE_MAPS_FRAGMENT")?.isVisible == true -> {
                showFragment(
                    CityFragment.newInstance(mLat, mLong, "", mUnits, this),
                    "CITY_FRAGMENT"
                )
            }
        }
    }

    override fun showCityWeather(cityName: String, lat: String, long: String) {
        mCityName = cityName
        if (lat.isNotEmpty() && long.isNotEmpty()) {
            mLat = lat
            mLong = long
        }
        mFromTopAdapter = true
        refreshRelevantFragment()
    }

    override fun replaceToCitiesListFragment(boundaryBox: String) {
        if (boundaryBox.isNotEmpty()) {
            mBoundaryBox = boundaryBox
        }
        showFragment(CitiesFragment.newInstance(mUnits, mBoundaryBox, this), "CITIES_FRAGMENT")
    }

    override fun replaceToCustomCityFragment() {
        showFragment(GoogleMapsFragment.newInstance(mLat, mLong, this), "GOOGLE_MAPS_FRAGMENT")
    }

    override fun showCityWeatherFromList(weather: Weather) {
        showFragment(CityFragment.newInstance(weather, mUnits, this), "CITY_FRAGMENT")
    }

    fun goToPermissionSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun showFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment, tag)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }
}