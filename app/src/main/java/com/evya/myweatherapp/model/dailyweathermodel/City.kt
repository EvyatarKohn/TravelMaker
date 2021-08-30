package com.evya.myweatherapp.model.dailyweathermodel


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("coord")
    @Expose
    val coord: Coord,

    @SerializedName("country")
    @Expose
    val country: String,

    @SerializedName("id")
    @Expose
    val id: Int,

    @SerializedName("name")
    @Expose
    val name: String
)