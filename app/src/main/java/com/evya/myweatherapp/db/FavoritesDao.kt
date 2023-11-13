package com.evya.myweatherapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.evya.myweatherapp.model.weathermodel.Weather
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCityDataToDB(weather: Weather)

    @Query("SELECT * FROM favorites WHERE cityName = :cityName")
    suspend fun fetchSpecificCity(cityName: String): Weather

/*    @Query("SELECT EXISTS(SELECT * FROM favorites WHERE cityName = :cityName)")
    fun fetchSpecificCity(cityName: String): Flow<Weather>*/

    @Query("SELECT * FROM favorites")
    fun fetchAllCities(): Flow<List<Weather>>

   /* @Query("SELECT EXISTS(SELECT * FROM favorites WHERE cityName = :cityName)")
    fun checkIfAlreadyAdded(cityName: String) : Flow<Boolean>*/

    /*@Query("SELECT * FROM favorites WHERE cityName = :cityName")
    fun checkIfAlreadyAddedToDB(cityName: String) : Flow<Boolean>
*/
    @Query("SELECT isInFavorites FROM favorites WHERE cityName = :cityName")
    fun checkIfAlreadyInFav(cityName: String) : Flow<Boolean>

    @Query("DELETE FROM favorites WHERE cityName = :cityName")
    suspend fun deleteSpecificFavorite(cityName: String)

    @Query("DELETE FROM favorites where isInFavorites = :isInFavorites")
    suspend fun deleteAllFavorite(isInFavorites: Boolean)

    @Query("DELETE FROM favorites")
    suspend fun nukeTable()

    @Query("UPDATE favorites SET isInFavorites = :isInFavorites WHERE cityName = :cityName")
    fun updateFavorites(isInFavorites: Boolean, cityName: String)
}