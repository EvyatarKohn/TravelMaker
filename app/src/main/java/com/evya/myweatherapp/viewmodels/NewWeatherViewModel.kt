package com.evya.myweatherapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evya.myweatherapp.R
import com.evya.myweatherapp.model.weathermodel.Weather
import com.evya.myweatherapp.model.dailyweathermodel.DailyWeather
import com.evya.myweatherapp.model.geocode.GeoCode
import com.evya.myweatherapp.repository.NewWeatherRepository
import com.evya.myweatherapp.repository.GeoCodeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewWeatherViewModel @Inject constructor(private val newWeatherRepository: NewWeatherRepository, private val geoCodeRepo: GeoCodeRepository) : ViewModel() {
    private val mWeatherLiveData = MutableLiveData<Pair<Weather?, Int?>>()
    val weatherData: LiveData<Pair<Weather?, Int?>> = mWeatherLiveData

    private val mDailyWeatherLiveData = MutableLiveData<Pair<DailyWeather?, Int?>>()
    val dailyWeatherData: LiveData<Pair<DailyWeather?, Int?>> = mDailyWeatherLiveData

    private val mCityNameLiveData = MutableLiveData<Pair<GeoCode?, Int?>>()
    val cityNameData: LiveData<Pair<GeoCode?, Int?>> = mCityNameLiveData

    private var weatherJob: Job? = null
    private var dailyWeatherJob: Job? = null
    private var cityNameJob: Job? = null

    fun getWeatherByLocation(lat: String, long: String, units: String) {
        weatherJob?.cancel()
        weatherJob = viewModelScope.launch {
            val result = weatherRequest(R.string.city_not_found_error) {
                newWeatherRepository.getWeatherByLocation(lat, long, units)
            }
            result.first?.responseUnits = units
            mWeatherLiveData.value = result
        }
    }

    fun getWeatherForSpecificDay(lat: String, long: String, date: String, units: String) {
        dailyWeatherJob?.cancel()
        dailyWeatherJob = viewModelScope.launch {
            mDailyWeatherLiveData.value = weatherRequest(R.string.daily_error) {
                newWeatherRepository.getWeatherForSpecificDay(lat, long, date, units)
            }
        }
    }

    fun getCityNameByLocation(lat: String, long: String) {
        cityNameJob?.cancel()
        cityNameJob = viewModelScope.launch {
            mCityNameLiveData.value = weatherRequest(R.string.city_not_found_error) {
                geoCodeRepo.getCityNameByLocation(lat, long)
            }
        }
    }
}
