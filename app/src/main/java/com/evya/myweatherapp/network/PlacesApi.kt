package com.evya.myweatherapp.network

import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApi {
    @GET("places/areas")
    suspend fun getWhatToDo(
        @Query("name") cityName: String,
        @Query("apikey") apiKey: String
    )
}