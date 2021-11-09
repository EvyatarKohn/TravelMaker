package com.evya.myweatherapp.model.pollution


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Coord(
    @SerializedName("lat")
    @Expose
    val latz: Double,

    @SerializedName("lon")
    @Expose
    val lon: Double
)