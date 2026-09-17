package com.evya.myweatherapp.ui

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.evya.myweatherapp.Constants
import com.evya.myweatherapp.network.NewWeatherApi
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit

/** Refreshes the home-screen widget from the last known location, at most every 6 hours. */
class WeatherWidgetWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface Deps {
        fun newWeatherApi(): NewWeatherApi
    }

    override suspend fun doWork(): Result {
        val prefs = applicationContext.getSharedPreferences("weather_widget", Context.MODE_PRIVATE)
        val lat = prefs.getString("lat", null) ?: return Result.success()
        val lon = prefs.getString("lon", null) ?: return Result.success()
        val units = prefs.getString("units", Constants.METRIC) ?: Constants.METRIC
        return try {
            val api = EntryPointAccessors.fromApplication(applicationContext, Deps::class.java).newWeatherApi()
            val response = api.getWeatherByLocation(lat, lon, units, Constants.WEATHER_REPOSITORY_API)
            val weather = response.body()
            if (response.isSuccessful && weather != null) {
                weather.cityName = prefs.getString("city", "").orEmpty()
                weather.callTime = System.currentTimeMillis()
                weather.responseUnits = units
                WeatherWidget.publish(applicationContext, weather)
                Result.success()
            } else {
                Result.retry()
            }
        } catch (_: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val UNIQUE = "weather_widget_refresh"

        fun ensureScheduled(context: Context) {
            val request = PeriodicWorkRequestBuilder<WeatherWidgetWorker>(6, TimeUnit.HOURS)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE, ExistingPeriodicWorkPolicy.KEEP, request
            )
        }
    }
}
