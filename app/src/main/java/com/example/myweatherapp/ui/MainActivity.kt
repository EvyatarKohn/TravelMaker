package com.example.myweatherapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.myweatherapp.R
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MainListener {
    private val mViewModel: MainViewModel by viewModels()
    private var mUnits = "metric"
    private var mCityName = "jerusalem"
    private var mLat: String = "32.0853"
    private var mLong: String = "34.781768"
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mLocationRequest: LocationRequest

    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private val PERMISSIONS_REQUEST_ID = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()

        degree_units.setOnClickListener {
            if (degree_units.text.equals("Celsius")) {
                degree_units.text = "Fahrenheit"
                mUnits = "imperial"
            } else if (degree_units.text.equals("Fahrenheit")) {
                degree_units.text = "Celsius"
                mUnits = "metric"
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
                mFusedLocationProviderClient.lastLocation.addOnCompleteListener{task ->
                    var location = task.result
                    if (location == null) {
                        getNewLocation()
                    } else {
                        mLat = location.latitude.toString()
                        mLong = location.longitude.toString()

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
                Toast.makeText(this, "Please Enable your location service", Toast.LENGTH_LONG)
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
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper())
    }

    private val locationCallback = object: LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation = locationResult.lastLocation

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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST_ID)
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager = getSystemService((Context.LOCATION_SERVICE)) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastLocation()
        }
    }

    private fun refreshBtn() {
        when {
            supportFragmentManager.findFragmentByTag("CITY_FRAGMENT")?.isVisible != null-> {
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
                        CityFragment.newInstance(mLat, mLong,"", mUnits, this),
                        "CITY_FRAGMENT_LOCATION"
                    )
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun replaceFragment(cityName: String) {
        mCityName = cityName
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, CityFragment.newInstance("", "", cityName, mUnits, this), "CITY_FRAGMENT")
            .addToBackStack(null)
            .commit()
    }

    override fun replaceToCitiesListFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, CitiesFragment.newInstance(mUnits, this), "CITIES_FRAGMENT")
            .addToBackStack(null)
            .commit()
    }
}