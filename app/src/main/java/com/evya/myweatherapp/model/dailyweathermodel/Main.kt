package com.evya.myweatherapp.model.dailyweathermodel


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Main(
    @SerializedName("grnd_level")
    @Expose
    val grndLevel: Double,

    @SerializedName("humidity")
    @Expose
    val humidity: Int,

    @SerializedName("pressure")
    @Expose
    val pressure: Double,

    @SerializedName("sea_level")
    @Expose
    val seaLevel: Double,

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