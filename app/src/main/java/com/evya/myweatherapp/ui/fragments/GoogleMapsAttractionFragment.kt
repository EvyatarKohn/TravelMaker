package com.evya.myweatherapp.ui.fragments

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.evya.myweatherapp.R
import com.evya.myweatherapp.model.placesmodel.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GoogleMapsAttractionFragment : Fragment(){
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mPlaces: Places
    private lateinit var mMyLatLong: LatLng

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

            mMyLatLong = LatLng(arguments?.getString("lat")?.toDouble()!!, arguments?.getString("long")?.toDouble()!!)
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

            val cameraPosition = CameraPosition.Builder().target(mMyLatLong).zoom(15f).build()
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            mGoogleMap.setOnMapLongClickListener {
                val googleIntent = Intent(Intent.ACTION_WEB_SEARCH)
                googleIntent.putExtra(SearchManager.QUERY, markerOptions.title)
                googleIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                googleIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                startActivity(googleIntent)
            }
        }

        return v
    }
}