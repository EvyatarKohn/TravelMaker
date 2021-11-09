package com.evya.myweatherapp.network

import com.evya.myweatherapp.model.citiesaroundmodel.CitiesAround
import com.evya.myweatherapp.model.dailyweathermodel.DailyWeather
import com.evya.myweatherapp.model.pollution.Pollution
import com.evya.myweatherapp.model.weathermodel.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("weather")
    suspend fun getCityByLocation(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Response<Weather>

    @GET("find")
    suspend fun getCitiesAround(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("cnt") cnt: String,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Response<CitiesAround>

    @GET("weather")
    suspend fun getWeather(
        @Query("q") cityName: String,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Response<Weather>


    @GET("forecast")
    suspend fun getDailyWeather(
        @Query("q") cityName: String,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Response<DailyWeather>

    @GET("forecast")
    suspend fun getDailyWeatherByLocation(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Response<DailyWeather>

    @GET("air_pollution")
    suspend fun getAirPollution(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String,
    ): Response<Pollution>
}