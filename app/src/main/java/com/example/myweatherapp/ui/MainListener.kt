package com.example.myweatherapp.ui

interface MainListener {
    fun replaceFragment(cityName: String, lat: String, long: String)
    fun replaceToCitiesListFragment()
    fun replaceToCustomCityFragment()
}