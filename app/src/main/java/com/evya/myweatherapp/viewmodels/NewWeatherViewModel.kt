package com.evya.myweatherapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evya.myweatherapp.R
import com.evya.myweatherapp.model.geocode.GeoCode
import com.evya.myweatherapp.model.weathermodel.Weather
import com.evya.myweatherapp.repository.GeoCodeRepository
import com.evya.myweatherapp.repository.NewWeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewWeatherViewModel@Inject constructor(private val newWeatherRepository: NewWeatherRepository, private val geoCodeRepo: GeoCodeRepository) : ViewModel() {
    private var mWeatherLiveData = MutableLiveData<Pair<Weather?, Int?>>()
    val weatherData: LiveData<Pair<Weather?, Int?>>
        get() = mWeatherLiveData

    private var mCityNameLiveData = MutableLiveData<Pair<GeoCode?, Int?>>()
    val cityNameData: LiveData<Pair<GeoCode?, Int?>>
        get() = mCityNameLiveData

    fun getWeatherByLocation(lat: String, long: String, units: String) = viewModelScope.launch {
        newWeatherRepository.getWeatherByLocation(lat, long, units).let { response ->
            if (response.isSuccessful) {
                mWeatherLiveData.postValue(Pair(response.body(), null))
            } else {
                mWeatherLiveData.postValue(Pair(null, R.string.city_not_found_error))
            }
        }
    }

    fun getCityNameByLocation(lat: String, long: String) = viewModelScope.launch {
        geoCodeRepo.getCityNameByLocation(lat, long).let { response ->
            if (response.isSuccessful) {
                mCityNameLiveData.postValue(Pair(response.body(), null))
            } else {
                mCityNameLiveData.postValue(Pair(null, R.string.city_not_found_error))
            }
        }
    }

}