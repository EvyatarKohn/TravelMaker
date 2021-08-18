package com.evya.myweatherapp.ui

interface MainListener {
    fun startFlow()
    fun replaceFragment(cityName: String, lat: String, long: String)
    fun replaceToCitiesListFragment(boundaryBox: String)
    fun replaceToCustomCityFragment()
    fun showCitiesListDialog()
}