package com.evya.myweatherapp.ui.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.evya.myweatherapp.R
import com.evya.myweatherapp.model.citiesaroundmodel.CitiesAround
import com.evya.myweatherapp.model.dailyweathermodel.DailyWeather
import com.evya.myweatherapp.model.weathermodel.Weather
import com.evya.myweatherapp.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: WeatherRepository,
    application: Application
) : AndroidViewModel(application) {

    private var mWeatherLiveData = MutableLiveData<Weather>()
    val weatherRepo: LiveData<Weather>
        get() = mWeatherLiveData

    private var mCitiesAroundLiveData = MutableLiveData<CitiesAround>()
    val citiesAroundRepo: LiveData<CitiesAround>
        get() = mCitiesAroundLiveData

    private var mDailyWeatherLiveData = MutableLiveData<DailyWeather>()
    val dailyWeatherRepo: LiveData<DailyWeather>
        get() = mDailyWeatherLiveData


    fun getCityByLocation(lat: String, long: String, units: String) = viewModelScope.launch {
        repository.getCityByLocation(lat, long, units).let { response ->
            if (response.isSuccessful) {
                if (response.body()?.name.isNullOrEmpty() || response.body()?.sys?.country.isNullOrEmpty()) {
                    showToast(R.string.city_not_found)
                }
                mWeatherLiveData.postValue(response.body())

            } else {
                showToast(R.string.city_not_found_error)
            }
        }
    }

    fun getCitiesAround(lat: String, long: String,  units: String) = viewModelScope.launch {
        repository.getCitiesAround(lat, long, units).let { response ->
            if (response.isSuccessful) {
                mCitiesAroundLiveData.postValue(response.body())
            } else {
                showToast(R.string.area_to_big_toast)
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
                mWeatherLiveData.postValue(response.body())
            } else {
                showToast(R.string.city_not_found_error)
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
                    showToast(R.string.daily_weather_error)
                }
            }
        }

    fun getDailyWeatherByLocation(lat: String, long: String, units: String) =
        viewModelScope.launch {
            repository.getDailyWeatherByLocation(lat, long, units).let { response ->
                if (response.isSuccessful) {
                    mDailyWeatherLiveData.postValue(response.body())
                } else {
                    showToast(R.string.daily_weather_error)
                }
            }
        }

    private fun showToast(RString: Int) {
        Toast.makeText(
            getApplication<Application>().applicationContext,
            getApplication<Application>().applicationContext.resources.getString(RString),
            Toast.LENGTH_LONG
        ).show()
    }
}