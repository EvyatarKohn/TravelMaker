package com.evya.myweatherapp.model.pollution

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PollutionData(
    @SerializedName("components")
    @Expose
    val components: Components,

    @SerializedName("dt")
    @Expose
    val dt: Int,

    @SerializedName("main")
    @Expose
    val main: Main
)