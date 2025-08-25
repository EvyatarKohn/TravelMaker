package com.evya.myweatherapp.repository

import com.evya.myweatherapp.Constants.WEATHER_REPOSITORY_API
import com.evya.myweatherapp.Constants.WEATHER_REPOSITORY_API2
import com.evya.myweatherapp.network.WeatherApi
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val weatherApi: WeatherApi) {

    suspend fun getCityByLocation(lat: String, long: String, units: String) =
        weatherApi.getCityByLocation(
            lat,
            long,
            WEATHER_REPOSITORY_API,
            units
        )

    suspend fun getCitiesAround(lat: String, long: String, units: String) =
        weatherApi.getCitiesAround(
            lat,
            long,
            "8", // Number of cities
            WEATHER_REPOSITORY_API,
            units
        )


    suspend fun getWeather(cityName: String, units: String) =
        weatherApi.getWeather(
            cityName,
            WEATHER_REPOSITORY_API,
            units
        )

    suspend fun getDailyWeather(cityName: String, units: String) =
        weatherApi.getDailyWeather(
            cityName,
            WEATHER_REPOSITORY_API2,
            units
        )

    suspend fun getDailyWeatherByLocation(lat: String, long: String, units: String) =
        weatherApi.getDailyWeatherByLocation(
            lat,
            long,
            WEATHER_REPOSITORY_API2,
            units
        )

    suspend fun getAirPollution(lat: String, long: String) =
        weatherApi.getAirPollution(
            lat,
            long,
            WEATHER_REPOSITORY_API2
        )
}