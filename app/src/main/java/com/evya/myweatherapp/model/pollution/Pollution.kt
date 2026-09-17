package com.evya.myweatherapp.model.pollution


import com.evya.myweatherapp.Constants.AirQuality
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Pollution(
    @SerializedName("coord")
    @Expose
    val coord: Coord,

    @SerializedName("list")
    @Expose
    val list: List<PollutionData>
) {

    fun definePollution(): String {
        var airAirQuality = AirQuality.GOOD.airQuality
        if (list[0].components.no2 > 400 || list[0].components.pm10 > 180 ||
            list[0].components.o3 > 240 || list[0].components.pm25 > 110
        ) {
            airAirQuality = AirQuality.VERY_POOR.airQuality
        } else if ((list[0].components.no2 > 200 && list[0].components.no2 < 399) ||
            (list[0].components.pm10 > 90 && list[0].components.pm10 < 179) ||
            (list[0].components.o3 > 180 && list[0].components.o3 < 239) ||
            (list[0].components.pm25 > 55 && list[0].components.pm25 < 109)
        ) {
            airAirQuality = AirQuality.POOR.airQuality
        } else if ((list[0].components.no2 > 100 && list[0].components.no2 < 199) ||
            (list[0].components.pm10 > 50 && list[0].components.pm10 < 89) ||
            (list[0].components.o3 > 120 && list[0].components.o3 < 179) ||
            (list[0].components.pm25 > 30 && list[0].components.pm25 < 54)
        ) {
            airAirQuality = AirQuality.MODERATE.airQuality
        } else if ((list[0].components.no2 > 50 && list[0].components.no2 < 99) ||
            (list[0].components.pm10 > 25 && list[0].components.pm10 < 49) ||
            (list[0].components.o3 > 60 && list[0].components.o3 < 119) ||
            (list[0].components.pm25 > 15 && list[0].components.pm25 < 29)
        ) {
            airAirQuality = AirQuality.FAIR.airQuality
        } else if ((list[0].components.no2 > 0 && list[0].components.no2 < 49) ||
            (list[0].components.pm10 > 0 && list[0].components.pm10 < 24) ||
            (list[0].components.o3 > 0 && list[0].components.o3 < 59) ||
            (list[0].components.pm25 > 0 && list[0].components.pm25 < 14)
        ) {
            airAirQuality = AirQuality.GOOD.airQuality
        }

        return "Air\nQuality:\n$airAirQuality"
    }
}