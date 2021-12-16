package com.evya.myweatherapp.db

import androidx.room.TypeConverter
import com.evya.myweatherapp.model.weathermodel.*
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.lang.reflect.Type
import java.util.*

class DataConverter {
    var gson = Gson()

    @TypeConverter
    fun stringToClouds(data: String?): Clouds {
        if (data == null) {
            return Clouds(0)
        }
        val listType: Type = object : TypeToken<Clouds>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun cloudsToString(someObjects: Clouds): String? {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun stringToCoord(data: String?): Coord {
        if (data == null) {
            return Coord(0.0, 0.0)
        }
        val listType: Type = object : TypeToken<Coord>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun coordToString(someObjects: Coord): String? {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun stringToMain(data: String?): Main {
        if (data == null) {
            return Main(0.0, 0, 0, 0.0, 0.0, 0.0)
        }
        val listType: Type = object : TypeToken<Main>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun mainString(someObjects: Main): String? {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun stringToSys(data: String?): Sys {
        if (data == null) {
            return Sys("", 0, 0, 0, 0)
        }
        val listType: Type = object : TypeToken<Sys>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun sysString(someObjects: Sys): String? {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun stringToWeatherX(data: String?): List<WeatherX> {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object : TypeToken<List<WeatherX>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun weatherXString(someObjects: List<WeatherX>): String? {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun stringToWind(data: String?): Wind {
        if (data == null) {
            return Wind(0, 0.0)
        }
        val listType: Type = object : TypeToken<Wind>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun windString(someObjects: Wind): String? {
        return gson.toJson(someObjects)
    }
}