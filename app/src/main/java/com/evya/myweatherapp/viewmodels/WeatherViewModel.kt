package com.evya.myweatherapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evya.myweatherapp.R
import com.evya.myweatherapp.model.citiesaroundmodel.CitiesAround
import com.evya.myweatherapp.model.dailyweathermodel.DailyWeather
import com.evya.myweatherapp.model.pollution.Pollution
import com.evya.myweatherapp.model.weathermodel.Weather
import com.evya.myweatherapp.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private var mWeatherLiveData = MutableLiveData<Pair<Weather?, Pair<Int?, Boolean>>>()
    val weatherRepo: LiveData<Pair<Weather?, Pair<Int?, Boolean>>>
        get() = mWeatherLiveData

    private var mDailyWeatherLiveData = MutableLiveData<Pair<DailyWeather?, Pair<Int?, Boolean>>>()
    val dailyWeatherRepo: LiveData<Pair<DailyWeather?, Pair<Int?, Boolean>>>
        get() = mDailyWeatherLiveData

    private var mCitiesAroundLiveData = MutableLiveData<Pair<CitiesAround?, Pair<Int?, Boolean>>>()
    val citiesAroundRepo: LiveData<Pair<CitiesAround?, Pair<Int?, Boolean>>>
        get() = mCitiesAroundLiveData

    private var mPollutionLiveData = MutableLiveData<Pair<Pollution?, Pair<Int?, Boolean>>>()
    val pollutionRepo: LiveData<Pair<Pollution?, Pair<Int?, Boolean>>>
        get() = mPollutionLiveData


    fun getWeather(cityName: String, units: String) = viewModelScope.launch {
        var cityNameTemp = cityName
        if (cityName.contains("(")) {
            cityNameTemp = cityName.substring(0, cityName.length - 5)
        }
        repository.getWeather(cityNameTemp, units).let { response ->
            if (response.isSuccessful) {
                mWeatherLiveData.postValue(Pair(response.body(), Pair(null, false)))
            } else {
                mWeatherLiveData.postValue(Pair(null, Pair(R.string.city_not_found_error, true)))
            }
        }
    }

    fun getWeatherByLocation(lat: String, long: String, units: String) = viewModelScope.launch {
        repository.getCityByLocation(lat, long, units).let { response ->
            if (response.isSuccessful) {
                if (response.body()?.name.isNullOrEmpty() || response.body()?.sys?.country.isNullOrEmpty()) {
                    mWeatherLiveData.postValue(
                        Pair(
                            null,
                            Pair(R.string.city_not_found_error, true)
                        )
                    )
                }
                mWeatherLiveData.postValue(Pair(response.body(), Pair(null, false)))
            } else {
                mWeatherLiveData.postValue(Pair(null, Pair(R.string.city_not_found_error, true)))
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
                    mDailyWeatherLiveData.postValue(Pair(response.body(), Pair(null, false)))
                } else {
                    mDailyWeatherLiveData.postValue(
                        Pair(
                            null,
                            Pair(R.string.daily_weather_error, true)
                        )
                    )
                }
            }
        }

    fun getDailyWeatherByLocation(lat: String, long: String, units: String) =
        viewModelScope.launch {
            repository.getDailyWeatherByLocation(lat, long, units).let { response ->
                if (response.isSuccessful) {
                    mDailyWeatherLiveData.postValue(Pair(response.body(), Pair(null, false)))
                } else {
                    mDailyWeatherLiveData.postValue(
                        Pair(
                            null,
                            Pair(R.string.daily_weather_error, true)
                        )
                    )
                }
            }
        }

    fun getCitiesAround(lat: String, long: String, units: String) = viewModelScope.launch {
        repository.getCitiesAround(lat, long, units).let { response ->
            if (response.isSuccessful) {
                mCitiesAroundLiveData.postValue(Pair(response.body(), Pair(null, false)))
            } else {
                mCitiesAroundLiveData.postValue(
                    Pair(
                        null,
                        Pair(R.string.cities_around_error, true)
                    )
                )
            }
        }
    }

    fun getAirPollution(lat: String, long: String) = viewModelScope.launch {
        repository.getAirPollution(lat, long).let { response ->
            if (response.isSuccessful) {
                mPollutionLiveData.postValue(Pair(response.body(), Pair(null, false)))
            } else {
                mPollutionLiveData.postValue(Pair(null, Pair(R.string.pollution_error, true)))
            }
        }
    }
}