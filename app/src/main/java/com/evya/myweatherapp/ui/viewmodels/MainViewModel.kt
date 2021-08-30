package com.evya.myweatherapp.ui.viewmodels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.evya.myweatherapp.R
import com.evya.myweatherapp.model.citiesweathermodel.CitiesWeather
import com.evya.myweatherapp.model.dailyweathermodel.DailyWeather
import com.evya.myweatherapp.model.weathermodel.Weather
import com.evya.myweatherapp.repository.WeatherRepository
import kotlinx.coroutines.launch


class MainViewModel @ViewModelInject constructor(
    private val repository: WeatherRepository,
    application: Application
) : AndroidViewModel(application) {

    private var mLiveData = MutableLiveData<Weather>()
    val weatherRepo: LiveData<Weather>
        get() = mLiveData

    private var mCitiesLiveData = MutableLiveData<CitiesWeather>()
    val citiesWeatherRepo: LiveData<CitiesWeather>
        get() = mCitiesLiveData

    private var mDailyWeatherLiveData = MutableLiveData<DailyWeather>()
    val dailyWeatherRepo: LiveData<DailyWeather>
        get() = mDailyWeatherLiveData

    private var mBackCLicked = MutableLiveData<Boolean>()
    val backClicked: LiveData<Boolean>
        get() = mBackCLicked

    fun getCityByLocation(lat: String, long: String, units: String) = viewModelScope.launch {
        repository.getCityByLocation(lat, long, units).let { response ->
            if (response.isSuccessful) {
                mLiveData.postValue(response.body())
            } else {
                Log.d("TAG", "getCityByLocation Error Response: ${response.message()}")
            }
        }
    }


    fun getCitiesList(units: String, boundaryBox: String) = viewModelScope.launch {
        repository.getCitiesList(units, boundaryBox).let { response ->
            if (response.isSuccessful) {
                mCitiesLiveData.postValue(response.body())
            } else {
                Toast.makeText(
                    getApplication<Application>().applicationContext,
                    getApplication<Application>().applicationContext.resources.getString(R.string.area_to_big_toast),
                    Toast.LENGTH_LONG
                ).show()
                mBackCLicked.postValue(true)
            }
        }
    }

    fun getWeather(cityName: String, units: String) = viewModelScope.launch {
        var cityNameTemp = cityName
        if (cityName.contains("(")) {
            cityNameTemp = cityName.substring(0, cityName.length - 5)
        }
        repository.getWeather(cityNameTemp, units).let { response ->
            if (response.isSuccessful) {
                mLiveData.postValue(response.body())
            } else {
                Toast.makeText(
                    getApplication<Application>().applicationContext,
                    getApplication<Application>().applicationContext.resources.getString(R.string.city_not_found),
                    Toast.LENGTH_LONG
                ).show()
                mBackCLicked.postValue(true)
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
                    Toast.makeText(
                        getApplication<Application>().applicationContext,
                        getApplication<Application>().applicationContext.resources.getString(R.string.daily_weather_error),
                        Toast.LENGTH_LONG
                    ).show()
                    mBackCLicked.postValue(true)
                }
            }
        }

    fun getDailyWeatherByLocation(lat: String, long: String, units: String) =
        viewModelScope.launch {
            repository.getDailyWeatherByLocation(lat, long, units).let { response ->
                if (response.isSuccessful) {
                    mDailyWeatherLiveData.postValue(response.body())
                } else {
                    Toast.makeText(
                        getApplication<Application>().applicationContext,
                        getApplication<Application>().applicationContext.resources.getString(R.string.daily_weather_error),
                        Toast.LENGTH_LONG
                    ).show()
                    mBackCLicked.postValue(true)
                }
            }
        }
}