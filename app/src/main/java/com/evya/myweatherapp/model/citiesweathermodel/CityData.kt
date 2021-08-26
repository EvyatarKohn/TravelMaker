package com.evya.myweatherapp.model.citiesweathermodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CityData(
    @SerializedName("clouds")
    @Expose
    val clouds: Clouds,

    @SerializedName("coord")
    @Expose
    val coord: Coord,

    @SerializedName("dt")
    @Expose
    val dt: Int,

    @SerializedName("id")
    @Expose
    val id: Int,

    @SerializedName("main")
    @Expose
    val main: Main,

    @SerializedName("name")
    @Expose
    val name: String,

    @SerializedName("rain")
    @Expose
    val rain: Any,

    @SerializedName("snow")
    @Expose
    val snow: Any,

    @SerializedName("visibility")
    @Expose
    val visibility: Int,

    @SerializedName("weather")
    @Expose
    val weather: List<Weather>,

    @SerializedName("wind")
    @Expose
    val wind: Wind
)
