package com.evya.myweatherapp.model.weathermodel


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Daily(
    @SerializedName("clouds")
    @Expose
    val clouds: Int,

    @SerializedName("dew_point")
    @Expose
    val dewPoint: Double,

    @SerializedName("dt")
    @Expose
    val dt: Int,

    @SerializedName("feels_like")
    @Expose
    val feelsLike: FeelsLike,

    @SerializedName("humidity")
    @Expose
    val humidity: Int,

    @SerializedName("moon_phase")
    @Expose
    val moonPhase: Double,

    @SerializedName("moonrise")
    @Expose
    val moonrise: Int,

    @SerializedName("moonset")
    @Expose
    val moonset: Int,

    @SerializedName("pop")
    @Expose
    val pop: Double,

    @SerializedName("pressure")
    @Expose
    val pressure: Int,

    @SerializedName("rain")
    @Expose
    val rain: Double,

    @SerializedName("summary")
    @Expose
    val summary: String,

    @SerializedName("sunrise")
    @Expose
    val sunrise: Int,

    @SerializedName("sunset")
    @Expose
    val sunset: Int,

    @SerializedName("temp")
    @Expose
    val temp: Temp,

    @SerializedName("uvi")
    @Expose
    val uvi: Double,

    @SerializedName("weather")
    @Expose
    val weather: List<WeatherX>,

    @SerializedName("wind_deg")
    @Expose
    val windDeg: Int,

    @SerializedName("wind_gust")
    @Expose
    val windGust: Double,

    @SerializedName("wind_speed")
    @Expose
    val windSpeed: Double
)