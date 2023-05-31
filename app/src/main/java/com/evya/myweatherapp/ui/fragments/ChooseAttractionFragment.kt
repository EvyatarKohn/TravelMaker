package com.evya.myweatherapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.evya.myweatherapp.Constants.manipulatedList
import com.evya.myweatherapp.MainData.attractionRadius
import com.evya.myweatherapp.MainData.lat
import com.evya.myweatherapp.MainData.long
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.ChooseAttractionFragmentLayoutBinding
import com.evya.myweatherapp.firebaseanalytics.FireBaseEvents
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsNamesStrings.*
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsParamsStrings.*
import com.evya.myweatherapp.ui.dialogs.NoAttractionFoundDialog
import com.evya.myweatherapp.util.UtilsFunctions.Companion.showToast
import com.evya.myweatherapp.viewmodels.PlacesViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ChooseAttractionFragment : Fragment(R.layout.choose_attraction_fragment_layout) {

    companion object {
        private val TAG = ChooseAttractionFragment::class.toString()
    }

    private val mPlacesViewModel: PlacesViewModel by viewModels()
    private lateinit var mNavController: NavController
    private lateinit var mBinding: ChooseAttractionFragmentLayoutBinding
    private lateinit var mName: String


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = ChooseAttractionFragmentLayoutBinding.bind(view)

        mNavController = Navigation.findNavController(view)
        setOnClickListener()

        val adapter = ArrayAdapter(
            activity?.applicationContext!!,
            android.R.layout.select_dialog_item,
            manipulatedList()
        )
        mBinding.autoCompleteTextview.threshold = 1
        mBinding.autoCompleteTextview.setAdapter(adapter)
        mBinding.autoCompleteTextview.setOnItemClickListener { _, _, _, _ ->
            whatToDo(
                mBinding.autoCompleteTextview.text.toString().replace(" ", "_"),
                R.string.general_error
            )
            val params = bundleOf(
                PARAMS_WHAT_TO_DO.paramsName to mBinding.autoCompleteTextview.text
            )
            FireBaseEvents.sendFireBaseCustomEvents(SEARCH_ATTRACTIONS.eventName, params)
        }


        val radiusAdapter = ArrayAdapter(
            activity?.applicationContext!!,
            android.R.layout.simple_spinner_item,
            arrayListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        )
        radiusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding.radiusSpinner.adapter = radiusAdapter
        mBinding.radiusSpinner.prompt = "sasdas"

        mBinding.radiusSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    attractionRadius =
                        (parent?.getItemAtPosition(position) as Int * 1000).toString()
                }

            }

        mPlacesViewModel.placesRepo.observe(viewLifecycleOwner) {
            if (it.first != null) {
                val latLong: ArrayList<LatLng> = ArrayList()
                it.first?.features?.forEach { feature ->
                    latLong.add(
                        LatLng(
                            feature.geometry.coordinates[0],
                            feature.geometry.coordinates[1]
                        )
                    )
                }
                if (latLong.size > 0) {
                    val bundle = bundleOf("places" to it.first)
                    mNavController.navigate(
                        R.id.action_chooseAttractionFragment_to_googleMapsAttractionFragment,
                        bundle
                    )
                } else {
                    mBinding.lottie.visibility = View.GONE
                    activity?.supportFragmentManager?.let { fragmentManager ->

                        if (mBinding.autoCompleteTextview.text.toString().isNotEmpty()) {
                            mName = mBinding.autoCompleteTextview.text.toString()
                        }
                        NoAttractionFoundDialog.newInstance(mName)
                            .show(fragmentManager, " NO_ATTRACTION_DIALOG")
                    }
                }
            } else {
                it.second?.let { it1 ->
                    showToast(context?.resources?.getString(it1), activity?.applicationContext)
                }
            }
        }
    }

    private fun setOnClickListener() {
        mBinding.apply {
            getHotelBtn.setOnClickListener {
                mName = resources.getString(R.string.get_hotels_btn)
                whatToDo("accomodations", R.string.accommodations_request_error)
            }

            getNightlifeBtn.setOnClickListener {
                mName = resources.getString(R.string.get_night_life_btn)
                whatToDo("adult", R.string.adults_request_error)
            }

            getTransportBtn.setOnClickListener {
                mName = resources.getString(R.string.get_transport_btn)
                whatToDo("transport", R.string.transports_request_error)
            }

            getBanksBtn.setOnClickListener {
                mName = resources.getString(R.string.get_banks_btn)
                whatToDo("banks", R.string.banks_request_error)
            }

            getFoodBtn.setOnClickListener {
                mName = resources.getString(R.string.get_food_btn)
                whatToDo("foods", R.string.food_request_error)
            }

            getMuseumsBtn.setOnClickListener {
                mName = resources.getString(R.string.get_museums_btn)
                whatToDo("museums", R.string.museum_request_error)
            }

            getHistoryBtn.setOnClickListener {
                mName = resources.getString(R.string.get_historic_btn)
                whatToDo("historic", R.string.historic_request_error)
            }

            getCultureBtn.setOnClickListener {
                mName = resources.getString(R.string.get_cultural_btn)
                whatToDo("cultural", R.string.natural_request_error)
            }

            getNatureBtn.setOnClickListener {
                mName = resources.getString(R.string.get_natural_btn)
                whatToDo("natural", R.string.natural_request_error)
            }
        }
    }

    private fun whatToDo(kind: String, error: Int) {
        val params = bundleOf(
            PARAMS_WHAT_TO_DO.paramsName to mName
        )
        FireBaseEvents.sendFireBaseCustomEvents(WHAT_TO_DO.eventName, params)
        mBinding.mainLayout.visibility = View.GONE
        mBinding.lottie.visibility = View.VISIBLE
        mBinding.autoCompleteTextview.visibility = View.GONE
        mPlacesViewModel.getWhatToDo(lat, long, kind, error)
    }


}