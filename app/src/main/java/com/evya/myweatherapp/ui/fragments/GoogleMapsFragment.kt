package com.evya.myweatherapp.ui.fragments

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.evya.myweatherapp.R
import com.evya.myweatherapp.ui.MainListener
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*


class GoogleMapsFragment : Fragment() {

    private lateinit var mMainListener: MainListener
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mLat: String
    private lateinit var mLong: String
    private lateinit var mShowWeatherBtn: Button


    companion object {
        private const val CIRCLE_RADIUS = 250000.0

        fun newInstance(lat: String, long: String, mainListener: MainListener) =
            GoogleMapsFragment().apply {
                mLat = lat
                mLong = long
                mMainListener = mainListener
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.google_maps_fragment_layout, container, false)
        val mapView: SupportMapFragment =
            (childFragmentManager.findFragmentById(R.id.map_layout)) as SupportMapFragment

        mapView.getMapAsync { googleMap ->
            mGoogleMap = googleMap
            val markerOptions = MarkerOptions()
            val myLocation = LatLng(mLat.toDouble(), mLong.toDouble())
            markerOptions.position(myLocation)
            mGoogleMap.addMarker(markerOptions)
            val cameraPosition = CameraPosition.Builder().target(myLocation).zoom(18f).build()
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            mGoogleMap.setOnMapLoadedCallback {
                mShowWeatherBtn.visibility = View.VISIBLE
            }
            mGoogleMap.setOnMapClickListener { latLng ->
                mGoogleMap.clear()
                mLat = latLng.latitude.toString()
                mLong = latLng.longitude.toString()
                val myLocation = LatLng(latLng.latitude, latLng.longitude)
                mGoogleMap.addMarker(
                    MarkerOptions().position(myLocation).title("lat:$mLat, long: $mLong")
                )

                val circleOptions = mGoogleMap.addCircle(
                    CircleOptions()
                        .center(LatLng(mLat.toDouble(), mLong.toDouble()))
                        .radius(CIRCLE_RADIUS)
                        .strokeWidth(5f)
                        .strokeColor(Color.GREEN)
                        .fillColor(Color.GREEN)
                        .clickable(true)
                )
                circleOptions.strokePattern
            }

            mShowWeatherBtn = v.findViewById(R.id.show_weather_btn)
            mShowWeatherBtn.setOnClickListener {
                mMainListener.showCityWeather("", mLat, mLong)
            }

        }
        return v
    }

}