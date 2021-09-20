package com.evya.myweatherapp.ui.fragments

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import androidx.fragment.app.Fragment
import com.evya.myweatherapp.R
import com.evya.myweatherapp.ui.MainListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GoogleMapsFragment : Fragment() {

    private lateinit var mMainListener: MainListener
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mLat: String
    private lateinit var mLong: String
    private lateinit var mShowWeatherBtn: Button
    private lateinit var mSearch: SearchView

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
        val mapView: SupportMapFragment =
            (childFragmentManager.findFragmentById(R.id.map_layout)) as SupportMapFragment

        mShowWeatherBtn = v.findViewById(R.id.show_weather_btn)
        mSearch = v.findViewById(R.id.search_Bar)

        mSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                mGoogleMap.clear()
                val location = mSearch.query.toString()
                val geocoder = Geocoder(activity?.applicationContext)
                val list = geocoder.getFromLocationName(location, 1) as ArrayList<Address>
                if (list.size > 0) {
                    val address = list[0]
                    mLat = address.latitude.toString()
                    mLong = address.longitude.toString()
                    val latLang = LatLng(address.latitude, address.longitude)
                    mGoogleMap.addMarker(MarkerOptions().position(latLang))
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLang, 10f))
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

        })

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
                mSearch.visibility = View.VISIBLE
            }
            mGoogleMap.setOnMapClickListener { latLng ->
                mGoogleMap.clear()
                mLat = latLng.latitude.toString()
                mLong = latLng.longitude.toString()
                mGoogleMap.addMarker(
                    MarkerOptions().position(LatLng(latLng.latitude, latLng.longitude))
                        .title("lat:$mLat, long: $mLong")
                )
            }

            mShowWeatherBtn = v.findViewById(R.id.show_weather_btn)
            mShowWeatherBtn.setOnClickListener {
                mMainListener.showCityWeather("", mLat, mLong)
            }

        }
        return v
    }
}