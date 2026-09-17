package com.evya.myweatherapp.util

internal fun forecastScrollTarget(visiblePosition: Int, itemCount: Int, offset: Int): Int? {
    if (itemCount <= 0 || visiblePosition !in 0 until itemCount) return null
    return (visiblePosition + offset).coerceIn(0, itemCount - 1)
}
