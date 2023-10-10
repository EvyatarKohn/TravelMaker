package com.evya.myweatherapp.network

import com.evya.myweatherapp.model.geocode.GeoCode
import com.evya.myweatherapp.model.weathermodel.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodeApi {
    @GET("reverse")
    suspend fun getCityNameByLocation(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String,
    ): Response<GeoCode>
}