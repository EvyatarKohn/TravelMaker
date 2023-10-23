package com.evya.myweatherapp.model.dailyweathermodel


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Precipitation(
    @SerializedName("total")
    @Expose
    val total: Double
)