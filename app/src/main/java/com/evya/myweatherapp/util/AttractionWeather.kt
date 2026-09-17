package com.evya.myweatherapp.util

import com.evya.myweatherapp.model.placesmodel.Feature
import com.evya.myweatherapp.model.weathermodel.Weather

enum class AttractionSetting { INDOOR, OUTDOOR, MIXED }

private val indoorKinds = setOf(
    "museums", "cultural", "accomodations", "accommodations", "hotels", "hostels",
    "banks", "foods", "restaurants", "cafes", "shops", "malls", "cinema", "theatres",
    "opera_houses", "concert_halls", "adult", "nightclubs", "bars", "pubs",
)

private val outdoorKinds = setOf(
    "natural", "beaches", "gardens_and_parks", "national_parks", "view_points",
    "outdoor", "sport", "climbing", "picnic_site", "historic", "architecture",
    "fountains", "squares", "monuments",
)

fun attractionSetting(kinds: Iterable<String>): AttractionSetting {
    val normalized = kinds.map { it.trim().lowercase() }.filter { it.isNotEmpty() }.toSet()
    val indoor = normalized.any { kind -> indoorKinds.any { kind.contains(it) } }
    val outdoor = normalized.any { kind -> outdoorKinds.any { kind.contains(it) } }
    return when {
        indoor && !outdoor -> AttractionSetting.INDOOR
        outdoor && !indoor -> AttractionSetting.OUTDOOR
        else -> AttractionSetting.MIXED
    }
}

/** True when an outdoor window is unlikely — prefer museums/food/indoor. */
fun preferIndoorOuting(weather: Weather?, hasOutdoorWindow: Boolean): Boolean {
    if (weather == null) return false
    if (!weather.alerts.isNullOrEmpty()) return true
    if (!hasOutdoorWindow) return true
    val current = weather.current.weather.firstOrNull()?.id ?: return false
    return current in 200..699
}

fun rankPlacesForWeather(places: List<Feature>, preferIndoor: Boolean): List<Feature> {
    if (!preferIndoor) return places
    return places.sortedWith(
        compareBy<Feature> {
            when (attractionSetting(it.properties.kinds.split(","))) {
                AttractionSetting.INDOOR -> 0
                AttractionSetting.MIXED -> 1
                AttractionSetting.OUTDOOR -> 2
            }
        }.thenBy { it.properties.dist }
    )
}

fun suggestedAttractionKinds(preferIndoor: Boolean): List<String> =
    if (preferIndoor) listOf("museums", "cultural", "foods", "accomodations")
    else listOf("natural", "historic", "cultural")
