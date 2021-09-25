package com.evya.myweatherapp.model.dailyweathermodel


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class Sys(
    @SerializedName("pod")
    @Expose
    val pod: String
)