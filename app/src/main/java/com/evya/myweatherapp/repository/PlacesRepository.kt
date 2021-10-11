package com.evya.myweatherapp.repository

import com.evya.myweatherapp.network.TripApi
import javax.inject.Inject

class PlacesRepository @Inject constructor(private val tripApi: TripApi) {

    suspend fun getWhatToDo(lat: String, long: String, kind: String) =
        tripApi.getWhatToDo(
            "10000",
            long,
            lat,
            kind,
//            "1000",// Default is 500
            "5ae2e3f221c38a28845f05b64bacfee3164cdfa9999c5226a8a0508f"
        )

}