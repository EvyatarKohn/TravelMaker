package com.evya.myweatherapp.network

import com.evya.myweatherapp.model.weathermodel.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewWeatherApi {
    @GET("onecall")
    suspend fun getWeatherByLocation(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Response<Weather>
}