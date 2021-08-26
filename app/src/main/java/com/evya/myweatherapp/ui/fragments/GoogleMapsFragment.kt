package com.evya.myweatherapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.evya.myweatherapp.R
import com.evya.myweatherapp.ui.MainListener
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class GoogleMapsFragment : Fragment() {

    private lateinit var mMainListener: MainListener
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mLat: String
    private lateinit var mLong: String
    private lateinit var mShowWeatherBtn: Button


    companion object {
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
        val mapView: SupportMapFragment = (childFragmentManager.findFragmentById(R.id.map_layout)) as SupportMapFragment

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
                mLat = latLng.latitude.toString()
                mLong = latLng.longitude.toString()
                val myLocation = LatLng(latLng.latitude, latLng.longitude)
                mGoogleMap.addMarker(
                    MarkerOptions().position(myLocation).title("Marker Title")
                        .snippet("Marker Description")
                )
            }
        }

        mShowWeatherBtn = v.findViewById(R.id.show_weather_btn)
        mShowWeatherBtn.setOnClickListener {
            mMainListener.replaceFragment("", mLat, mLong)
        }

        return v
    }
}