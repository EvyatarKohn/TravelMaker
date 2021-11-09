package com.evya.myweatherapp.model.pollution


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Pollution(
    @SerializedName("coord")
    @Expose
    val coord: Coord,

    @SerializedName("list")
    @Expose
    val list: List<PollutionData>
)