package com.evya.myweatherapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.evya.myweatherapp.Constants
import com.evya.myweatherapp.MainData
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.ChooseAttractionFragmentLayoutBinding
import com.evya.myweatherapp.ui.dialogs.NoAttractionFoundDialog
import com.evya.myweatherapp.util.FireBaseEvents
import com.evya.myweatherapp.util.UtilsFunctions
import com.evya.myweatherapp.viewmodels.PlacesViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ChooseAttractionFragment : Fragment(R.layout.choose_attraction_fragment_layout) {

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
            Constants.replacedList()
        )
        mBinding.autoCompleteTextview.threshold = 1
        mBinding.autoCompleteTextview.setAdapter(adapter)
        mBinding.autoCompleteTextview.setOnItemClickListener { _, _, _, _ ->
            whatToDo(
                mBinding.autoCompleteTextview.text.toString().replace(" ", "_"),
                R.string.general_error
            )
            FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.SearchAttractions)
        }

        mPlacesViewModel.placesRepo.observe(viewLifecycleOwner, {
            if (it.first != null) {
                val latLong: ArrayList<LatLng> = ArrayList()
                it.first!!.features.forEach { feature ->
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
                            .show(
                                fragmentManager, " NO_ATTRACTION_DIALOG"
                            )
                    }
                }
            } else if (it.second.second) {
                it.second.first?.let { it1 ->
                    UtilsFunctions.showToast(
                        it1,
                        activity?.applicationContext
                    )
                }

            }
        })
    }

    private fun setOnClickListener() {

        mBinding.getHotelBtn.setOnClickListener {
            mName = resources.getString(R.string.get_hotels_btn)
            whatToDo("accomodations", R.string.accommodations_request_error)
            FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.HotelAttractions)
        }

        mBinding.getNightlifeBtn.setOnClickListener {
            mName = resources.getString(R.string.get_night_life_btn)
            whatToDo("adult", R.string.adults_request_error)
            FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.NightLifeAttractions)
        }

        mBinding.getTransportBtn.setOnClickListener {
            mName = resources.getString(R.string.get_transport_btn)
            whatToDo("transport", R.string.transports_request_error)
            FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.TransportAttraction)
        }

        mBinding.getBanksBtn.setOnClickListener {
            mName = resources.getString(R.string.get_banks_btn)
            whatToDo("banks", R.string.banks_request_error)
            FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.BanksAttraction)
        }

        mBinding.getFoodBtn.setOnClickListener {
            mName = resources.getString(R.string.get_food_btn)
            whatToDo("foods", R.string.food_request_error)
            FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.FoodAttraction)
        }

        mBinding.getMuseumsBtn.setOnClickListener {
            mName = resources.getString(R.string.get_museums_btn)
            whatToDo("museums", R.string.museum_request_error)
            FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.MuseumsAttraction)
        }

        mBinding.getHistoryBtn.setOnClickListener {
            mName = resources.getString(R.string.get_historic_btn)
            whatToDo("historic", R.string.historic_request_error)
            FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.HistoricAttraction)
        }

        mBinding.getCultureBtn.setOnClickListener {
            mName = resources.getString(R.string.get_cultural_btn)
            whatToDo("cultural", R.string.natural_request_error)
            FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.CulturalAttraction)
        }

        mBinding.getNatureBtn.setOnClickListener {
            mName = resources.getString(R.string.get_natural_btn)
            whatToDo("natural", R.string.natural_request_error)
            FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.NatureAttraction)
        }
    }

    private fun whatToDo(kind: String, error: Int) {
        mBinding.mainLayout.visibility = View.GONE
        mBinding.lottie.visibility = View.VISIBLE
        mBinding.autoCompleteTextview.visibility = View.GONE
        mPlacesViewModel.getWhatToDo(MainData.lat, MainData.long, kind, error)
    }
}