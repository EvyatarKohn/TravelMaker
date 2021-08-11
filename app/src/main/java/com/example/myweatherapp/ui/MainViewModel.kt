package com.example.myweatherapp.ui

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.myweatherapp.model.Weather
import com.example.myweatherapp.repository.WeatherRepository
import kotlinx.coroutines.launch


class MainViewModel @ViewModelInject constructor(private val repository: WeatherRepository) : ViewModel() {

    private var mLiveData = MutableLiveData<Weather>()
    val weatherRepo: LiveData<Weather>
        get() = mLiveData


    fun getWeather(cityName: String) = viewModelScope.launch {
        repository.getWeather(cityName).let {
                response ->
            if (response.isSuccessful) {
                mLiveData.postValue(response.body())
            } else {
                Log.d("TAG", "getWeather Error Response: ${response.message()}")
            }
        }
    }
}