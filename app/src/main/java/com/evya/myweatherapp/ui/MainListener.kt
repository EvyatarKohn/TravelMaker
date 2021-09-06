package com.evya.myweatherapp.ui

import com.evya.myweatherapp.model.weathermodel.Weather

interface MainListener {
    fun showCityWeather(cityName: String, lat: String, long: String)
    fun replaceToCitiesListFragment(boundaryBox: String)
    fun replaceToCustomCityFragment()
    fun showCityWeatherFromList(weather: Weather)
}