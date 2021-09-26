package com.evya.myweatherapp.model.placesmodel


import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Geometry(
    @SerializedName("coordinates")
    @Expose
    val coordinates: List<Double>,

    @SerializedName("type")
    @Expose
    val type: String
) : Parcelable