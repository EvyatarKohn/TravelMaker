package com.evya.myweatherapp.db

import androidx.room.*
import com.evya.myweatherapp.model.weathermodel.Weather
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {
    @Insert
    suspend fun addFavorites(weather: Weather)

    @Query("SELECT * FROM favorites")
    fun fetchAllCities(): Flow<List<Weather>>

    @Query("SELECT EXISTS(SELECT * FROM favorites WHERE name = :cityName)")
    fun checkIfAlreadyAdded(cityName: String) : Flow<Boolean>

    @Query("DELETE FROM favorites WHERE name = :cityName")
    suspend fun deleteSpecificFavorite(cityName: String)

    @Query("DELETE FROM favorites")
    suspend fun deleteAllFavorites()
}