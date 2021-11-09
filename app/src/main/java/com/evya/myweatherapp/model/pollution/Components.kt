package com.evya.myweatherapp.model.pollution


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Components(
    @SerializedName("co")
    @Expose
    val co: Double,

    @SerializedName("nh3")
    @Expose
    val nh3: Double,

    @SerializedName("no")
    @Expose
    val no: Double,

    @SerializedName("no2")
    @Expose
    val no2: Double,

    @SerializedName("o3")
    @Expose
    val o3: Double,

    @SerializedName("pm10")
    @Expose
    val pm10: Double,

    @SerializedName("pm2_5")
    @Expose
    val pm25: Double,

    @SerializedName("so2")
    @Expose
    val so2: Double
)