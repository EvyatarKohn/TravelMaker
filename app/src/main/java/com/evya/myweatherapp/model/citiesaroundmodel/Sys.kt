package com.evya.myweatherapp.model.citiesaroundmodel


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Sys(
    @SerializedName("country")
    @Expose
    val country: String
)