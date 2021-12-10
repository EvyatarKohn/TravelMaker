package com.evya.myweatherapp.model.weathermodel


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "favorites")
data class Weather(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    @SerializedName("base")
    @Expose
    val base: String,

    @SerializedName("clouds")
    @Expose
    val clouds: Clouds,

    @SerializedName("cod")
    @Expose
    val cod: Int,

    @SerializedName("coord")
    @Expose
    val coord: Coord,

    @SerializedName("dt")
    @Expose
    val dt: Int,

    @SerializedName("id")
    @Expose
    val ids: Int,

    @SerializedName("main")
    @Expose
    val main: Main,

    @SerializedName("name")
    @Expose
    val name: String,

    @SerializedName("sys")
    @Expose
    val sys: Sys,

    @SerializedName("timezone")
    @Expose
    val timezone: Int,

    @SerializedName("visibility")
    @Expose
    val visibility: Int,

    @SerializedName("weather")
    @Expose
    val weather: List<WeatherX>,

    @SerializedName("wind")
    @Expose
    val wind: Wind
)