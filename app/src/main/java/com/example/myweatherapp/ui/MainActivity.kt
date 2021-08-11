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
class MainActivity : AppCompatActivity() {
    private val mViewModel: MainViewModel by viewModels()
    private var mUnits = "metric"
    private lateinit var mCityName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        mViewModel.getCitiesList()

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

        choose_city.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                mCityName = s.toString()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, HomeFragment.newInstance(s.toString(), mUnits))
                    .commit()
            }

        })


    }

    private fun refreshBtn() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, HomeFragment.newInstance(mCityName, mUnits))
            .commit()
    }
}