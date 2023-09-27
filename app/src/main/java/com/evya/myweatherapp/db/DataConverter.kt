package com.evya.myweatherapp.db

import androidx.room.TypeConverter
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.evya.myweatherapp.model.weathermodel.*
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.lang.reflect.Type

class DataConverter {
    var gson = Gson()

    @TypeConverter
    fun stringToAlerts(data: String?): List<Alerts>? {
        if (data == null) {
            return listOf(Alerts("", 0, "", "", 0, emptyList()))
        }
        val listType: Type = object : TypeToken<List<Alerts>?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun alertsToString(someObjects: List<Alerts>?): String? {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun stringToCurrent(data: String?): Current {
        if (data == null) {
            return Current(0, 0.0, 0, 0.0, 0, 0, 0, 0, 0.0, 0.0, 0, emptyList(), 0, 0.0, 0.0)
        }
        val listType: Type = object : TypeToken<Current>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun currentToString(someObjects: Current): String? {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun stringToDaily(data: String?): List<Daily>? {
        if (data == null) {
            return listOf(
                Daily(
                    0,
                    0.0,
                    0,
                    FeelsLike(0.0, 0.0, 0.0, 0.0),
                    0,
                    0.0,
                    0,
                    0,
                    0.0,
                    0,
                    0.0,
                    "",
                    0,
                    0,
                    Temp(0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                    0.0,
                    listOf(WeatherX("", "", 0, "")),
                    0,
                    0.0,
                    0.0
                )
            )
        }
        val listType: Type = object : TypeToken<List<Daily>?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun dailyToString(someObjects: List<Daily>?): String? {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun stringToHourly(data: String?): List<Hourly>? {
        if (data == null) {
            return listOf(
                Hourly(
                    0,
                    0.0,
                    0,
                    0.0,
                    0,
                    0.0,
                    0,
                    0.0,
                    0.0,
                    0,
                    listOf(WeatherX("", "", 0, "")),
                    0,
                    0.0,
                    0.0
                )
            )
        }
        val listType: Type = object : TypeToken<List<Hourly>?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun hourlyString(someObjects: List<Hourly>?): String? {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun stringToMinutely(data: String?): List<Minutely>? {
        if (data == null) {
            return listOf(Minutely(0, 0.0))
        }
        val listType: Type = object : TypeToken<List<Minutely>?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun minutelyString(someObjects: List<Minutely>?): String? {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun stringToFeelsLike(data: String?): FeelsLike {
        if (data == null) {
            return FeelsLike(0.0, 0.0, 0.0, 0.0)
        }
        val listType: Type = object : TypeToken<FeelsLike>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun feelsLikeString(someObjects: FeelsLike): String? {
        return gson.toJson(someObjects)
    }
    @TypeConverter
    fun stringToTemp(data: String?): Temp {
        if (data == null) {
            return Temp(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        }
        val listType: Type = object : TypeToken<Temp>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun tempsLikeString(someObjects: Temp): String? {
        return gson.toJson(someObjects)
    }
}

val MIGRATION_FORM_1_TO_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
//        database.execSQL("ALTER TABLE favorites ADD COLUMN timezone")
        database.execSQL("DROP TABLE IF EXISTS `favorites_temp`")
        database.execSQL("CREATE TABLE IF NOT EXISTS `favorites_temp`(`alerts` TEXT NOT NULL, `current` TEXT NOT NULL, `daily` TEXT NOT NULL, `hourly` TEXT NOT NULL, `lat` DOUBLE NOT NULL, `lon` DOUBLE NOT NULL, `minutely` TEXT NOT NULL, `timezone` TEXT NOT NULL, `timezone_offset` INTEGER NOT NULL)")
        database.execSQL("INSERT INTO favorites_temp(alerts, current, daily, hourly, lat, lon, minutely, timezone, timezone_offset)")
        database.execSQL("DROP TABLE `favorites`")
        database.execSQL("ALTER TABLE favorites_temp RENAME TO favorites")
    }
}