package com.evya.myweatherapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.evya.myweatherapp.model.weathermodel.Weather
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {
    @Insert
    suspend fun addCityDataToDB(weather: Weather)

    @Query("SELECT * FROM favorites WHERE cityName = :cityName")
    fun fetchSpecificCity(cityName: String): LiveData<Weather>

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

    @Query("DELETE FROM favorites")
    suspend fun deleteAllFavorites()

    @Query("UPDATE favorites SET isInFavorites = :isInFavorites WHERE cityName = :cityName")
    fun update(isInFavorites: Boolean, cityName: String)
}