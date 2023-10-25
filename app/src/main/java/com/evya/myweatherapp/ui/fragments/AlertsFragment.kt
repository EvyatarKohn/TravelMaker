package com.evya.myweatherapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.evya.myweatherapp.Constants.FROM_ALERTS
import com.evya.myweatherapp.Constants.LAT
import com.evya.myweatherapp.Constants.LONG
import com.evya.myweatherapp.MainData.lat
import com.evya.myweatherapp.MainData.long
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.AlertFragmentBinding
import com.evya.myweatherapp.firebaseanalytics.FireBaseEvents
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsNamesStrings.*
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsParamsStrings.*
import com.evya.myweatherapp.model.weathermodel.Alerts
import com.evya.myweatherapp.ui.adapters.AlertsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AlertsFragment : Fragment(R.layout.alert_fragment) {

    private lateinit var mBinding: AlertFragmentBinding
    private var alertsAdapter: AlertsAdapter? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = AlertFragmentBinding.bind(view)
        val alertsList = arguments?.get("alerts") as ArrayList<Alerts>
        val cityName = arguments?.getString("cityName")

        alertsAdapter = AlertsAdapter(requireContext(), alertsList)


        val layoutManager =
            LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.VERTICAL, false)
        mBinding.alertsRecyclerView.layoutManager = layoutManager
        mBinding.alertsRecyclerView.adapter = alertsAdapter
        mBinding.alertsRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))

        mBinding.closeBtn.setOnClickListener {
            val bundle = bundleOf(
                LAT to lat.toFloat(),
                LONG to long.toFloat(),
                FROM_ALERTS to true
            )

            findNavController().navigate(R.id.action_alertsFragment_to_cityFragment, bundle)
        }

        val params = bundleOf(
            PARAMS_CITY_NAME.paramsName to cityName,
            PARAMS_ALERT_DAY.paramsName to alertsList,
        )
        FireBaseEvents.sendFireBaseCustomEvents(SHOW_ALERT.eventName, params)

    }
}