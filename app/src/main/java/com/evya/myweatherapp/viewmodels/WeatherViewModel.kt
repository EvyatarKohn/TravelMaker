package com.evya.myweatherapp.viewmodels

import androidx.lifecycle.*
import com.evya.myweatherapp.R
import com.evya.myweatherapp.model.citiesaroundmodel.CitiesAround
import com.evya.myweatherapp.model.dailyweathermodel.DailyWeather
import com.evya.myweatherapp.model.weathermodel.Weather
import com.evya.myweatherapp.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
) : ViewModel() {

    private var mWeatherLiveData = MutableLiveData<Weather>()
    val weatherRepo: LiveData<Weather>
        get() = mWeatherLiveData

    private var mWeatherError = MutableLiveData<Pair<Int, Boolean>>()
    val repoWeatherError: LiveData<Pair<Int, Boolean>>
        get() = mWeatherError

    private var mDailyWeatherLiveData = MutableLiveData<DailyWeather>()
    val dailyWeatherRepo: LiveData<DailyWeather>
        get() = mDailyWeatherLiveData

    private var mDailyWeatherError = MutableLiveData<Pair<Int, Boolean>>()
    val repoDailyWeatherError: LiveData<Pair<Int, Boolean>>
        get() = mDailyWeatherError

    private var mCitiesAroundLiveData = MutableLiveData<CitiesAround>()
    val citiesAroundRepo: LiveData<CitiesAround>
        get() = mCitiesAroundLiveData

    private var mCitiesAroundError = MutableLiveData<Int>()
    val repoCitiesAroundError: LiveData<Int>
        get() = mCitiesAroundError


    fun getWeather(cityName: String, units: String) = viewModelScope.launch {
        var cityNameTemp = cityName
        if (cityName.contains("(")) {
            cityNameTemp = cityName.substring(0, cityName.length - 5)
        }
        repository.getWeather(cityNameTemp, units).let { response ->
            if (response.isSuccessful) {
                mWeatherLiveData.postValue(response.body())
            } else {
                mWeatherError.postValue(Pair(R.string.city_not_found_error, true))
            }
        }
    }

    fun getWeatherByLocation(lat: String, long: String, units: String) = viewModelScope.launch {
        repository.getCityByLocation(lat, long, units).let { response ->
            if (response.isSuccessful) {
                if (response.body()?.name.isNullOrEmpty() || response.body()?.sys?.country.isNullOrEmpty()) {
                    mWeatherError.postValue(Pair(R.string.city_not_found_error, true))
                }
                mWeatherLiveData.postValue(response.body())
            } else {
                mWeatherError.postValue(Pair(R.string.city_not_found_error, true))
            }
        }
    }

    fun getDailyWeather(cityName: String, countryCode: String, units: String) =
        viewModelScope.launch {
            var cityNameTemp = cityName
            if (cityName.contains("(")) {
                cityNameTemp = cityName.substring(0, cityName.length - 5)
            }
            cityNameTemp += ",$countryCode"
            repository.getDailyWeather(cityNameTemp, units).let { response ->
                if (response.isSuccessful) {
                    mDailyWeatherLiveData.postValue(response.body())
                } else {
                    mDailyWeatherError.postValue(Pair(R.string.daily_weather_error, true))
                }
            }
        }

    fun getDailyWeatherByLocation(lat: String, long: String, units: String) =
        viewModelScope.launch {
            repository.getDailyWeatherByLocation(lat, long, units).let { response ->
                if (response.isSuccessful) {
                    mDailyWeatherLiveData.postValue(response.body())
                } else {
                    mDailyWeatherError.postValue(Pair(R.string.daily_weather_error, true))
                }
            }
        }

    fun getCitiesAround(lat: String, long: String, units: String) = viewModelScope.launch {
        repository.getCitiesAround(lat, long, units).let { response ->
            if (response.isSuccessful) {
                mCitiesAroundLiveData.postValue(response.body())
            } else {
                mCitiesAroundError.postValue(R.string.cities_around_error)
            }
        }
    }
}