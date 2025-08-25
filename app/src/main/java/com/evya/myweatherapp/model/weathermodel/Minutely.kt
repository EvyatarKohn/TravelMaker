package com.evya.myweatherapp.model.weathermodel


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Minutely(
    @SerializedName("dt")
    @Expose
    val dt: Int,

    @SerializedName("precipitation")
    @Expose
    val precipitation: Double
)