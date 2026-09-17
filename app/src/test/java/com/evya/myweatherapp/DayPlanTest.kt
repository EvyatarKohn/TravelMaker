package com.evya.myweatherapp

import com.evya.myweatherapp.model.DayPlan
import com.evya.myweatherapp.model.PlanStop
import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class DayPlanTest {
    private val a = PlanStop("a", "Museum", "Culture", 32.0, 34.0)
    private val b = PlanStop("b", "Park", "Nature", 32.1, 34.1)
    @Test fun addingSamePlaceIsIdempotent() {
        assertEquals(listOf(a), DayPlan().add(a).add(a).stops)
    }
    @Test fun reorderAndRemovePreserveNotesAndDate() {
        val plan = DayPlan("2026-09-20", "Meet at ten", listOf(a, b))
        assertEquals(listOf(b, a), plan.move("b", -1).stops)
        assertEquals(plan, plan.move("a", -1))
        assertEquals(plan, plan.move("missing", 1))
        assertEquals(plan.copy(stops = listOf(b)), plan.remove("a"))
    }
    @Test fun storageRoundTripKeepsCoordinatesOrderAndText() {
        val plan = DayPlan("2026-09-20", "Picnic & coffee", listOf(b, a))
        val gson = Gson()
        assertEquals(plan, gson.fromJson(gson.toJson(plan), DayPlan::class.java))
    }
}
