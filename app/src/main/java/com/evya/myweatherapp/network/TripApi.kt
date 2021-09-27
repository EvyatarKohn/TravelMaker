package com.evya.myweatherapp.network

import com.evya.myweatherapp.model.placesmodel.Places
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TripApi {
    @GET("places/radius")
    suspend fun getWhatToDo(
        @Query("radius") radius: String,
        @Query("lon") long: String,
        @Query("lat") lat: String,
        @Query("kinds") kinds: String,
//        @Query("limit") limit: String,
        @Query("apikey") apiKey: String
    ): Response<Places>
}