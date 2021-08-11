package com.example.myweatherapp.ui

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.myweatherapp.model.Weather
import com.example.myweatherapp.repository.WeatherRepository
import kotlinx.coroutines.launch


class MainViewModel @ViewModelInject constructor(private val repository: WeatherRepository) :
    ViewModel() {

    private var mLiveData = MutableLiveData<Weather>()
    private lateinit var mCitiesList: List<Weather>
    val weatherRepo: LiveData<Weather>
        get() = mLiveData


   /* fun getCitiesList() = viewModelScope.launch {
        repository.getCitiesList().let { response ->
            if (response.isSuccessful) {
                mCitiesList = response.body()!!
            } else {
                Log.d("TAG", "getCities Error Response: ${response.message()}")
            }
        }
    }*/


    fun getWeather(cityName: String, units: String) = viewModelScope.launch {
        repository.getWeather(cityName, units).let { response ->
            if (response.isSuccessful) {
                mLiveData.postValue(response.body())
            } else {
                Log.d("TAG", "getWeather Error Response: ${response.message()}")
            }
        }
    }
}