package com.evya.myweatherapp.model.citiesweathermodel


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Coord(
    @SerializedName("Lat")
    @Expose
    val lat: Double,

    @SerializedName("Lon")
    @Expose
    val lon: Double
)