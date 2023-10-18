package com.evya.myweatherapp.model.dailyweathermodelold

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Snow(
    @SerializedName("3h")
    @Expose
    val h: Double
)
