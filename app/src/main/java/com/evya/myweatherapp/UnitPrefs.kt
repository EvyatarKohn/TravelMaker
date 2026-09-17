package com.evya.myweatherapp

import android.content.Context
import com.evya.myweatherapp.Constants.METRIC

object UnitPrefs {
    private const val PREFS = "settings"
    private const val KEY_UNITS = "degrees_units"

    fun load(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_UNITS, METRIC) ?: METRIC

    fun save(context: Context, units: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_UNITS, units).apply()
    }
}
