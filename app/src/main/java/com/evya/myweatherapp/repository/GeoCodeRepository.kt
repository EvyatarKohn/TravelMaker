package com.evya.myweatherapp.repository

import com.evya.myweatherapp.Constants.WEATHER_REPOSITORY_API
import com.evya.myweatherapp.network.GeocodeApi
import javax.inject.Inject

class GeoCodeRepository@Inject constructor(private val geocodeApi: GeocodeApi) {

    suspend fun getCityNameByLocation(lat: String, long: String) =
        geocodeApi.getCityNameByLocation(
            lat,
            long,
            WEATHER_REPOSITORY_API,
        )
}