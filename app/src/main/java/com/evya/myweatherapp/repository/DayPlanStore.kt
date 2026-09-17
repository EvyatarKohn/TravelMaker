package com.evya.myweatherapp.repository

import android.content.Context
import com.evya.myweatherapp.model.DayPlan
import com.google.gson.Gson

/** Small local itinerary. No account or network is required. */
class DayPlanStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences("day_plan_v1", Context.MODE_PRIVATE)
    private val gson = Gson()
    fun load(): DayPlan = runCatching {
        val plan = gson.fromJson(preferences.getString("plan", null), DayPlan::class.java) ?: DayPlan()
        plan.copy(stops = plan.stops.filter {
            it.id.isNotBlank() && it.name.isNotBlank() && it.latitude.isFinite() && it.longitude.isFinite() &&
                it.latitude in -90.0..90.0 && it.longitude in -180.0..180.0
        }.distinctBy { it.id })
    }.getOrDefault(DayPlan())
    fun save(plan: DayPlan) { preferences.edit().putString("plan", gson.toJson(plan)).apply() }
}
