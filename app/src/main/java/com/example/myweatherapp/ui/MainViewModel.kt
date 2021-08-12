package com.example.myweatherapp.ui

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.myweatherapp.citiesmodel.CitiesWeather
import com.example.myweatherapp.model.Weather
import com.example.myweatherapp.repository.WeatherRepository
import kotlinx.coroutines.launch


class MainViewModel @ViewModelInject constructor(private val repository: WeatherRepository) :
    ViewModel() {

    private var mLiveData = MutableLiveData<Weather>()
    val weatherRepo: LiveData<Weather>
        get() = mLiveData

    private var mCitiesLiveData = MutableLiveData<CitiesWeather>()
    val citiesWeatherRepo: LiveData<CitiesWeather>
        get() = mCitiesLiveData


    fun getCityByLocation(lat: String, long: String, units: String) = viewModelScope.launch {
        repository.getCityByLocation(lat, long, units).let { response ->
            if (response.isSuccessful) {
                mLiveData.postValue(response.body())
            } else {
                Log.d("TAG", "getCityByLocation Error Response: ${response.message()}")
            }
        }
    }


    fun getCitiesList(units: String) = viewModelScope.launch {
        repository. getCitiesList(units).let { response ->
            if (response.isSuccessful) {
                mCitiesLiveData.postValue(response.body())
            } else {
                Log.d("TAG", "getCities Error Response: ${response.message()}")
            }
        }
    }

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