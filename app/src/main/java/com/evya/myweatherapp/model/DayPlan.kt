package com.evya.myweatherapp.model

data class PlanStop(val id: String, val name: String, val category: String, val latitude: Double, val longitude: Double)
data class DayPlan(val date: String = "", val notes: String = "", val stops: List<PlanStop> = emptyList()) {
    fun add(stop: PlanStop): DayPlan = if (stops.any { it.id == stop.id }) this else copy(stops = stops + stop)
    fun remove(id: String) = copy(stops = stops.filterNot { it.id == id })
    fun move(id: String, offset: Int): DayPlan {
        val index = stops.indexOfFirst { it.id == id }
        val target = index + offset
        if (index < 0 || target !in stops.indices) return this
        val reordered = stops.toMutableList()
        reordered.add(target, reordered.removeAt(index))
        return copy(stops = reordered)
    }
}
