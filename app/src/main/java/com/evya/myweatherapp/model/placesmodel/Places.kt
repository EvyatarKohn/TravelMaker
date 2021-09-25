package com.evya.myweatherapp.model.placesmodel


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Places(
    @SerializedName("features")
    @Expose
    val features: List<Feature>,

    @SerializedName("type")
    @Expose
    val type: String
)