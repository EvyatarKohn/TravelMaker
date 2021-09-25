package com.evya.myweatherapp.model.dailyweathermodel


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

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