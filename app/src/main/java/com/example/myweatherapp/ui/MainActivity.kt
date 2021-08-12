package com.example.myweatherapp.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.myweatherapp.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MainListener {
    private val mViewModel: MainViewModel by viewModels()
    private var mUnits = "metric"
    private lateinit var mCityName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, HomeFragment.newInstance(mUnits, this), "HOME_FRAGMENT")
            .addToBackStack(null)
            .commit()

        degree_units.setOnClickListener {
            if (degree_units.text.equals("Celsius")) {
                degree_units.text = "Fahrenheit"
                mUnits = "imperial"
            } else if (degree_units.text.equals("Fahrenheit")) {
                degree_units.text = "Celsius"
                mUnits = "metric"
            }
            refreshBtn()
        }

        refresh_btn.setOnClickListener {
            refreshBtn()
        }

    }

    private fun refreshBtn() {
        if (supportFragmentManager.findFragmentByTag("HOME_FRAGMENT")?.isVisible!!) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, HomeFragment.newInstance(mUnits, this), "HOME_FRAGMENT")
                .addToBackStack(null)
                .commit()
        } else if (supportFragmentManager.findFragmentByTag("CITY_FRAGMENT")?.isVisible!!) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, CityFragment.newInstance(mCityName, mUnits), "CITY_FRAGMENT")
                .addToBackStack(null)
                .commit()
        }
    }

    override fun replaceFragment(cityName: String) {
        mCityName = cityName
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, CityFragment.newInstance(cityName, mUnits), "CITY_FRAGMENT")
            .addToBackStack(null)
            .commit()
    }
}