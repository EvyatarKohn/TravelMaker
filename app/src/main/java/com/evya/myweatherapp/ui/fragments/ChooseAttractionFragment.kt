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
import com.evya.myweatherapp.ui.MainActivity
import com.evya.myweatherapp.ui.dialogs.NoAttractionFoundDialog
import com.evya.myweatherapp.util.UtilsFunctions.Companion.showToast
import com.evya.myweatherapp.viewmodels.PlacesViewModel
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
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
    private var mInterstitialAd: InterstitialAd? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = ChooseAttractionFragmentLayoutBinding.bind(view)
        loadInterstitialAd()

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
            handleInterstitialAd(
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

    private fun loadInterstitialAd() {
        val adRequest = (activity as MainActivity).adRequest

        context?.let {
            InterstitialAd.load(it, "ca-app-pub-9058418744370338/1048685069", adRequest, object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        val params = bundleOf(
                            PARAMS_FAILED_TO_LOAD_AD.paramsName to adError.message
                        )
                        FireBaseEvents.sendFireBaseCustomEvents(
                            ON_INTERSTITIAL_AD_FAILED_TO_LOAD.eventName,
                            params
                        )
                        mInterstitialAd = null
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        val params = bundleOf()
                        FireBaseEvents.sendFireBaseCustomEvents(
                            ON_INTERSTITIAL_AD_LOADED.eventName,
                            params
                        )
                        mInterstitialAd = interstitialAd
                    }
                })
        }
    }

    private fun setOnClickListener() {
        mBinding.apply {
            getHotelBtn.setOnClickListener {
                mName = resources.getString(R.string.get_hotels_btn)
                handleInterstitialAd("accomodations", R.string.accommodations_request_error)
            }

            getNightlifeBtn.setOnClickListener {
                mName = resources.getString(R.string.get_night_life_btn)
                handleInterstitialAd("adult", R.string.adults_request_error)
            }

            getTransportBtn.setOnClickListener {
                mName = resources.getString(R.string.get_transport_btn)
                handleInterstitialAd("transport", R.string.transports_request_error)
            }

            getBanksBtn.setOnClickListener {
                mName = resources.getString(R.string.get_banks_btn)
                handleInterstitialAd("banks", R.string.banks_request_error)
            }

            getFoodBtn.setOnClickListener {
                mName = resources.getString(R.string.get_food_btn)
                handleInterstitialAd("foods", R.string.food_request_error)
            }

            getMuseumsBtn.setOnClickListener {
                mName = resources.getString(R.string.get_museums_btn)
                handleInterstitialAd("museums", R.string.museum_request_error)
            }

            getHistoryBtn.setOnClickListener {
                mName = resources.getString(R.string.get_historic_btn)
                handleInterstitialAd("historic", R.string.historic_request_error)
            }

            getCultureBtn.setOnClickListener {
                mName = resources.getString(R.string.get_cultural_btn)
                handleInterstitialAd("cultural", R.string.natural_request_error)
            }

            getNatureBtn.setOnClickListener {
                mName = resources.getString(R.string.get_natural_btn)
                handleInterstitialAd("natural", R.string.natural_request_error)
            }
        }
    }

    private fun handleInterstitialAd(kind: String, error: Int) {
        if (mInterstitialAd != null) {
            activity?.let { mInterstitialAd?.show(it) }
        } else {
            whatToDo(kind, error)
        }
        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                val params = bundleOf()
                FireBaseEvents.sendFireBaseCustomEvents(
                    CLICK_ON_INTERSTITIAL_AD.eventName,
                    params
                )
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                val params = bundleOf()
                FireBaseEvents.sendFireBaseCustomEvents(
                    ON_INTERSTITIAL_AD_DISMISSED_FULL_SCREEN_CONTENT.eventName,
                    params
                )
                mInterstitialAd = null
                whatToDo(kind, error)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when ad fails to show.
                val params = bundleOf(
                    PARAMS_FAILED_TO_LOAD_AD.paramsName to adError.message
                )
                FireBaseEvents.sendFireBaseCustomEvents(
                    ON_INTERSTITIAL_AD_FAILED_TO_SHOW_FULL_SCREEN_CONTENT.eventName,
                    params
                )
                mInterstitialAd = null
                whatToDo(kind, error)
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                val params = bundleOf()
                FireBaseEvents.sendFireBaseCustomEvents(
                    ON_INTERSTITIAL_AD_IMPRESSION.eventName,
                    params
                )
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                val params = bundleOf()
                FireBaseEvents.sendFireBaseCustomEvents(
                    ON_INTERSTITIAL_AD_SHOWED_FULL_SCREEN_CONTENT.eventName,
                    params
                )
            }
        }
    }

    fun whatToDo(kind: String, error: Int) {
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