package com.evya.myweatherapp

import com.evya.myweatherapp.model.placesmodel.Feature
import com.evya.myweatherapp.model.placesmodel.Geometry
import com.evya.myweatherapp.model.placesmodel.Properties
import com.evya.myweatherapp.model.weathermodel.Current
import com.evya.myweatherapp.model.weathermodel.Weather
import com.evya.myweatherapp.model.weathermodel.WeatherX
import com.evya.myweatherapp.util.AttractionSetting
import com.evya.myweatherapp.util.attractionSetting
import com.evya.myweatherapp.util.preferIndoorOuting
import com.evya.myweatherapp.util.rankPlacesForWeather
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AttractionWeatherTest {
    private fun weather(code: Int) = Weather(
        alerts = null,
        current = Current(0, 0.0, 0, 20.0, 50, 1000, 0, 0, 20.0, 0.0, 10000,
            listOf(WeatherX("x", "01d", code, "X")), 0, 0.0, 0.0),
        daily = null, hourly = null, lat = 0.0, lon = 0.0, minutely = null,
        timezone = "UTC", timezoneOffset = 0, cityName = "T", isInFavorites = false,
        callTime = 0L, dailyWeather = null,
    )

    private fun place(id: String, kinds: String, dist: Double) = Feature(
        geometry = Geometry(listOf(0.0, 0.0), "Point"),
        id = id,
        properties = Properties(dist, kinds, id, null, 0, null, id),
        type = "Feature",
    )

    @Test fun classifiesIndoorAndOutdoorKinds() {
        assertEquals(AttractionSetting.INDOOR, attractionSetting(listOf("museums")))
        assertEquals(AttractionSetting.OUTDOOR, attractionSetting(listOf("natural")))
        assertEquals(AttractionSetting.MIXED, attractionSetting(listOf("museums", "natural")))
    }

    @Test fun prefersIndoorWhenStormyOrNoWindow() {
        assertTrue(preferIndoorOuting(weather(500), hasOutdoorWindow = true))
        assertTrue(preferIndoorOuting(weather(800), hasOutdoorWindow = false))
        assertFalse(preferIndoorOuting(weather(800), hasOutdoorWindow = true))
    }

    @Test fun ranksIndoorPlacesFirstWhenNeeded() {
        val ranked = rankPlacesForWeather(
            listOf(place("park", "natural", 100.0), place("museum", "museums", 500.0)),
            preferIndoor = true,
        )
        assertEquals("museum", ranked.first().id)
    }
}
