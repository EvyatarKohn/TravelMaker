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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.NavHostFragment
import com.evya.myweatherapp.R
import com.evya.myweatherapp.ui.dialogs.InfoDialog
import com.evya.myweatherapp.ui.dialogs.PermissionDeniedDialog
import com.evya.myweatherapp.ui.fragments.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    var mLat: String = ""//"32.083333"
    var mLong: String = ""// "34.7999968
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mLocationRequest: LocationRequest
    var mApprovePermissions = false
    private var mGspIsOn = false
    private var mThreeSec = false
    private lateinit var mBottomNavigationBar: ChipNavigationBar
    private lateinit var mNavHostFragment: NavHostFragment
    private lateinit var mFragmentContainerView: FragmentContainerView
    private lateinit var mAppName: TextView

    companion object {
        private val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private const val THREE_SEC = 3000L
        private const val PERMISSIONS_REQUEST_ID = 1000
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, container).show(WindowInsetsCompat.Type.systemBars())
        setContentView(R.layout.activity_main)

        mGspIsOn = isLocationEnabled()
        mAppName = findViewById(R.id.app_name)
        val span = SpannableString(resources.getString(R.string.app_name_title))
        span.setSpan(ForegroundColorSpan(ContextCompat.getColor(applicationContext, R.color.black)), 6, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        mAppName.text = span

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        mBottomNavigationBar = findViewById(R.id.bottom_navigation_bar)
        mFragmentContainerView = findViewById(R.id.nav_host_fragment)
        mNavHostFragment =  (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)

        Handler(Looper.getMainLooper()).postDelayed({
            mThreeSec = true
            getLastLocation()
        }, THREE_SEC)
        mBottomNavigationBar.setOnItemSelectedListener { id ->
            val inflater = mNavHostFragment.navController.navInflater
            val graph = inflater.inflate(R.navigation.nav_graph)
            when (id) {
                R.id.weather -> {
                    mApprovePermissions = true
                    graph.startDestination = R.id.cityFragment
                    mNavHostFragment.navController.graph = graph
                }
                R.id.maps -> {
                    graph.startDestination = R.id.googleMapsFragment
                    mNavHostFragment.navController.graph = graph
                }
                R.id.attractions -> {
                    graph.startDestination = R.id.chooseAttractionFragment
                    mNavHostFragment.navController.graph = graph
                }
                R.id.info -> {
                    InfoDialog.newInstance().show(supportFragmentManager, "INFO_DIALOG")
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
                        mLat = location.latitude.toString()
                        mLong = location.longitude.toString()
                       startFlow()
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

    private fun startFlow() {
        if (mGspIsOn && mThreeSec) {
            mBottomNavigationBar.setItemSelected(R.id.weather, true)
            mBottomNavigationBar.visibility = View.VISIBLE
            mFragmentContainerView.visibility = View.VISIBLE
            val inflater = mNavHostFragment.navController.navInflater
            val graph = inflater.inflate(R.navigation.nav_graph)
            graph.startDestination = R.id.cityFragment
            mNavHostFragment.navController.graph = graph
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
            mLat = lastLocation.latitude.toString()
            mLong = lastLocation.longitude.toString()
            getLastLocation()
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

    fun goToPermissionSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        mApprovePermissions = true
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

        val result =
            LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                task.getResult(ApiException::class.java)
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        mApprovePermissions = true
                        mGspIsOn = true
                        val resolvable = exception as ResolvableApiException
                        resolvable.startResolutionForResult(this@MainActivity, 100)
                    } catch (e: SendIntentException) {
                        e.message?.let { Log.d("TAG", it) }
                    } catch (e: ClassCastException) {
                        e.message?.let { Log.d("TAG", it) }
                    }
                }
            }
        }
    }
}