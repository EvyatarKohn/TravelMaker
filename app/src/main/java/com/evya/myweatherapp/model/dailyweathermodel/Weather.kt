package com.evya.myweatherapp.model.dailyweathermodel


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Weather(
    @SerializedName("description")
    @Expose
    val description: String,

    @SerializedName("icon")
    @Expose
    val icon: String,

    @SerializedName("id")
    @Expose
    val id: Int,

    @SerializedName("main")
    @Expose
    val main: String
)