package com.evya.myweatherapp.model.citiesweathermodel


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CitiesWeather(
    @SerializedName("calctime")
    @Expose
    val calctime: Double,

    @SerializedName("cnt")
    @Expose
    val cnt: Int,

    @SerializedName("cod")
    @Expose
    val cod: Int,

    @SerializedName("list")
    @Expose
    val list: List<CityData>
)