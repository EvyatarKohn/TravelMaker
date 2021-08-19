package com.evya.myweatherapp.ui

interface MainListener {
    fun replaceFragment(cityName: String, lat: String, long: String)
    fun replaceToCitiesListFragment(boundaryBox: String)
    fun replaceToCustomCityFragment()
    fun showCitiesListDialog()
}