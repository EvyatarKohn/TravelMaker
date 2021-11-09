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
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.evya.myweatherapp.MainData
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.ActivityMainBinding
import com.evya.myweatherapp.ui.dialogs.InfoDialog
import com.evya.myweatherapp.ui.dialogs.PermissionDeniedDialog
import com.evya.myweatherapp.ui.fragments.*
import com.evya.myweatherapp.util.FireBaseEvents
import com.evya.myweatherapp.util.UtilsFunctions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mLocationRequest: LocationRequest
    var mApprovePermissions = false
    private var mGpsIsOn = false
    private var mThreeSec = false
    private lateinit var mNavHostFragment: NavHostFragment
    private lateinit var mGraph: NavGraph
    private lateinit var mBinding: ActivityMainBinding
    private var mFirsTimeBack = true


    companion object {
        private val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
        private const val THREE_SEC = 3000L
        private const val PERMISSIONS_REQUEST_ID = 1000
        private const val REQUEST_CODE_LOCATION_SETTING = 100
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        FireBaseEvents.init()

        mGpsIsOn = isLocationEnabled()
        UtilsFunctions.setColorSpan(
            6,
            11,
            R.color.black,
            R.string.app_name_title,
            mBinding.appName,
            applicationContext
        )

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mNavHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
        mGraph = mNavHostFragment.navController.navInflater.inflate(R.navigation.nav_graph)

        Handler(Looper.getMainLooper()).postDelayed({
            mThreeSec = true
            getLastLocation()
        }, THREE_SEC)

        mBinding.bottomNavigationBar.setOnItemSelectedListener { id ->
            when (id) {
                R.id.weather -> {
                    mApprovePermissions = true
                    changeNavBarIndex(R.id.cityFragment, R.id.weather)
                    FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.MoveToWeather)
                }
                R.id.map -> {
                    changeNavBarIndex(R.id.googleMapsFragment, R.id.map)
                    FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.MoveToGoogleMap)
                }
                R.id.attractions -> {
                    changeNavBarIndex(R.id.chooseAttractionFragment, R.id.attractions)
                    FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.MoveToAttractions)
                }
                R.id.info -> {
                    InfoDialog.newInstance().show(supportFragmentManager, "INFO_DIALOG")
                    FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.ShowInfo)
                }
                else -> {
                }
            }

        }
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
                        mApprovePermissions = true
                        MainData.mLat = location.latitude.toString()
                        MainData.mLong = location.longitude.toString()
                        startFlow()
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

    private fun startFlow() {
        if (mGpsIsOn && mThreeSec) {
            mBinding.bottomNavigationBar.setItemSelected(R.id.weather, true)
            mBinding.bottomNavigationBar.visibility = View.VISIBLE
            mBinding.navHostFragment.visibility = View.VISIBLE
            mGraph.startDestination = R.id.cityFragment
            mNavHostFragment.navController.graph = mGraph
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
            Looper.myLooper()!!
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation = locationResult.lastLocation
            MainData.mLat = lastLocation.latitude.toString()
            MainData.mLong = lastLocation.longitude.toString()
            getLastLocation()
        }
    }

    private fun checkPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
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

    fun goToPermissionSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        mApprovePermissions = true
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

        val result =
            LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                task.getResult(ApiException::class.java)
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        mApprovePermissions = true
                        mGpsIsOn = true
                        val resolvable = exception as ResolvableApiException
                        resolvable.startResolutionForResult(
                            this@MainActivity,
                            REQUEST_CODE_LOCATION_SETTING
                        )
                        resolvable.status
                    } catch (e: SendIntentException) {
                        e.message?.let { Log.d("TAG", it) }
                    } catch (e: ClassCastException) {
                        e.message?.let { Log.d("TAG", it) }
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
        if (mApprovePermissions && !mGpsIsOn) {
            getLastLocation()
        }
    }

    fun changeNavBarIndex(destination: Int, bottomNavId: Int) {
        mFirsTimeBack = true
        mGraph.startDestination = destination
        mNavHostFragment.navController.graph = mGraph
        mBinding.bottomNavigationBar.setItemSelected(bottomNavId, true)
    }

    override fun onBackPressed() {
        when {
            mNavHostFragment.navController.currentDestination?.label == "GoogleMapsAttractionFragment" -> {
                changeNavBarIndex(R.id.chooseAttractionFragment, R.id.attractions)
            }
            mFirsTimeBack -> {
                Toast.makeText(applicationContext, resources.getString(R.string.again_to_exit), Toast.LENGTH_LONG).show()
                mFirsTimeBack = false
            }
            else -> {
                finish()
            }
        }
    }
}