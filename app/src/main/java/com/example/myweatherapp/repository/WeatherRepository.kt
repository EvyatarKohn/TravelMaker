package com.example.myweatherapp.repository

import com.example.myweatherapp.network.MainApi
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val mainApi: MainApi) {

    suspend fun getCityByLocation(lat: String, long: String, units: String) = mainApi.getCityByLocation(
        lat,
        long,
        "b2baa3886c9bf495a13704d6ce1523a5",
        units
    )

    suspend fun getCitiesList(units: String, boundaryBox: String) = mainApi.getCitiesList(
        boundaryBox,
        "b2baa3886c9bf495a13704d6ce1523a5",
        units
    )

    suspend fun getWeather(cityName: String, units: String) =
        mainApi.getWeather(cityName,
            "b2baa3886c9bf495a13704d6ce1523a5",
            units
        )
}