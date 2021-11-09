package com.evya.myweatherapp.repository

import com.evya.myweatherapp.network.WeatherApi
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val weatherApi: WeatherApi) {

    suspend fun getCityByLocation(lat: String, long: String, units: String) =
        weatherApi.getCityByLocation(
            lat,
            long,
            "b2baa3886c9bf495a13704d6ce1523a5",
            units
        )

    suspend fun getCitiesAround(lat: String, long: String, units: String) =
        weatherApi.getCitiesAround(
            lat,
            long,
            "8", // Number of cities
            "b2baa3886c9bf495a13704d6ce1523a5",
            units
        )


    suspend fun getWeather(cityName: String, units: String) =
        weatherApi.getWeather(
            cityName,
            "b2baa3886c9bf495a13704d6ce1523a5",
            units
        )

    suspend fun getDailyWeather(cityName: String, units: String) =
        weatherApi.getDailyWeather(
            cityName,
            "df128db76f752a3e23d8a0735cde83e6",
            units
        )

    suspend fun getDailyWeatherByLocation(lat: String, long: String, units: String) =
        weatherApi.getDailyWeatherByLocation(
            lat,
            long,
            "df128db76f752a3e23d8a0735cde83e6",
            units
        )

    suspend fun getAirPollution(lat: String, long: String) =
        weatherApi.getAirPollution(
            lat,
            long,
            "df128db76f752a3e23d8a0735cde83e6"
        )
}