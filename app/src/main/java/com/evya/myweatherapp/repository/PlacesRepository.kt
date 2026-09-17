package com.evya.myweatherapp.repository

import com.evya.myweatherapp.Constants.PLACES_REPOSITORY_API
import com.evya.myweatherapp.MainData.attractionRadius
import com.evya.myweatherapp.network.TripApi
import javax.inject.Inject

class PlacesRepository @Inject constructor(private val tripApi: TripApi) {

    suspend fun getWhatToDo(lat: String, long: String, kind: String) =
        tripApi.getWhatToDo(
            attractionRadius,
            long,
            lat,
            kind,
//            "1000",// Default is 500
            PLACES_REPOSITORY_API
        )

}