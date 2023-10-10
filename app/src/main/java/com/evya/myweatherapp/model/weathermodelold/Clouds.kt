package com.evya.myweatherapp.model.weathermodelold


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Clouds(
    @SerializedName("all")
    @Expose
    val all: Int
)