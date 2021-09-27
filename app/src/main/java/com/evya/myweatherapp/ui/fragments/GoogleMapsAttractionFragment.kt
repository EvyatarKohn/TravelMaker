package com.evya.myweatherapp.ui.fragments

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.evya.myweatherapp.R
import com.evya.myweatherapp.model.placesmodel.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.google_maps_attraction_fragment_layout.*


@AndroidEntryPoint
class GoogleMapsAttractionFragment : Fragment() {
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mPlaces: Places
    private lateinit var mMyLatLong: LatLng
    private lateinit var mMarkerTitle: String
    private lateinit var mNavController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)
        back_arrow.setOnClickListener {
            val bundle = bundleOf(
                "lat" to mMyLatLong.latitude.toFloat(),
                "long" to mMyLatLong.longitude.toFloat(),
                "fromMaps" to true
            )
            mNavController.navigate(
                R.id.action_googleMapsAttractionFragment_to_chooseAttractionFragment,
                bundle
            )
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.google_maps_attraction_fragment_layout, container, false)
        val mapView: SupportMapFragment =
            (childFragmentManager.findFragmentById(R.id.map_layout)) as SupportMapFragment

        mapView.getMapAsync { googleMap ->
            mGoogleMap = googleMap
            mMyLatLong = LatLng(
                arguments?.getString("lat")?.toDouble()!!,
                arguments?.getString("long")?.toDouble()!!
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

        return v
    }
}