package com.evya.myweatherapp.network

import com.evya.myweatherapp.model.dailyweathermodel.DailyWeather
import com.evya.myweatherapp.model.timemachinemodel.TimeMachineWeather
import com.evya.myweatherapp.model.weathermodel.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewWeatherApi {
    @GET("onecall")
    suspend fun getWeatherByLocation(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") units: String,
        @Query("appid") appid: String
        ): Response<Weather>

    @GET("onecall/timemachine")
    suspend fun getWeatherForTimeMachine(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("dt") time: Int,
        @Query("units") units: String,
        @Query("appid") appid: String
    ): Response<TimeMachineWeather>

    @GET("onecall/day_summary")
    suspend fun getWeatherForSpecificDay(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("date") date: String,
        @Query("units") units: String,
        @Query("appid") appid: String
    ): Response<DailyWeather>

}