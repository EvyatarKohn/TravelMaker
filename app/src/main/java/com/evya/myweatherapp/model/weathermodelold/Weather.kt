package com.evya.myweatherapp.model.weathermodelold


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.evya.myweatherapp.Constants.IMPERIAL
import com.evya.myweatherapp.Constants.IMPERIAL_DEGREE
import com.evya.myweatherapp.Constants.KM
import com.evya.myweatherapp.Constants.METRIC_DEGREE
import com.evya.myweatherapp.Constants.MILE
import com.evya.myweatherapp.MainData.degreesUnits
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

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
) {
    fun changeDoubleToInt(double: Double): Int {
        return double.toInt()
    }

    fun setTimeToHour(time: Int): String {
        val calendar = Calendar.getInstance()
        val tz = TimeZone.getDefault()
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.timeInMillis))
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(time.toLong() * 1000))
    }

    fun getDegreeUnits(temp: Double): String {
        return if (degreesUnits == IMPERIAL) {
            temp.toInt().toString() + " \u2109"
        } else {
            temp.toInt().toString() + " \u2103"
        }
    }

    fun getWindSpeedDegree(): String {
        return if (degreesUnits == IMPERIAL) {
            IMPERIAL_DEGREE
        } else {
            METRIC_DEGREE
        }
    }

    fun getVisibilityUnits(visibility: Int): String {
        return if (degreesUnits == IMPERIAL) {
            (visibility / 1609).toString() + MILE
        } else {
            (visibility / 1000).toString() + KM
        }
    }

}