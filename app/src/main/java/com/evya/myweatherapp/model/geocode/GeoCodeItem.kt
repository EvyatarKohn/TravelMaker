package com.evya.myweatherapp.model.geocode


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GeoCodeItem(
    @SerializedName("country")
    @Expose
    val country: String,

    @SerializedName("lat")
    @Expose
    val lat: Double,

    @SerializedName("local_names")
    @Expose
    val localNames: LocalNames,

    @SerializedName("lon")
    @Expose
    val lon: Double,

    @SerializedName("name")
    @Expose
    val name: String
)