package com.evya.myweatherapp.util

import com.evya.myweatherapp.model.weathermodel.Hourly

/** A two-hour daylight outing; inputs use the units of the weather API response. */
data class OutdoorWindow(val first: Hourly, val second: Hourly)

fun bestOutdoorWindow(
    hours: List<Hourly>,
    daylight: List<LongRange>,
    imperial: Boolean,
    nowSeconds: Long,
): OutdoorWindow? {
    fun celsius(hour: Hourly) = if (imperial) (hour.feelsLike - 32) * 5 / 9 else hour.feelsLike
    fun wind(hour: Hourly) = if (imperial) hour.windSpeed * 0.44704 else hour.windSpeed
    fun comfortable(hour: Hourly): Boolean =
        hour.pop.isFinite() && hour.pop in 0.0..0.3 && celsius(hour) in 10.0..30.0 &&
            wind(hour) in 0.0..8.0 && hour.uvi.isFinite() && hour.uvi in 0.0..7.0 &&
            hour.weather.isNotEmpty() && hour.weather.all { it.id in 800..804 }
    return hours.distinctBy { it.dt }.sortedBy { it.dt }
        .filter { it.dt.toLong() >= nowSeconds && it.dt.toLong() < nowSeconds + 24 * 3600 }
        .zipWithNext()
        .filter { (a, b) ->
            b.dt.toLong() - a.dt.toLong() == 3600L && comfortable(a) && comfortable(b) &&
                daylight.any { a.dt.toLong() in it && b.dt.toLong() + 3600 <= it.last }
        }
        .minByOrNull { (a, b) ->
            listOf(a, b).sumOf { it.pop * 100 + kotlin.math.abs(celsius(it) - 22) * 2 + wind(it) + it.uvi }
        }?.let { OutdoorWindow(it.first, it.second) }
}
