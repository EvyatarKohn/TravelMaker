package com.evya.myweatherapp.model.weathermodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Alerts(
    @SerializedName("description")
    @Expose
    val description: String,

    @SerializedName("end")
    @Expose
    val end: Int,

    @SerializedName("event")
    @Expose
    val event: String,

    @SerializedName("sender_name")
    @Expose
    val senderName: String,

    @SerializedName("start")
    @Expose
    val start: Int,

    @SerializedName("tags")
    @Expose
    val tags: List<Tags>
)