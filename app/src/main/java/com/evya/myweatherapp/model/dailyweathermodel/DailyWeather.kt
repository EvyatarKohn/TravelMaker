package com.evya.myweatherapp.model.dailyweathermodel


import com.evya.myweatherapp.Constants
import com.evya.myweatherapp.R
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class DailyWeather(
    @SerializedName("city")
    @Expose
    val city: City,

    @SerializedName("cnt")
    @Expose
    val cnt: Int,

    @SerializedName("cod")
    @Expose
    val cod: String,

    @SerializedName("list")
    @Expose
    val list: List<DailyWeatherData>,

    @Expose
    @SerializedName("message")
    val message: Int
) {

    fun precipitationLast3H(): String {
        var rainHeight = "0"
        var text = "Rain last\n3h\n"
        if (list[0].rain != null) {
            rainHeight = list[0].rain.h.toString()
        } else if (list[0].snow != null) {
            rainHeight = list[0].snow.h.toString()
            text = "Snow last\n3h\n"
        }

        return text + rainHeight + Constants.MM
    }
}