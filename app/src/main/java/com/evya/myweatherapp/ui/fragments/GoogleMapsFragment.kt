package com.evya.myweatherapp.ui.fragments

import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.evya.myweatherapp.Constants.CITY_NAME
import com.evya.myweatherapp.Constants.FROM_GOOGLE_MAPS
import com.evya.myweatherapp.Constants.LAT
import com.evya.myweatherapp.Constants.LONG
import com.evya.myweatherapp.MainData.cityName
import com.evya.myweatherapp.MainData.lat
import com.evya.myweatherapp.MainData.long
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.GoogleMapsFragmentLayoutBinding
import com.evya.myweatherapp.firebaseanalytics.FireBaseEvents
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsNamesStrings.SEARCH_IN_GOOGLE_MAP
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsNamesStrings.SHOW_WEATHER
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsParamsStrings.PARAMS_CITY_NAME
import com.evya.myweatherapp.util.UtilsFunctions.Companion.showToast
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class GoogleMapsFragment : Fragment(R.layout.google_maps_fragment_layout) {

    private lateinit var mNavController: NavController
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mBinding: GoogleMapsFragmentLayoutBinding
    private lateinit var mAddress: Address
    private var mLocation: String? = null
    private var savedCamera: CameraPosition? = null
    private var geocodeJob: Job? = null

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (::mGoogleMap.isInitialized) outState.putParcelable("map_camera", mGoogleMap.cameraPosition)
        if (::mAddress.isInitialized) outState.putParcelable("map_address", mAddress)
        outState.putString("map_location", mLocation)
    }

    override fun onDestroyView() {
        geocodeJob?.cancel()
        if (::mGoogleMap.isInitialized) savedCamera = mGoogleMap.cameraPosition
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)
        mBinding = GoogleMapsFragmentLayoutBinding.bind(view)
        @Suppress("DEPRECATION")
        val restoredCamera = savedInstanceState?.getParcelable<CameraPosition>("map_camera") ?: savedCamera
        @Suppress("DEPRECATION")
        val restoredAddress = savedInstanceState?.getParcelable<Address>("map_address")
        if (restoredAddress != null) mAddress = restoredAddress
        mLocation = savedInstanceState?.getString("map_location") ?: mLocation

        val mapView: SupportMapFragment =
            (childFragmentManager.findFragmentById(mBinding.mapLayout.id) as SupportMapFragment)

        mapView.getMapAsync { googleMap ->
            mGoogleMap = googleMap
            val markerOptions = MarkerOptions()

            val myLocation = if (::mAddress.isInitialized) LatLng(mAddress.latitude, mAddress.longitude)
                else LatLng(lat.toDoubleOrNull() ?: 0.0, long.toDoubleOrNull() ?: 0.0)
            markerOptions.position(myLocation)
            mGoogleMap.addMarker(markerOptions)
            val cameraPosition = CameraPosition.Builder().target(myLocation).zoom(18f).build()
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(restoredCamera ?: cameraPosition))
            mBinding.showWeatherBtn.visibility = if (::mAddress.isInitialized) View.VISIBLE else View.GONE
            mGoogleMap.setOnMapLoadedCallback {
//                mBinding.showWeatherBtn.visibility = View.VISIBLE
            }
            mGoogleMap.setOnMapClickListener { latLng ->
                geocodeJob?.cancel()
                mGoogleMap.clear()
                val fallback = getString(R.string.selected_map_location)
                applyResolvedAddress(createCoordinateAddress(latLng), fallback)
                val geocoder = Geocoder(requireContext().applicationContext, Locale.getDefault())
                geocodeJob = viewLifecycleOwner.lifecycleScope.launch {
                    val address = withContext(Dispatchers.IO) {
                        try { geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)?.firstOrNull() }
                        catch (_: IOException) { null }
                    }
                    if (address != null) {
                        mGoogleMap.clear()
                        applyResolvedAddress(address, fallback)
                    }
                }
            }
        }

        if (!Places.isInitialized()) {
            activity?.applicationContext?.let {
                Places.initialize(
                    it,
                    getString(R.string.google_maps_key),
                    Locale.US
                )
                Places.createClient(it)
            }
        }

        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment =
            childFragmentManager.findFragmentById(mBinding.autocompleteFragment.id)
                    as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.DISPLAY_NAME, Place.Field.LOCATION))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                if (!::mGoogleMap.isInitialized) return
                val coordinates = place.location ?: return
                geocodeJob?.cancel()
                mGoogleMap.clear()
                applyResolvedAddress(createCoordinateAddress(coordinates), place.displayName)
            }

            override fun onError(status: Status) {
                showToast("${context?.resources?.getString(R.string.google_search_error)}: ${status.statusMessage}")
            }
        })

        mBinding.showWeatherBtn.setOnClickListener {
            if (!::mAddress.isInitialized || mNavController.currentDestination?.id != R.id.googleMapsFragment) return@setOnClickListener
            val address = try {
                mLocation ?: arguments?.getString("cityName") ?: ""
            } catch (e: Exception) {
                arguments?.getString("cityName") ?: ""
            }

            val params = bundleOf(
                PARAMS_CITY_NAME.paramsName to address,
            )

            FireBaseEvents.sendFireBaseCustomEvents(SHOW_WEATHER.eventName, params)

            val bundle = bundleOf(
                LAT to mAddress.latitude.toFloat(),
                LONG to mAddress.longitude.toFloat(),
                CITY_NAME to (mLocation ?: ""),
                FROM_GOOGLE_MAPS to true
            )
            mNavController.navigate(R.id.action_googleMapsFragment_to_cityFragment, bundle)
        }
    }

    private fun getAddressForSdkEarlierTheTiramisu(location: String?, geocoder: Geocoder?) {
        val list = try {
            location?.let {
                geocoder?.getFromLocationName(it, 1)
            }
        } catch (e: IOException) {
            Log.w("GoogleMapsFragment", "Unable to geocode location name: $location", e)
            null
        }
        list?.size?.let { listSize ->
            if (listSize > 0) {
                CoroutineScope(Dispatchers.Main).launch {
                    applyResolvedAddress(list[0], location)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun getAddressForTiramisuAndAbove(location: String?, geocoder: Geocoder?) {
        try {
            location?.let {
                geocoder?.getFromLocationName(it, 1) { list ->
                    if (list.size > 0) {
                        CoroutineScope(Dispatchers.Main).launch {
                            applyResolvedAddress(list[0], location)
                        }
                    }
                }
            }
        } catch (e: IOException) {
            Log.w("GoogleMapsFragment", "Unable to geocode location name: $location", e)
        }
    }

    private fun getAddressFromLocation(latLng: LatLng): Address? {
        return try {
            Geocoder(requireContext(), Locale.getDefault())
                .getFromLocation(latLng.latitude, latLng.longitude, 1)
                ?.firstOrNull()
        } catch (e: IOException) {
            Log.w("GoogleMapsFragment", "Unable to reverse geocode map location", e)
            null
        }
    }

    private fun createCoordinateAddress(latLng: LatLng): Address {
        return Address(Locale.getDefault()).apply {
            latitude = latLng.latitude
            longitude = latLng.longitude
        }
    }

    private fun applyResolvedAddress(address: Address, fallbackName: String?) {
        mAddress = address
        mBinding.showWeatherBtn.visibility = View.VISIBLE
        lat = address.latitude.toString()
        long = address.longitude.toString()
        val placeName = resolvePlaceName(address, fallbackName)
        cityName = placeName
        mLocation = placeName
        val latLang = LatLng(address.latitude, address.longitude)
        mGoogleMap.addMarker(MarkerOptions().position(latLang))
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLang, 18f))
        FireBaseEvents.sendFireBaseCustomEvents(
            SEARCH_IN_GOOGLE_MAP.eventName,
            bundleOf(PARAMS_CITY_NAME.paramsName to placeName)
        )
    }

    private fun resolvePlaceName(address: Address, fallback: String?): String {
        return listOf(
            address.locality,
            address.subLocality,
            address.subAdminArea,
            address.adminArea,
            address.featureName,
            fallback,
            mLocation
        ).firstOrNull { !it.isNullOrBlank() }.orEmpty()
    }
}
