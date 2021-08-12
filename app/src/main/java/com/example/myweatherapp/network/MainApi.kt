package com.example.myweatherapp.network

import com.example.myweatherapp.citiesmodel.CitiesWeather
import com.example.myweatherapp.model.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MainApi {

    @GET("weather")
    suspend fun getCityByLocation(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Response<Weather>

    @GET("box/city")
    suspend fun getCitiesList(
        @Query("bbox") bbox: String,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Response<CitiesWeather>

    @GET("weather")
    suspend fun getWeather(
        @Query("q") cityName: String,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Response<Weather>
}