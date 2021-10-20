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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.anychart.AnyChart
import com.evya.myweatherapp.MainData
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.ActivityMainBinding
import com.evya.myweatherapp.ui.dialogs.InfoDialog
import com.evya.myweatherapp.ui.dialogs.PermissionDeniedDialog
import com.evya.myweatherapp.ui.fragments.*
import com.evya.myweatherapp.util.UtilsFunctions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import android.R.attr.name
import android.location.LocationListener


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var mLocationManager: LocationManager
    private lateinit var mLocationRequest: LocationRequest
    var mApprovePermissions = false
    private var mGpsIsOn = false
    private var mThreeSec = false
    private lateinit var mNavHostFragment: NavHostFragment
    private lateinit var mGraph: NavGraph
    private lateinit var mBinding: ActivityMainBinding

    companion object {
        private val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
        private const val THREE_SEC = 3000L
        private const val PERMISSIONS_REQUEST_ID = 1000
        private const val REQUEST_CODE_LOCATION_SETTING = 100
        private const val LOCATION_REFRESH_DISTANCE = 1f
        private const val LOCATION_REFRESH_TIME: Long = 100
    }


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mGpsIsOn = isLocationEnabled()
        UtilsFunctions.setColorSpan(
            6,
            11,
            R.color.black,
            R.string.app_name_title,
            mBinding.appName,
            applicationContext
        )
        mLocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        mLocationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            LOCATION_REFRESH_TIME,
            LOCATION_REFRESH_DISTANCE,
            mLocationListener
        )

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
                }
                R.id.map -> {
                    changeNavBarIndex(R.id.googleMapsFragment, R.id.map)
                }
                R.id.attractions -> {
                    changeNavBarIndex(R.id.chooseAttractionFragment, R.id.attractions)
                }
                R.id.info -> {
                    InfoDialog.newInstance().show(supportFragmentManager, "INFO_DIALOG")
                }
                else -> {
                }
            }

        }
    }

    fun getLastLocation() {
        if (checkPermissions()) {
            if (!isLocationEnabled()) {
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

    private val mLocationListener = LocationListener { location ->
        MainData.mLat = location.latitude.toString()
        MainData.mLong = location.longitude.toString()
        startFlow()
    }


    private fun startFlow() {
        if (mGpsIsOn && mThreeSec) {
            mLocationManager.removeUpdates(mLocationListener)
            mBinding.bottomNavigationBar.setItemSelected(R.id.weather, true)
            mBinding.bottomNavigationBar.visibility = View.VISIBLE
            mBinding.navHostFragment.visibility = View.VISIBLE
            mGraph.startDestination = R.id.cityFragment
            mNavHostFragment.navController.graph = mGraph
        }
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
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
        mGraph.startDestination = destination
        mNavHostFragment.navController.graph = mGraph
        mBinding.bottomNavigationBar.setItemSelected(bottomNavId, true)
    }
}