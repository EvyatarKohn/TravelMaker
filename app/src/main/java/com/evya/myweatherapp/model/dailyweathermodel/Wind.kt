package com.evya.myweatherapp.model.dailyweathermodel


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Wind(
    @SerializedName("deg")
    @Expose
    val deg: Double,

    @SerializedName("gust")
    @Expose
    val gust: Double,

    @SerializedName("speed")
    @Expose
    val speed: Double
)