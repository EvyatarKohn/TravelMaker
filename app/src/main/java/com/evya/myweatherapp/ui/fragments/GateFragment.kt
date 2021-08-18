package com.evya.myweatherapp.ui.fragments

import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.evya.myweatherapp.R
import com.evya.myweatherapp.ui.MainListener
import com.evya.myweatherapp.ui.dialogs.InfoDialog

class GateFragment: Fragment() {
    private lateinit var mMainListener: MainListener
    private lateinit var mLottie: LottieAnimationView
    private lateinit var mContinueBtn: Button
    private lateinit var mInfoBtn: ImageButton
    companion object {
        fun newInstance(mainListener: MainListener) = GateFragment().apply {
            mMainListener = mainListener
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val v = inflater.inflate(R.layout.gate_fragment_layout, container, false)
        mContinueBtn = v.findViewById(R.id.start_flow)
        mContinueBtn.setOnClickListener {
            if (isLocationEnabled()) {
                mMainListener.startFlow()
            } else {
                Toast.makeText(activity, resources.getString(R.string.permissions_toast), Toast.LENGTH_LONG).show()
            }
        }
        mInfoBtn = v.findViewById(R.id.info_btn)
        mInfoBtn.setOnClickListener {
            activity?.supportFragmentManager?.let { it1 -> InfoDialog().show(it1, "INFO_DIALOG") }
        }

        return v
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = activity?.getSystemService((Context.LOCATION_SERVICE)) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

}