package com.evya.myweatherapp.repository

import com.evya.myweatherapp.db.FavoritesDao
import com.evya.myweatherapp.model.weathermodel.Weather
import javax.inject.Inject

class FavoritesRepository @Inject constructor(private val favoritesDao: FavoritesDao) {

    suspend fun addCityDataToDB(weather: Weather) = favoritesDao.addFavorites(weather)

    val fetchAllCitiesFromDB = favoritesDao.fetchAllCities()

    fun checkIfAlreadyAddedToDB(cityName: String) = favoritesDao.checkIfAlreadyAdded(cityName)

    suspend fun removeCityDataFromDB(cityName: String) =
        favoritesDao.deleteSpecificFavorite(cityName)

    suspend fun deleteAllFavorites() = favoritesDao.deleteAllFavorites()
}