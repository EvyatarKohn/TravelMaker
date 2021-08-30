package com.evya.myweatherapp.model.dailyweathermodel


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DailyWeather(
    @SerializedName("city")
    @Expose
    val city: City,

    @SerializedName("cnt")
    @Expose
    val cnt: Int,

    @SerializedName("cod")
    @Expose
    val cod: String,

    @SerializedName("list")
    @Expose
    val list: List<DailyWeatherData>,

    @SerializedName("message")
    @Expose
    val message: Double
)