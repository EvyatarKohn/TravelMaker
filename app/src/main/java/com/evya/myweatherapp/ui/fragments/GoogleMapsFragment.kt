package com.evya.myweatherapp.ui.fragments

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.evya.myweatherapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.google_maps_fragment_layout.*

@AndroidEntryPoint
class GoogleMapsFragment : Fragment() {

    private lateinit var mNavController: NavController
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mLat: String
    private lateinit var mLong: String
//    private lateinit var mPlaceAutoCompleteAdapter: PlaceAutocompleteAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)

        search_bar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                mGoogleMap.clear()
                val location = search_bar.query.toString()
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

        show_weather_btn.setOnClickListener {
            val bundle = bundleOf("lat" to mLat.toFloat(), "long" to mLong.toFloat(), "fromMaps" to true)
            mNavController.navigate(R.id.action_googleMapsFragment_to_cityFragment, bundle)
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

        mLat = arguments?.get("lat").toString()
        mLong = arguments?.get("long").toString()

        mapView.getMapAsync { googleMap ->
            mGoogleMap = googleMap
            val markerOptions = MarkerOptions()

            val myLocation = LatLng(mLat.toDouble(), mLong.toDouble())
            markerOptions.position(myLocation)
            mGoogleMap.addMarker(markerOptions)
            val cameraPosition = CameraPosition.Builder().target(myLocation).zoom(18f).build()
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            mGoogleMap.setOnMapLoadedCallback {
                show_weather_btn.visibility = View.VISIBLE
                search_bar.visibility = View.VISIBLE
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
        }
        return v
    }
}