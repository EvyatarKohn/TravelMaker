package com.example.myweatherapp.repository

import com.example.myweatherapp.network.MainApi
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val mainApi: MainApi) {

/*    suspend fun getCitiesList() = mainApi.getCitiesList(
        "34,29.5,34.9,36.5,200",
        "b2baa3886c9bf495a13704d6ce1523a5"
    )*/

    suspend fun getWeather(cityName: String, units: String) =
        mainApi.getWeather(cityName, "b2baa3886c9bf495a13704d6ce1523a5", units)

}