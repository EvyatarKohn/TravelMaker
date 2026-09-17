package com.evya.myweatherapp.model.dailyweathermodel


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Max(
    @SerializedName("direction")
    @Expose
    val direction: Double,

    @SerializedName("speed")
    @Expose
    val speed: Double
)