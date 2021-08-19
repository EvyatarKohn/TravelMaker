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
            mGoogleMap.setOnMapClickListener { latLng ->

                // when clicked on map initialized marker option
                val markerOptions = MarkerOptions()
                // set position of marker
                markerOptions.position(latLng)
                mLat = latLng.latitude.toString()
                mLong = latLng.longitude.toString()
                // set title of marker
                markerOptions.title(latLng.latitude.toString() + " : " + latLng.longitude.toString())
                // remove all marker
                mGoogleMap.clear()
                // animating to zoom the  marker
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10F))
                // add marker on map
                mGoogleMap.addMarker(markerOptions)

            }
            val myLocation = LatLng(mLat.toDouble(), (mLong.toDouble()))
            mGoogleMap.addMarker(
                MarkerOptions().position(myLocation).title("Marker Title")
                    .snippet("Marker Description")
            )

            // For zooming automatically to the location of the marker
            val cameraPosition = CameraPosition.Builder().target(myLocation).zoom(12f).build()
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        }

        mShowWeatherBtn = v.findViewById(R.id.show_weather_btn)
        mShowWeatherBtn.setOnClickListener {
            mMainListener.replaceFragment("", mLat, mLong)
        }

        return v
    }
}