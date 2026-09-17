package com.evya.myweatherapp

import com.evya.myweatherapp.model.weathermodel.Current
import com.evya.myweatherapp.model.weathermodel.Weather
import com.evya.myweatherapp.model.weathermodel.WeatherX
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WeatherUnitsTest {
    private fun sample(units: String) = Weather(
        alerts = null,
        current = Current(
            0, 0.0, 0, 72.0, 50, 1000, 0, 0, 72.0, 0.0, 10000,
            listOf(WeatherX("clear", "01d", 800, "Clear")), 0, 5.0, 5.0
        ),
        daily = null,
        hourly = null,
        lat = 0.0,
        lon = 0.0,
        minutely = null,
        timezone = "UTC",
        timezoneOffset = 0,
        cityName = "Test",
        isInFavorites = false,
        callTime = 0L,
        dailyWeather = null,
        responseUnits = units,
    )

    @Test
    fun labelsFollowResponseUnitsNotGlobalPreference() {
        MainData.degreesUnits = Constants.METRIC
        val imperial = sample(Constants.IMPERIAL)
        assertTrue(imperial.isImperial())
        assertTrue(imperial.getDegreeUnits(72.0).endsWith("\u2109"))
        assertTrue(imperial.getWindSpeedDegree().contains("miles"))
        assertTrue(imperial.getVisibilityUnits(1609).contains("mile"))

        MainData.degreesUnits = Constants.IMPERIAL
        val metric = sample(Constants.METRIC)
        assertFalse(metric.isImperial())
        assertTrue(metric.getDegreeUnits(22.0).endsWith("\u2103"))
        assertTrue(metric.getWindSpeedDegree().contains("m/s"))
        assertEquals("1 Km", metric.getVisibilityUnits(1000))
    }
}
