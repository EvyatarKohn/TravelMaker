package com.evya.myweatherapp.viewmodels

import com.evya.myweatherapp.R
import retrofit2.Response
import java.io.IOException

/** Transport failures are UI errors; coroutine cancellation must still propagate. */
internal suspend fun <T> weatherRequest(httpError: Int, request: suspend () -> Response<T>): Pair<T?, Int?> =
    try {
        val response = request()
        val body = response.body()
        if (response.isSuccessful && body != null) body to null else null to httpError
    } catch (_: IOException) {
        null to R.string.weather_connection_error
    }
