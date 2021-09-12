package com.evya.myweatherapp.model.citiesaroundmodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CitiesAroundData(
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

    @SerializedName("sys")
    @Expose
    val sys: Sys,

    @SerializedName("weather")
    @Expose
    val weather: List<Weather>,

    @SerializedName("wind")
    @Expose
    val wind: Wind
)
