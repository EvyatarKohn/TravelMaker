package com.evya.myweatherapp.ui

interface MainListener {
    fun showCityWeather(cityName: String, lat: String, long: String)
    fun replaceToCitiesListFragment(boundaryBox: String)
    fun replaceToCustomCityFragment()
}