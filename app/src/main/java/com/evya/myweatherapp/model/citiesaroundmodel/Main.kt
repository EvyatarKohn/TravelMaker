package com.evya.myweatherapp.model.citiesaroundmodel


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Main(
    @SerializedName("feels_like")
    @Expose
    val feelsLike: Double,

    @SerializedName("humidity")
    @Expose
    val humidity: Int,

    @SerializedName("pressure")
    @Expose
    val pressure: Int,

    @SerializedName("temp")
    @Expose
    val temp: Double,

    @SerializedName("temp_max")
    @Expose
    val tempMax: Double,

    @SerializedName("temp_min")
    @Expose
    val tempMin: Double
)