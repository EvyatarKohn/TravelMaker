package com.evya.myweatherapp

import com.evya.myweatherapp.model.weathermodel.Hourly
import com.evya.myweatherapp.model.weathermodel.WeatherX
import com.evya.myweatherapp.util.bestOutdoorWindow
import com.evya.myweatherapp.util.forecastTime
import com.evya.myweatherapp.util.relativeTimeEnglish
import org.junit.Assert.*
import org.junit.Test

class OutdoorForecastTest {
    private val daylight = listOf(3600L..36000L)
    private fun hour(time: Int, rain: Double = 0.1, feels: Double = 22.0, wind: Double = 2.0) =
        Hourly(0, 0.0, time, feels, 40, rain, 1010, feels, 3.0, 10000,
            listOf(WeatherX("clear sky", "01d", 800, "Clear")), 0, wind, wind)

    @Test fun selectsBestContiguousWindow() {
        val best = bestOutdoorWindow(listOf(hour(3600, .2), hour(7200), hour(10800)), daylight, false, 0)
        assertEquals(7200, best?.first?.dt)
        assertEquals(10800, best?.second?.dt)
    }
    @Test fun rejectsGapsDarknessPastAndStorms() {
        assertNull(bestOutdoorWindow(listOf(hour(3600), hour(10800)), daylight, false, 0))
        assertNull(bestOutdoorWindow(listOf(hour(3600), hour(7200)), listOf(3600L..8000L), false, 0))
        assertNull(bestOutdoorWindow(listOf(hour(3600), hour(7200)), daylight, false, 8000))
        val storm = hour(3600).copy(weather = listOf(WeatherX("thunderstorm", "11d", 200, "Thunderstorm")))
        assertNull(bestOutdoorWindow(listOf(storm, hour(7200)), daylight, false, 0))
    }
    @Test fun handlesImperialAndUncomfortableConditions() {
        assertNotNull(bestOutdoorWindow(listOf(hour(3600, feels = 71.6, wind = 4.47), hour(7200, feels = 71.6, wind = 4.47)), daylight, true, 0))
        for (bad in listOf(hour(3600, .8), hour(3600, feels = 40.0), hour(3600, wind = 20.0), hour(3600).copy(uvi = 10.0))) {
            assertNull(bestOutdoorWindow(listOf(bad, hour(7200)), daylight, false, 0))
        }
        assertNull(bestOutdoorWindow(emptyList(), daylight, false, 0))
    }
    @Test fun formatsDestinationTimeWithoutAddingOffsetTwice() {
        assertEquals("02:00", forecastTime(0, "+02:00"))
        assertEquals("00:00", forecastTime(0, "invalid-zone"))
    }

    @Test fun formatsWeekdayNamesInEnglish() {
        // Epoch 0 is Thu 1 Jan 1970 UTC — must stay English even if the device locale is Hebrew.
        assertEquals("Thu 1 Jan", forecastTime(0, "UTC", "EEE d MMM"))
    }

    @Test fun formatsRelativeTimeInEnglish() {
        val now = 1_000_000L
        assertEquals("just now", relativeTimeEnglish(now, now))
        assertEquals("1 minute ago", relativeTimeEnglish(now - 60_000, now))
        assertEquals("1 hour ago", relativeTimeEnglish(now - 3_600_000, now))
        assertEquals("2 hours ago", relativeTimeEnglish(now - 7_200_000, now))
    }
}
