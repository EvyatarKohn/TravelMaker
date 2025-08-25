package com.evya.myweatherapp.model.weathermodel


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FeelsLike(
    @SerializedName("day")
    @Expose
    val day: Double,

    @SerializedName("eve")
    @Expose
    val eve: Double,

    @SerializedName("morn")
    @Expose
    val morn: Double,

    @SerializedName("night")
    @Expose
    val night: Double
)