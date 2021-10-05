package com.evya.myweatherapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.evya.myweatherapp.Constants
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.ChooseAttractionFragmentLayoutBinding
import com.evya.myweatherapp.ui.MainActivity
import com.evya.myweatherapp.ui.dialogs.NoAttractionFoundDialog
import com.evya.myweatherapp.viewmodels.PlacesViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ChooseAttractionFragment : Fragment(R.layout.choose_attraction_fragment_layout) {

    private val mPlacesViewModel: PlacesViewModel by viewModels()
    private var mLat: String = "" //"32.083333"
    private var mLong: String = "" //"34.7999968"
    private lateinit var mNavController: NavController
    private lateinit var mBinding: ChooseAttractionFragmentLayoutBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = ChooseAttractionFragmentLayoutBinding.bind(view)

        mNavController = Navigation.findNavController(view)
        setOnClickListener()

        mLat = (activity as MainActivity).mLat
        mLong = (activity as MainActivity).mLong

        val adapter = ArrayAdapter(
            activity?.applicationContext!!,
            android.R.layout.select_dialog_item,
            Constants.replacedList()
        )
        mBinding.autoCompleteTextview.threshold = 1
        mBinding.autoCompleteTextview.setAdapter(adapter)
        mBinding.autoCompleteTextview.setOnItemClickListener { adapterView, view, i, l ->
            whatToDo(
                mBinding.autoCompleteTextview.text.toString().replace(" ", "_"),
                R.string.general_error
            )
        }

        mPlacesViewModel.placesRepo.observe(viewLifecycleOwner, { places ->
            val latLong: ArrayList<LatLng> = ArrayList()
            places.features.forEach {
                latLong.add(LatLng(it.geometry.coordinates[0], it.geometry.coordinates[1]))
            }
            if (latLong.size > 0) {
                val bundle = bundleOf("lat" to mLat, "long" to mLong, "places" to places)
                mNavController.navigate(
                    R.id.action_chooseAttractionFragment_to_googleMapsAttractionFragment,
                    bundle
                )
            } else {
                mBinding.lottie.visibility = View.GONE
                activity?.supportFragmentManager?.let {
                    NoAttractionFoundDialog.newInstance(mBinding.autoCompleteTextview.text.toString())
                        .show(
                            it, " NO_ATTRACTION_DIALOG"
                        )
                }
            }
        })
        mPlacesViewModel.placesRepoError.observe(viewLifecycleOwner, {
            showToast(it)
        })

    }

    private fun setOnClickListener() {

        mBinding.getHotelBtn.setOnClickListener {
            whatToDo("accomodations", R.string.accommodations_request_error)
        }

        mBinding.getCinemasBtn.setOnClickListener {
            whatToDo("cinemas", R.string.cinemas_request_error)
        }

        mBinding.getTransportBtn.setOnClickListener {
            whatToDo("transport", R.string.transports_request_error)
        }

        mBinding.getBanksBtn.setOnClickListener {
            whatToDo("banks", R.string.banks_request_error)
        }

        mBinding.getFoodBtn.setOnClickListener {
            whatToDo("foods", R.string.food_request_error)
        }

        mBinding.getMuseumsBtn.setOnClickListener {
            whatToDo("museums", R.string.museum_request_error)
        }

        mBinding.getReligionBtn.setOnClickListener {
            whatToDo("religion", R.string.religion_request_error)
        }

        mBinding.getCultureBtn.setOnClickListener {
            whatToDo("cultural", R.string.natural_request_error)
        }

        mBinding.getNatureBtn.setOnClickListener {
            whatToDo("natural", R.string.natural_request_error)
        }
    }

    private fun showToast(error: Int) {
        Toast.makeText(
            activity?.applicationContext,
            activity?.applicationContext?.resources?.getString(error),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun whatToDo(kind: String, error: Int) {
        mBinding.mainLayout.visibility = View.GONE
        mBinding.lottie.visibility = View.VISIBLE
        mBinding.autoCompleteTextview.visibility = View.GONE
        mPlacesViewModel.getWhatToDo(mLong, mLat, kind, error)
    }
}