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

    fun getWeather(cityName: String, units: String) = viewModelScope.launch {
        mWeatherLiveData.value = weatherRequest(R.string.city_not_found_error) { repository.getWeather(cityName.substringBefore("(").trim(), units) }
    }

    fun getWeatherByLocation(lat: String, long: String, units: String) = viewModelScope.launch {
        mWeatherLiveData.value = weatherRequest(R.string.city_not_found_error) { repository.getCityByLocation(lat, long, units) }
    }

    fun getDailyWeather(cityName: String, countryCode: String, units: String) = viewModelScope.launch {
        mDailyWeatherLiveData.value = weatherRequest(R.string.daily_weather_error) { repository.getDailyWeather(cityName.substringBefore("(").trim() + "," + countryCode, units) }
    }

    fun getDailyWeatherByLocation(lat: String, long: String, units: String) = viewModelScope.launch {
        mDailyWeatherLiveData.value = weatherRequest(R.string.daily_weather_error) { repository.getDailyWeatherByLocation(lat, long, units) }
    }

    fun getCitiesAround(lat: String, long: String, units: String) = viewModelScope.launch {
        mCitiesAroundLiveData.value = weatherRequest(R.string.cities_around_error) { repository.getCitiesAround(lat, long, units) }
    }

    fun getAirPollution(lat: String, long: String) = viewModelScope.launch {
        mPollutionLiveData.value = weatherRequest(R.string.pollution_error) { repository.getAirPollution(lat, long) }
    }
}
