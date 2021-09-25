package com.evya.myweatherapp.ui

import com.evya.myweatherapp.model.placesmodel.Places
import com.evya.myweatherapp.model.weathermodel.Weather
import com.google.android.gms.maps.model.LatLng

interface MainListener {
    fun showCityWeather(cityName: String, lat: String, long: String)
    fun replaceToCustomCityFragment()
    fun showCityWeatherFromList(weather: Weather)
    fun showAttractionMap(localLatLng: LatLng, places: Places)
}