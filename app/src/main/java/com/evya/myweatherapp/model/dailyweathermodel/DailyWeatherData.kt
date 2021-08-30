package com.evya.myweatherapp.model.dailyweathermodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DailyWeatherData(
    @SerializedName("clouds")
    @Expose
    val clouds: Clouds,

    @SerializedName("dt")
    @Expose
    val dt: Int,

    @SerializedName("dt_txt")
    @Expose
    val dtTxt: String,

    @SerializedName("main")
    @Expose
    val main: Main,

    @SerializedName("rain")
    @Expose
    val rain: Rain,

    @SerializedName("snow")
    @Expose
    val snow: Snow,

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
