package com.evya.myweatherapp.model.placesmodel


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Feature(
    @SerializedName("geometry")
    @Expose
    val geometry: Geometry,

    @SerializedName("id")
    @Expose
    val id: String,

    @SerializedName("properties")
    @Expose
    val properties: Properties,

    @SerializedName("type")
    @Expose
    val type: String
)