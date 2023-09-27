package com.evya.myweatherapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.evya.myweatherapp.databinding.AlertFragmentBinding
import com.evya.myweatherapp.model.weathermodel.Alerts
import com.evya.myweatherapp.ui.MainActivity
import com.evya.myweatherapp.ui.adapters.AlertsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AlertsFragment : Fragment() {

    private lateinit var mNavController: NavController
    private lateinit var mBinding: AlertFragmentBinding
    private var alertsAdapter: AlertsAdapter? = null
    private var alertsList: List<Alerts>? = null

    companion object {
        fun newInstance(alerts: List<Alerts>) =
            AlertsFragment().apply {
                alertsList = alerts
            }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)
        mBinding = AlertFragmentBinding.bind(view)

        alertsAdapter =
            (activity as MainActivity).supportFragmentManager.let { fm ->
                alertsList?.let { alertsList ->
                    AlertsAdapter(fm, alertsList)
                }
            }
        val layoutManager =
            LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.HORIZONTAL, false)
        mBinding.alertsRecyclerView.layoutManager = layoutManager
        mBinding.alertsRecyclerView.adapter = alertsAdapter


           /* val params = bundleOf(
                PARAMS_CITY_NAME.paramsName to address
            )
            FireBaseEvents.sendFireBaseCustomEvents(SHOW_WEATHER.eventName, params)
            mNavController.navigate(R.id.action_googleMapsFragment_to_cityFragment)
            (activity as MainActivity).changeNavBarIndex(R.id.cityFragment, R.id.weather)
        }*/
    }
}