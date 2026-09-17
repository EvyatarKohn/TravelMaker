package com.evya.myweatherapp.model.dailyweathermodel


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Pressure(
    @SerializedName("afternoon")
    @Expose
    val afternoon: Double
)