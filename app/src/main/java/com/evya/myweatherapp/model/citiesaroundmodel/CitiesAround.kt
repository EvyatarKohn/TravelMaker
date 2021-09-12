package com.evya.myweatherapp.model.citiesaroundmodel


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CitiesAround(
    @SerializedName("cod")
    @Expose
    val cod: String,

    @SerializedName("count")
    @Expose
    val count: Int,

    @SerializedName("list")
    @Expose
    val list: List<CitiesAroundData>,

    @SerializedName("message")
    @Expose
    val message: String
)