package com.evya.myweatherapp.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.evya.myweatherapp.R
import com.evya.myweatherapp.ui.MainListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CircleOptions
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
    private lateinit var mRadiusBtn: Button
    private lateinit var mBtnLayout: LinearLayout
    private lateinit var mTitle: TextView
    private var mBoundaryBox = "34,29.5,34.9,36.5,200" /*"west:34,south:29.5,east:34.9,north:36.5,200"*/



    companion object {
        private const val CIRCLE_RADIUS = 277000.0

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

        mBtnLayout = v.findViewById(R.id.btn_layout)
        mTitle = v.findViewById(R.id.google_maps_title)

        mapView.getMapAsync { googleMap ->
            mGoogleMap = googleMap
            val markerOptions = MarkerOptions()
            val myLocation = LatLng(mLat.toDouble(), mLong.toDouble())
            markerOptions.position(myLocation)
            mGoogleMap.addMarker(markerOptions)
            val cameraPosition = CameraPosition.Builder().target(myLocation).zoom(18f).build()
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            mGoogleMap.setOnMapLoadedCallback {
                mBtnLayout.visibility = View.VISIBLE
                mTitle.visibility = View.VISIBLE
            }
            mGoogleMap.setOnMapClickListener { latLng ->
                mGoogleMap.clear()
                mLat = latLng.latitude.toString()
                mLong = latLng.longitude.toString()
                val myLocation = LatLng(latLng.latitude, latLng.longitude)
                mGoogleMap.addMarker(
                    MarkerOptions().position(myLocation).title("lat:$mLat, long: $mLong")
                )
                mRadiusBtn = v.findViewById(R.id.show_area_radius)
                mRadiusBtn.setOnClickListener {
                    mRadiusBtn.text = resources.getString(R.string.show_cities_list)
                    drawCircleAndGetCitiesList(latLng)
                }

            }

            mShowWeatherBtn = v.findViewById(R.id.show_weather_btn)
            mShowWeatherBtn.setOnClickListener {
                mMainListener.showCityWeather("", mLat, mLong)
            }

        }
        return v
    }

    private fun drawCircleAndGetCitiesList(latLng: LatLng) {
        mGoogleMap.addCircle(
            CircleOptions()
                .center(LatLng(mLat.toDouble(), mLong.toDouble()))
                .radius(CIRCLE_RADIUS)
                .strokeWidth(3f)
                .strokeColor(Color.BLUE)
                .clickable(true)
        )
        //West
        val westLocation = LatLng(latLng.latitude, latLng.longitude - 2.5)
        //North
        val northLocation = LatLng(latLng.latitude + 2.5, latLng.longitude)
        //East
        val eastLocation = LatLng(latLng.latitude, latLng.longitude + 2.5)
        //South
        val southLocation = LatLng(latLng.latitude - 2.5, latLng.longitude)


        mBoundaryBox = westLocation.longitude.toString() + "," + southLocation.latitude + "," + eastLocation.longitude + "," + northLocation.latitude + ",200"
        mRadiusBtn.setOnClickListener {
            mMainListener.replaceToCitiesListFragment(mBoundaryBox)
        }
    }

}