package com.evya.myweatherapp.model.dailyweathermodel


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DailyWeather(
    @SerializedName("cloud_cover")
    @Expose
    val cloudCover: CloudCover,

    @SerializedName("date")
    @Expose
    val date: String,

    @SerializedName("humidity")
    @Expose
    val humidity: Humidity,

    @SerializedName("lat")
    @Expose
    val lat: Double,

    @SerializedName("lon")
    @Expose
    val lon: Double,

    @SerializedName("precipitation")
    @Expose
    val precipitation: Precipitation,

    @SerializedName("pressure")
    @Expose
    val pressure: Pressure,

    @SerializedName("temperature")
    @Expose
    val temperature: Temperature,

    @SerializedName("tz")
    @Expose
    val tz: String,

    @SerializedName("units")
    @Expose
    val units: String,

    @SerializedName("wind")
    @Expose
    val wind: Wind
)