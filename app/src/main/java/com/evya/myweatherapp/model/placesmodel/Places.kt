package com.evya.myweatherapp.model.placesmodel


import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Places(
    @SerializedName("features")
    @Expose
    val features: List<Feature>,

    @SerializedName("type")
    @Expose
    val type: String
) : Parcelable