package com.evya.myweatherapp.ui.fragments

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.evya.myweatherapp.MainData.lat
import com.evya.myweatherapp.MainData.long
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.GoogleMapsFragmentLayoutBinding
import com.evya.myweatherapp.ui.MainActivity
import com.evya.myweatherapp.util.FireBaseEvents
import com.evya.myweatherapp.util.UtilsFunctions
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
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class GoogleMapsFragment : Fragment(R.layout.google_maps_fragment_layout) {

    private lateinit var mNavController: NavController
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mBinding: GoogleMapsFragmentLayoutBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)
        mBinding = GoogleMapsFragmentLayoutBinding.bind(view)

        val mapView: SupportMapFragment =
            (childFragmentManager.findFragmentById(mBinding.mapLayout.id) as SupportMapFragment)

        mapView.getMapAsync { googleMap ->
            mGoogleMap = googleMap
            val markerOptions = MarkerOptions()

            val myLocation = LatLng(lat.toDouble(), long.toDouble())
            markerOptions.position(myLocation)
            mGoogleMap.addMarker(markerOptions)
            val cameraPosition = CameraPosition.Builder().target(myLocation).zoom(18f).build()
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            mGoogleMap.setOnMapLoadedCallback {
                mBinding.showWeatherBtn.visibility = View.VISIBLE
            }
            mGoogleMap.setOnMapClickListener { latLng ->
                mGoogleMap.clear()
                lat = latLng.latitude.toString()
                long = latLng.longitude.toString()
                mGoogleMap.addMarker(
                    MarkerOptions().position(LatLng(latLng.latitude, latLng.longitude))
                )
            }
        }

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
        val autocompleteFragment =
            childFragmentManager.findFragmentById(mBinding.autocompleteFragment.id)
                    as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))

        // Set up a PlaceSelectionListener to handle the response.
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i("GoogleMapsFragment", "Place: ${place.name}, ${place.id}")
                mGoogleMap.clear()
                val location = place.name
                val geocoder = activity?.applicationContext?.let { Geocoder(it) }
                val list = geocoder?.getFromLocationName(location, 1) as ArrayList<Address>
                if (list.size > 0) {
                    val address = list[0]
                    lat = address.latitude.toString()
                    long = address.longitude.toString()
                    lat = address.latitude.toString()
                    long = address.longitude.toString()
                    val latLang = LatLng(address.latitude, address.longitude)
                    mGoogleMap.addMarker(MarkerOptions().position(latLang))
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLang, 18f))
                    FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.SearchInGoogleMap)
                }
            }

            override fun onError(status: Status) {
                UtilsFunctions.showToast(R.string.google_search_error, activity?.applicationContext)
            }
        })

        mBinding.showWeatherBtn.setOnClickListener {
            FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.ShowWeather)
            mNavController.navigate(R.id.action_googleMapsFragment_to_cityFragment)
            (activity as MainActivity).changeNavBarIndex(R.id.cityFragment, R.id.weather)
        }
    }
}