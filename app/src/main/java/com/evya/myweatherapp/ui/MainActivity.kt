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
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.evya.myweatherapp.Constants.PERMISSIONS_REQUEST_ID
import com.evya.myweatherapp.Constants.REQUEST_CODE_LOCATION_SETTING
import com.evya.myweatherapp.Constants.THREE_SEC
import com.evya.myweatherapp.MainData.approvedPermissions
import com.evya.myweatherapp.MainData.lat
import com.evya.myweatherapp.MainData.long
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.ActivityMainBinding
import com.evya.myweatherapp.ui.dialogs.InfoDialog
import com.evya.myweatherapp.ui.dialogs.PermissionDeniedDialog
import com.evya.myweatherapp.util.AdsUtils.Companion.getAdRequest
import com.evya.myweatherapp.util.AdsUtils.Companion.handleAds
import com.evya.myweatherapp.util.FireBaseEvents
import com.evya.myweatherapp.util.UtilsFunctions
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mLocationRequest: LocationRequest
    private var mGpsIsOn = false
    private var mThreeSec = false
    private lateinit var mNavHostFragment: NavHostFragment
    private lateinit var mGraph: NavGraph
    private lateinit var mBinding: ActivityMainBinding
    private var mFirsTimeBack = true
    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var mAdRequest: AdRequest

    companion object {
        private val TAG = MainActivity::class.toString()

        private val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        FireBaseEvents.init(this)
        MobileAds.initialize(this) {}

        mBinding.adView.loadAd(getAdRequest())

        handleOnBackPressed()

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
            handleAds(this, TAG)
            navigateToRelevantScreen(id)
        }
    }

    private fun navigateToRelevantScreen(id: Int) {
        var firebaseEvent = FireBaseEvents.FirebaseEventsStrings.MoveToWeather

        when (id) {
            R.id.weather -> {
                approvedPermissions = true
                changeNavBarIndex(R.id.cityFragment, R.id.weather)
                firebaseEvent = FireBaseEvents.FirebaseEventsStrings.MoveToWeather
            }
            R.id.map -> {
                changeNavBarIndex(R.id.googleMapsFragment, R.id.map)
                firebaseEvent = FireBaseEvents.FirebaseEventsStrings.MoveToGoogleMap
            }
            R.id.attractions -> {
                changeNavBarIndex(R.id.chooseAttractionFragment, R.id.attractions)
                firebaseEvent = FireBaseEvents.FirebaseEventsStrings.MoveToAttractions
            }

            R.id.favorites -> {
                changeNavBarIndex(R.id.favoritesFragment, R.id.favorites)
                firebaseEvent = FireBaseEvents.FirebaseEventsStrings.MoveToFavorites
            }

            R.id.info -> {
                InfoDialog.newInstance().show(supportFragmentManager, "INFO_DIALOG")
                firebaseEvent = FireBaseEvents.FirebaseEventsStrings.ShowInfo
            }
            else -> {
            }
        }
        FireBaseEvents.sendFireBaseCustomEvents(firebaseEvent)
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
            mBinding.adView.visibility = View.VISIBLE
            mBinding.navHostFragment.visibility = View.VISIBLE
            startDestination(R.id.cityFragment)

        }
    }

    @SuppressLint("MissingPermission")
    private fun getNewLocation() {
        mLocationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
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
            lat = lastLocation?.latitude.toString()
            long = lastLocation?.longitude.toString()
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
        approvedPermissions = true
    }

    fun turnGPSOn() {
        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
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

    override fun onResume() {
        super.onResume()
        if (approvedPermissions && !mGpsIsOn) {
            getLastLocation()
        }
    }

    fun changeNavBarIndex(destination: Int, bottomNavId: Int) {
        mFirsTimeBack = true
        startDestination(destination)
        mNavHostFragment.navController.graph = mGraph
        mBinding.bottomNavigationBar.setItemSelected(bottomNavId, true)
    }

    private fun startDestination(id: Int) {
        mGraph.startDestination = id
        mNavHostFragment.navController.graph = mGraph
        mNavHostFragment.navController.navigate(id)
    }


    private fun handleOnBackPressed() {
        onBackPressedDispatcher.addCallback(
            this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    when {
                        mNavHostFragment.navController.currentDestination?.label == "GoogleMapsAttractionFragment" -> {
                            changeNavBarIndex(R.id.chooseAttractionFragment, R.id.attractions)
                        }
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