package com.evya.myweatherapp.ui.fragments

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.evya.myweatherapp.R
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.google_maps_fragment_layout.*


@AndroidEntryPoint
class GoogleMapsFragment : Fragment() {

    private lateinit var mNavController: NavController
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mLat: String
    private lateinit var mLong: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)
        if (!Places.isInitialized()) {
            activity?.applicationContext?.let {
                Places.initialize(
                    it,
                    getString(R.string.google_maps_key)
                )
                Places.createClient(it)
            }
        }

        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i("GoogleMapsFragment", "Place: ${place.name}, ${place.id}")
                mGoogleMap.clear()
                val location = place.name
                val geocoder = Geocoder(activity?.applicationContext)
                val list = geocoder.getFromLocationName(location, 1) as ArrayList<Address>
                if (list.size > 0) {
                    val address = list[0]
                    mLat = address.latitude.toString()
                    mLong = address.longitude.toString()
                    val latLang = LatLng(address.latitude, address.longitude)
                    mGoogleMap.addMarker(MarkerOptions().position(latLang))
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLang, 18f))
                }
            }

            override fun onError(status: Status) {
                showToast(R.string.google_search_error)
            }
        })

        show_weather_btn.setOnClickListener {
            val bundle =
                bundleOf("lat" to mLat.toFloat(), "long" to mLong.toFloat(), "fromMaps" to true)
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
            (childFragmentManager.findFragmentById(R.id.map_layout) as SupportMapFragment)

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

    private fun showToast(error: Int) {
        Toast.makeText(
            activity?.applicationContext,
            activity?.applicationContext?.resources?.getString(error),
            Toast.LENGTH_LONG
        ).show()
    }
}