package com.evya.myweatherapp.model.pollution


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Main(
    @SerializedName("aqi")
    @Expose
    val aqi: Int
)