package com.example.myweatherapp.citiesmodel

data class CityData(
    val clouds: Clouds,
    val coord: Coord,
    val dt: Int,
    val id: Int,
    val main: Main,
    val name: String,
    val rain: Any,
    val snow: Any,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind
)