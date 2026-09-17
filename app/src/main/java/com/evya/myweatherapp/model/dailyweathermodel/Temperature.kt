package com.evya.myweatherapp.model.dailyweathermodel


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Temperature(
    @SerializedName("afternoon")
    @Expose
    val afternoon: Double,

    @SerializedName("evening")
    @Expose
    val evening: Double,

    @SerializedName("max")
    @Expose
    val max: Double,

    @SerializedName("min")
    @Expose
    val min: Double,

    @SerializedName("morning")
    @Expose
    val morning: Double,

    @SerializedName("night")
    @Expose
    val night: Double
)