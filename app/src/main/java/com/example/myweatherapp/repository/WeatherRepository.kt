package com.example.myweatherapp.repository

import com.example.myweatherapp.network.MainApi
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val mainApi: MainApi) {
    suspend fun getWeather(cityName: String) = mainApi.getWeather(cityName, "b2baa3886c9bf495a13704d6ce1523a5")

}