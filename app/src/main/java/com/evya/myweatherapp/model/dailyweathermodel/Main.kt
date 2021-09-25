package com.evya.myweatherapp.model.dailyweathermodel


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class Main(
    @SerializedName("feels_like")
    @Expose
    val feelsLike: Double,

    @SerializedName("grnd_level")
    @Expose
    val grndLevel: Int,

    @SerializedName("humidity")
    @Expose
    val humidity: Int,

    @SerializedName("pressure")
    @Expose
    val pressure: Int,

    @SerializedName("sea_level")
    @Expose
    val seaLevel: Int,

    @SerializedName("temp")
    @Expose
    val temp: Double,

    @SerializedName("temp_kf")
    @Expose
    val tempKf: Double,

    @SerializedName("temp_max")
    @Expose
    val tempMax: Double,

    @SerializedName("temp_min")
    @Expose
    val tempMin: Double
)