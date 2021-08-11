package com.example.myweatherapp.network

import com.example.myweatherapp.model.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MainApi {

    @GET("weather")
    suspend fun getWeather(@Query("q") cityName: String, @Query("appid") appid: String): Response<Weather>
}