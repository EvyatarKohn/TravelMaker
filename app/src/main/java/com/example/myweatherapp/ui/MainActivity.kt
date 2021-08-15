package com.example.myweatherapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.myweatherapp.R
import com.example.myweatherapp.ui.dialogs.PermissionDeniedDialog
import com.example.myweatherapp.ui.fragments.CitiesFragment
import com.example.myweatherapp.ui.fragments.CityFragment
import com.example.myweatherapp.ui.fragments.CustomCityFragment
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import android.content.Intent

import android.R.attr.name
import android.net.Uri
import android.provider.Settings


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MainListener {

    private var mUnits = METRIC
    private var mCityName = "Tel-aviv"
    private var mLat: String = "32.083333"
    private var mLong: String = "34.7999968"
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mLocationRequest: LocationRequest

    companion object {
        private const val CELSIUS = "Celsius"
        private const val FAHRENHEIT = "Fahrenheit"
        private const val IMPERIAL = "imperial"
        private const val METRIC = "metric"

        private val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        private const val PERMISSIONS_REQUEST_ID = 1000

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()

        degree_units.setOnClickListener {
            if (degree_units.text.equals(CELSIUS)) {
                degree_units.text = resources.getString(R.string.degree_units_fahrenheit)
                mUnits = IMPERIAL
            } else if (degree_units.text.equals(FAHRENHEIT)) {
                degree_units.text = resources.getString(R.string.degree_units_celsius)
                mUnits = METRIC
            }
            refreshBtn()
        }

        refresh_btn.setOnClickListener {
            refreshBtn()
        }

    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    var location = task.result
                    if (location == null) {
                        getNewLocation()
                    } else {
                        supportFragmentManager.beginTransaction()
                            .replace(
                                R.id.frame_layout,
                                CityFragment.newInstance(mLat, mLong, "", mUnits, this),
                                "CITY_FRAGMENT_LOCATION"
                            )
                            .addToBackStack(null)
                            .commit()
                    }
                }
            } else {
                Toast.makeText(this, "Please Enable your location service and start again", Toast.LENGTH_LONG).show()
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
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.frame_layout,
                    CityFragment.newInstance(mLat, mLong, "", mUnits, this@MainActivity),
                    "CITY_FRAGMENT_LOCATION"
                )
                .addToBackStack(null)
                .commit()
        }
    }

    private fun checkPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST_ID)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService((Context.LOCATION_SERVICE)) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastLocation()
        } else {
            PermissionDeniedDialog().show(supportFragmentManager, "PERMISSION_DENIED_DIALOG")
        }
    }

    private fun refreshBtn() {
        when {
            supportFragmentManager.findFragmentByTag("CITY_FRAGMENT")?.isVisible != null -> {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.frame_layout,
                        CityFragment.newInstance("", "", mCityName, mUnits, this),
                        "CITY_FRAGMENT"
                    )
                    .addToBackStack(null)
                    .commit()
            }
            supportFragmentManager.findFragmentByTag("CITIES_FRAGMENT")?.isVisible != null -> {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.frame_layout,
                        CitiesFragment.newInstance(mUnits, this),
                        "CITIES_FRAGMENT"
                    )
                    .addToBackStack(null)
                    .commit()
            }
            supportFragmentManager.findFragmentByTag("CITY_FRAGMENT_LOCATION")?.isVisible != null -> {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.frame_layout,
                        CityFragment.newInstance(mLat, mLong, "", mUnits, this),
                        "CITY_FRAGMENT_LOCATION"
                    )
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun replaceFragment(cityName: String, lat: String, long: String) {
        mCityName = cityName
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.frame_layout,
                CityFragment.newInstance("", "", cityName, mUnits, this),
                "CITY_FRAGMENT"
            )
            .addToBackStack(null)
            .commit()
    }

    override fun replaceToCitiesListFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, CitiesFragment.newInstance(mUnits, this), "CITIES_FRAGMENT")
            .addToBackStack(null)
            .commit()
    }

    override fun replaceToCustomCityFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.frame_layout,
                CustomCityFragment.newInstance(this),
                "CUSTOM_CITY_FRAGMENT"
            )
            .addToBackStack(null)
            .commit()
    }

    fun goToPermissionSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }


}