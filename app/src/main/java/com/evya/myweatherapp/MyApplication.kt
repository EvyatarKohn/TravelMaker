package com.evya.myweatherapp

import android.app.Application
import com.evya.myweatherapp.ui.WeatherWidgetWorker
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MainData.degreesUnits = UnitPrefs.load(this)
        WeatherWidgetWorker.ensureScheduled(this)
    }
}
