package com.evya.myweatherapp

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.evya.myweatherapp.db.CitiesDB
import com.evya.myweatherapp.model.weathermodel.Current
import com.evya.myweatherapp.model.weathermodel.Weather
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritesDatabaseTest {
    private lateinit var database: CitiesDB

    @Before fun setUp() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), CitiesDB::class.java).build()
    }

    @After fun tearDown() = database.close()

    @Test fun missingCityHasNoFavoriteFlagOrCachedWeather() = runBlocking {
        val dao = database.attractionsDao()
        assertFalse(dao.checkIfAlreadyInFav("Missing city").first())
        assertNull(dao.fetchSpecificCity("Missing city"))
    }

    @Test fun deletingLastFavoriteReturnsFalseInsteadOfNull() = runBlocking {
        val dao = database.attractionsDao()
        dao.addCityDataToDB(city("Tel Aviv", true))
        assertTrue(dao.checkIfAlreadyInFav("Tel Aviv").first())
        dao.deleteSpecificFavorite("Tel Aviv")
        assertFalse(dao.checkIfAlreadyInFav("Tel Aviv").first())
        assertTrue(dao.fetchAllCities().first().isEmpty())
    }

    @Test fun deletingAllFavoritesPreservesOtherCachedCities() = runBlocking {
        val dao = database.attractionsDao()
        dao.addCityDataToDB(city("Tel Aviv", true))
        dao.addCityDataToDB(city("Paris", false))
        dao.deleteAllFavorite(true)
        assertFalse(dao.checkIfAlreadyInFav("Tel Aviv").first())
        assertFalse(dao.checkIfAlreadyInFav("Paris").first())
        assertEquals(listOf("Paris"), dao.fetchAllCities().first().map { it.cityName })
    }

    private fun city(name: String, favorite: Boolean) = Weather(
        alerts = null, current = Current(0, 0.0, 0, 20.0, 50, 1000, 0, 0, 20.0, 0.0, 10000, emptyList(), 0, 0.0, 0.0),
        daily = null, hourly = null, lat = 32.0, lon = 34.0, minutely = null,
        timezone = "Asia/Jerusalem", timezoneOffset = 0, cityName = name,
        isInFavorites = favorite, callTime = 0, dailyWeather = null
    )
}
