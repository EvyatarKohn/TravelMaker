package com.evya.myweatherapp.repository

import com.evya.myweatherapp.Constants
import com.evya.myweatherapp.network.NewWeatherApi
import javax.inject.Inject

class NewWeatherRepository @Inject constructor(private val newWeatherApi: NewWeatherApi) {

    suspend fun getWeatherByLocation(lat: String, long: String, units: String) =
        newWeatherApi.getWeatherByLocation(
            lat = lat,
            lon = long,
            units = units,
            appid = Constants.WEATHER_REPOSITORY_API,
        )

    suspend fun getWeatherForSpecificDay(lat: String, long: String, date: String, units: String) =
        newWeatherApi.getWeatherForSpecificDay(
            lat = lat,
            lon = long,
            date = date,
            units = units,
            appid = Constants.WEATHER_REPOSITORY_API,
        )
}