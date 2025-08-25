package com.evya.myweatherapp.model.dailyweathermodelold


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class Clouds(
    @SerializedName("all")
    @Expose
    val all: Int
)