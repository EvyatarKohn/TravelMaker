package com.evya.myweatherapp.model.dailyweathermodel


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class Rain(
    @SerializedName("3h")
    @Expose
    val h: Double
)