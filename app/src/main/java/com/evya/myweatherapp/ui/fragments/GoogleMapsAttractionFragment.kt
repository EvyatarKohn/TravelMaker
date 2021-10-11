package com.evya.myweatherapp.ui.fragments

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.evya.myweatherapp.MainData
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.GoogleMapsAttractionFragmentLayoutBinding
import com.evya.myweatherapp.model.placesmodel.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GoogleMapsAttractionFragment : Fragment(R.layout.google_maps_attraction_fragment_layout) {
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mPlaces: Places
    private lateinit var mMyLatLong: LatLng
    private lateinit var mMarkerTitle: String
    private lateinit var mNavController: NavController
    private lateinit var mBinding: GoogleMapsAttractionFragmentLayoutBinding

    @SuppressLint("PotentialBehaviorOverride")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)
        mBinding = GoogleMapsAttractionFragmentLayoutBinding.bind(view)

        val mapView: SupportMapFragment =
            (childFragmentManager.findFragmentById(mBinding.mapLayout.id)) as SupportMapFragment

        mapView.getMapAsync { googleMap ->
            mGoogleMap = googleMap
            mMyLatLong = LatLng(
                MainData.mLat.toDouble(),
                MainData.mLong.toDouble()
            )
            mPlaces = arguments?.getParcelable("places")!!

            val markerOptions = MarkerOptions()
            mPlaces.features.forEach {
                markerOptions.position(
                    LatLng(
                        it.geometry.coordinates[1],
                        it.geometry.coordinates[0]
                    )
                ).title(it.properties.name)
                    .snippet(resources.getString(R.string.more_details))
                mGoogleMap.addMarker(markerOptions)
            }

            val cameraPosition = CameraPosition.Builder().target(mMyLatLong).zoom(11f).build()

            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            mGoogleMap.setOnMarkerClickListener { marker ->
                marker.showInfoWindow()
                mMarkerTitle = marker.title!!
                true
            }

            mGoogleMap.setOnMapLongClickListener {
                val googleSearchIntent = Intent(Intent.ACTION_WEB_SEARCH)
                googleSearchIntent.putExtra(SearchManager.QUERY, mMarkerTitle)
                googleSearchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                googleSearchIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                startActivity(googleSearchIntent)
            }
        }

        mBinding.backArrow.setOnClickListener {

            mNavController.navigate(
                R.id.action_googleMapsAttractionFragment_to_chooseAttractionFragment,
            )
        }
    }
}