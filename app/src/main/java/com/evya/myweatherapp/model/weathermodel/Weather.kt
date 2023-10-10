package com.evya.myweatherapp.model.weathermodel


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.evya.myweatherapp.Constants
import com.evya.myweatherapp.MainData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Entity(tableName = "favorites")
data class Weather(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    @SerializedName("alerts")
    @Expose
    val alerts: List<Alerts>?,

    @SerializedName("current")
    @Expose
    val current: Current,

    @SerializedName("daily")
    @Expose
    val daily: List<Daily>?,

    @SerializedName("hourly")
    @Expose
    val hourly: List<Hourly>?,

    @SerializedName("lat")
    @Expose
    val lat: Double,

    @SerializedName("lon")
    @Expose
    val lon: Double,

    @SerializedName("minutely")
    @Expose
    val minutely: List<Minutely>?,

    @SerializedName("timezone")
    @Expose
    val timezone: String,

    @SerializedName("timezone_offset")
    @Expose
    val timezoneOffset: Int,

    @SerializedName("cityName")
    @Expose
    var cityName: String
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
        return if (MainData.degreesUnits == Constants.IMPERIAL) {
            temp.toInt().toString() + " \u2109"
        } else {
            temp.toInt().toString() + " \u2103"
        }
    }

    fun getWindSpeedDegree(): String {
        return if (MainData.degreesUnits == Constants.IMPERIAL) {
            Constants.IMPERIAL_DEGREE
        } else {
            Constants.METRIC_DEGREE
        }
    }

    fun getVisibilityUnits(visibility: Int): String {
        return if (MainData.degreesUnits == Constants.IMPERIAL) {
            (visibility / 1609).toString() + Constants.MILE
        } else {
            (visibility / 1000).toString() + Constants.KM
        }
    }

    fun precipitationToday(): String {
        var rainHeight = "0"
        val text = "Rain Today:\n"
        if (daily?.get(0)?.rain != null) {
            rainHeight = daily[0].rain.toString()
        }

        return if (MainData.degreesUnits == Constants.METRIC) {
            text + rainHeight + Constants.MM
        } else {
            text + (String.format("%.2f", rainHeight.toDouble() * 0.04)) + Constants.INCH
        }
    }
}