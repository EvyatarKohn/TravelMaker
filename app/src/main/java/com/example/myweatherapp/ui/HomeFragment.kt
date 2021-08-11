package com.example.myweatherapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.fragment.app.viewModels
import com.example.myweatherapp.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment_layout.*

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val mViewModel: MainViewModel by viewModels()
    private lateinit var mCityName: String

    companion object {
        fun newInstance(cityName: String) = HomeFragment().apply {
            mCityName = cityName
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.home_fragment_layout, container, false)
        mViewModel.getWeather(mCityName)
        mViewModel.weatherRepo.observe(viewLifecycleOwner, Observer {
            city_name.text = it.name
        })


        /* city_name = */

        return v
    }

}