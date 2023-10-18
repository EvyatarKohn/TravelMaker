package com.evya.myweatherapp.model.dailyweathermodel


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DailyWeather(
    @SerializedName("data")
    @Expose
    val data: List<Data>,

    @SerializedName("lat")
    @Expose
    val lat: Double,

    @SerializedName("lon")
    @Expose
    val lon: Double,

    @SerializedName("timezone")
    @Expose
    val timezone: String,

    @SerializedName("timezone_offset")
    @Expose
    val timezoneOffset: Int
)