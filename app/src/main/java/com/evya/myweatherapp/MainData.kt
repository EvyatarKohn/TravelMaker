package com.evya.myweatherapp

import com.evya.myweatherapp.Constants.DEFAULT_ATTRACTION_RADIUS
import com.evya.myweatherapp.Constants.METRIC
import com.evya.myweatherapp.model.weathermodel.Weather

object MainData {
    var lat = ""//"32.083333"
    var long = ""// "34.7999968"
    var addedToFav = false
    var degreesUnits = METRIC
    var attractionRadius = DEFAULT_ATTRACTION_RADIUS
    var approvedPermissions = false
    var weather: Weather? = null
    var counter = 0
}