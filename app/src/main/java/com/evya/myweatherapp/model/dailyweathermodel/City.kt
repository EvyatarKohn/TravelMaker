package com.evya.myweatherapp.model.dailyweathermodel


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose


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
    val name: String,

    @SerializedName("population")
    @Expose
    val population: Int,

    @SerializedName("sunrise")
    @Expose
    val sunrise: Int,

    @SerializedName("sunset")
    @Expose
    val sunset: Int,

    @SerializedName("timezone")
    @Expose
    val timezone: Int
)