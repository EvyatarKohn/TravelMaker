package com.evya.myweatherapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evya.myweatherapp.R
import com.evya.myweatherapp.model.weathermodel.Weather
import com.evya.myweatherapp.model.dailyweathermodelold.DailyWeather
import com.evya.myweatherapp.model.citiesaroundmodel.CitiesAround
import com.evya.myweatherapp.model.pollution.Pollution
import com.evya.myweatherapp.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(private val repository: WeatherRepository) : ViewModel() {
    private val mWeatherLiveData = MutableLiveData<Pair<Weather?, Int?>>()
    val weatherRepo: LiveData<Pair<Weather?, Int?>> = mWeatherLiveData

    private val mDailyWeatherLiveData = MutableLiveData<Pair<DailyWeather?, Int?>>()
    val dailyWeatherRepo: LiveData<Pair<DailyWeather?, Int?>> = mDailyWeatherLiveData

    private val mCitiesAroundLiveData = MutableLiveData<Pair<CitiesAround?, Int?>>()
    val citiesAroundRepo: LiveData<Pair<CitiesAround?, Int?>> = mCitiesAroundLiveData

    private val mPollutionLiveData = MutableLiveData<Pair<Pollution?, Int?>>()
    val pollutionRepo: LiveData<Pair<Pollution?, Int?>> = mPollutionLiveData

    private var weatherJob: Job? = null
    private var dailyWeatherJob: Job? = null
    private var citiesAroundJob: Job? = null
    private var pollutionJob: Job? = null

    fun getWeather(cityName: String, units: String) {
        weatherJob?.cancel()
        weatherJob = viewModelScope.launch {
            val result = weatherRequest(R.string.city_not_found_error) {
                repository.getWeather(cityName.substringBefore("(").trim(), units)
            }
            result.first?.responseUnits = units
            mWeatherLiveData.value = result
        }
    }

    fun getWeatherByLocation(lat: String, long: String, units: String) {
        weatherJob?.cancel()
        weatherJob = viewModelScope.launch {
            val result = weatherRequest(R.string.city_not_found_error) {
                repository.getCityByLocation(lat, long, units)
            }
            result.first?.responseUnits = units
            mWeatherLiveData.value = result
        }
    }

    fun getDailyWeather(cityName: String, countryCode: String, units: String) {
        dailyWeatherJob?.cancel()
        dailyWeatherJob = viewModelScope.launch {
            mDailyWeatherLiveData.value = weatherRequest(R.string.daily_weather_error) {
                repository.getDailyWeather(cityName.substringBefore("(").trim() + "," + countryCode, units)
            }
        }
    }

    fun getDailyWeatherByLocation(lat: String, long: String, units: String) {
        dailyWeatherJob?.cancel()
        dailyWeatherJob = viewModelScope.launch {
            mDailyWeatherLiveData.value = weatherRequest(R.string.daily_weather_error) {
                repository.getDailyWeatherByLocation(lat, long, units)
            }
        }
    }

    fun getCitiesAround(lat: String, long: String, units: String) {
        citiesAroundJob?.cancel()
        citiesAroundJob = viewModelScope.launch {
            mCitiesAroundLiveData.value = weatherRequest(R.string.cities_around_error) {
                repository.getCitiesAround(lat, long, units)
            }
        }
    }

    fun getAirPollution(lat: String, long: String) {
        pollutionJob?.cancel()
        pollutionJob = viewModelScope.launch {
            mPollutionLiveData.value = weatherRequest(R.string.pollution_error) {
                repository.getAirPollution(lat, long)
            }
        }
    }
}
