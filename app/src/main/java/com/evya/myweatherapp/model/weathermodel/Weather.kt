package com.evya.myweatherapp.model.weathermodel


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.evya.myweatherapp.Constants
import com.evya.myweatherapp.model.dailyweathermodel.DailyWeather
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Entity(tableName = "cities")
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

    var cityName: String,

    var isInFavorites: Boolean,

    var callTime: Long,

    var dailyWeather: DailyWeather?,

    /** Units of the API values stored on this row (`metric` / `imperial`). Not from the JSON body. */
    var responseUnits: String = Constants.METRIC,

) {
    fun isImperial(): Boolean = responseUnits == Constants.IMPERIAL

    fun changeDoubleToInt(double: Double): Int {
        return double.toInt()
    }

    fun setTimeToHour(time: Int): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone(timezone)
        return sdf.format(Date(time.toLong() * 1000))
    }

    fun getDegreeUnits(temp: Double): String {
        return if (isImperial()) {
            temp.toInt().toString() + " \u2109" // Fahrenheit symbol
        } else {
            temp.toInt().toString() + " \u2103" // Celsius symbol
        }
    }

    fun getWindSpeedDegree(): String {
        return if (isImperial()) {
            Constants.IMPERIAL_DEGREE
        } else {
            Constants.METRIC_DEGREE
        }
    }

    fun getVisibilityUnits(visibility: Int): String {
        return if (isImperial()) {
            (visibility / 1609).toString() + Constants.MILE
        } else {
            (visibility / 1000).toString() + Constants.KM
        }
    }

    fun precipitationAmount(): String {
        val rainHeight = daily?.firstOrNull()?.rain?.toString() ?: "0"
        return if (!isImperial()) {
            rainHeight + Constants.MM
        } else {
            String.format(Locale.US, "%.2f", rainHeight.toDouble() * 0.04) + Constants.INCH
        }
    }

    fun precipitationToday(): String {
        return "Rain Today:\n" + precipitationAmount()
    }
}
