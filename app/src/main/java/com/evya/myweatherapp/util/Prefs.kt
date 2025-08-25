package com.evya.myweatherapp.util

import android.content.Context
import android.content.SharedPreferences
import com.evya.myweatherapp.R

class Prefs(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun setRemoveAd(value: Int) {
        editor.putInt("RemoveAd", value)
        editor.apply()
    }

    fun getRemoveAd(): Int {
        return sharedPreferences.getInt("RemoveAd", 0)
    }
}