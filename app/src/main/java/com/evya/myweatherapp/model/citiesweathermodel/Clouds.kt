package com.evya.myweatherapp.model.citiesweathermodel


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Clouds(
    @SerializedName("today")
    @Expose
    val today: Int
)