package com.evya.myweatherapp.repository

import com.evya.myweatherapp.network.PlacesApi
import javax.inject.Inject

class PlacesRepository @Inject constructor(private val placesApi: PlacesApi) {

    suspend fun getWhatToDo(long: String, lat: String, kind: String) =
        placesApi.getWhatToDo(
            "10000",
            long,
            lat,
            kind,
            "50",
            "5ae2e3f221c38a28845f05b64bacfee3164cdfa9999c5226a8a0508f"
        )

}