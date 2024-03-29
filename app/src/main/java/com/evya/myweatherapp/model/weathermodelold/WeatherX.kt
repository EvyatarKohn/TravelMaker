package com.evya.myweatherapp.model.weathermodelold


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WeatherX(
    @SerializedName("description")
    @Expose
    val description: String,

    @SerializedName("icon")
    @Expose
    val icon: String,

    @SerializedName("id")
    @Expose
    val id: Int,

    @SerializedName("main")
    @Expose
    val main: String
)