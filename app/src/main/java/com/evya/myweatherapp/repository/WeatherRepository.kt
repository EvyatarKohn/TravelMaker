package com.evya.myweatherapp.repository

import com.evya.myweatherapp.network.MainApi
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val mainApi: MainApi) {

    suspend fun getCityByLocation(lat: String, long: String, units: String) =
        mainApi.getCityByLocation(
            lat,
            long,
            "b2baa3886c9bf495a13704d6ce1523a5",
            units
        )

    suspend fun getCitiesAround(lat: String, long: String, units: String) = mainApi.getCitiesAround(
        lat,
        long,
        "8",
        "b2baa3886c9bf495a13704d6ce1523a5",
        units
    )


    suspend fun getWeather(cityName: String, units: String) =
        mainApi.getWeather(
            cityName,
            "b2baa3886c9bf495a13704d6ce1523a5",
            units
        )

    suspend fun getDailyWeather(cityName: String, units: String) =
        mainApi.getDailyWeather(
            cityName, "df128db76f752a3e23d8a0735cde83e6", units
        )

    suspend fun getDailyWeatherByLocation(lat: String, long: String, units: String) =
        mainApi.getDailyWeatherByLocation(lat, long, "df128db76f752a3e23d8a0735cde83e6", units)
}