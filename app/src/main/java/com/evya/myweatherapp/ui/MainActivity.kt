package com.evya.myweatherapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.evya.myweatherapp.R
import com.evya.myweatherapp.model.placesmodel.Places
import com.evya.myweatherapp.model.weathermodel.Weather
import com.evya.myweatherapp.ui.dialogs.PermissionDeniedDialog
import com.evya.myweatherapp.ui.fragments.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.ArrayList


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MainListener {

    private var mUnits = METRIC
    var mCityName = "Tel-aviv"
    var mCountryCode = "IL"
    private var mLat: String = "32.083333"
    private var mLong: String = "34.7999968"
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mLocationRequest: LocationRequest
    private var mFromTopAdapter = false
    private var mApprovePermissions = false

    companion object {
        private const val CELSIUS = "Celsius"
        private const val FAHRENHEIT = "Fahrenheit"
        private const val IMPERIAL = "imperial"
        private const val METRIC = "metric"
        private const val THREE_SEC = 3000L

        private val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        private const val PERMISSIONS_REQUEST_ID = 1000

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, container).show(WindowInsetsCompat.Type.systemBars())
        setContentView(R.layout.activity_main)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        Handler(Looper.getMainLooper()).postDelayed({
            getLastLocation()
        }, THREE_SEC)
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
        mLocationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0
            fastestInterval = 0
            numUpdates = 2
        }
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

    override fun replaceToCustomCityFragment() {
        showFragment(GoogleMapsFragment.newInstance(mLat, mLong, this), "GOOGLE_MAPS_FRAGMENT")
    }

    override fun showCityWeatherFromList(weather: Weather) {
        showFragment(CityFragment.newInstance(weather, mUnits, this), "CITY_FRAGMENT")
    }

    override fun showAttractionMap(
        localLatLng: LatLng, places: Places
    ) {
        showFragment(
            GoogleMapsAttractionFragment.newInstance(localLatLng, places, this),
            "GOOGLE_MAPS_ATTRACTION_FRAGMENT"
        )
    }

    fun goToPermissionSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        mApprovePermissions = true
    }

    private fun showFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment, tag)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        if (mApprovePermissions) {
            getLastLocation()
        }
    }

    fun turnGPSOn() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 30000
            fastestInterval = 5000
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())
        result.addOnCompleteListener { task ->
            try {
                task.getResult(ApiException::class.java)
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvable = exception as ResolvableApiException
                        resolvable.startResolutionForResult(this@MainActivity, 100)
                    } catch (e: SendIntentException) {
                        e.message?.let { Log.d("TAG", it) }
                    } catch (e: ClassCastException) {
                        e.message?.let { Log.d("TAG", it) }
                    }
                }
            }
            Handler(Looper.getMainLooper()).postDelayed({
                getLastLocation()
            }, THREE_SEC)
        }

    }
}