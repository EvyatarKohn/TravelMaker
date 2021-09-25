package com.evya.myweatherapp.model.placesmodel


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Properties(
    @SerializedName("dist")
    @Expose
    val dist: Double,

    @SerializedName("kinds")
    @Expose
    val kinds: String,

    @SerializedName("name")
    @Expose
    val name: String,

    @SerializedName("osm")
    @Expose
    val osm: String,

    @SerializedName("rate")
    @Expose
    val rate: Int,

    @SerializedName("wikidata")
    @Expose
    val wikidata: String,

    @SerializedName("xid")
    @Expose
    val xid: String
)