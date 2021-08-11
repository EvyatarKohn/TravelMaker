package com.example.myweatherapp.network

import com.example.myweatherapp.model.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MainApi {

   /* @GET("box/city")
    suspend fun getCitiesList(@Query("bbox") bbox: String,
                              @Query("appid") appid: String): Response<List<Weather>>*/

    @GET("weather")
    suspend fun getWeather(@Query("q") cityName: String,
                           @Query("appid") appid: String,
                           @Query("units") units: String): Response<Weather>
}