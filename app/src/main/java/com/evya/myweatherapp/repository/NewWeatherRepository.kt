package com.evya.myweatherapp.repository

import com.evya.myweatherapp.Constants
import com.evya.myweatherapp.network.NewWeatherApi
import javax.inject.Inject

class NewWeatherRepository @Inject constructor(private val newWeatherApi: NewWeatherApi) {

    suspend fun getWeatherByLocation(lat: String, long: String, units: String) =
        newWeatherApi.getWeatherByLocation(
            lat,
            long,
            Constants.WEATHER_REPOSITORY_API,
            units
        )
}