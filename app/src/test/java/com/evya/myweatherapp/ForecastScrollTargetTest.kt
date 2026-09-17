package com.evya.myweatherapp

import com.evya.myweatherapp.util.forecastScrollTarget
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ForecastScrollTargetTest {
    @Test fun leftArrowNearStartNeverProducesNegativePosition() {
        for (position in 0..3) assertEquals(0, forecastScrollTarget(position, 8, -3))
    }

    @Test fun rightArrowStopsAtLastItem() {
        for (position in 4..7) assertEquals(7, forecastScrollTarget(position, 8, 3))
    }

    @Test fun emptyOrUnlaidOutListDoesNotScroll() {
        assertNull(forecastScrollTarget(-1, 0, -3))
        assertNull(forecastScrollTarget(0, 0, 3))
        assertNull(forecastScrollTarget(-1, 8, 3))
        assertNull(forecastScrollTarget(7, 3, -3))
    }

    @Test fun singleItemAndNormalPagingStayInBounds() {
        assertEquals(0, forecastScrollTarget(0, 1, -3))
        assertEquals(0, forecastScrollTarget(0, 1, 3))
        assertEquals(2, forecastScrollTarget(5, 8, -3))
        assertEquals(5, forecastScrollTarget(2, 8, 3))
    }
}
