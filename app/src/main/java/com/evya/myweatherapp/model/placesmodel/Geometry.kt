package com.evya.myweatherapp.model.placesmodel


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Geometry(
    @SerializedName("coordinates")
    @Expose
    val coordinates: List<Double>,

    @SerializedName("type")
    @Expose
    val type: String
)