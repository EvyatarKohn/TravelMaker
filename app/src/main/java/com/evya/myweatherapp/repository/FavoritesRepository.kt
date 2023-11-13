package com.evya.myweatherapp.repository

import com.evya.myweatherapp.db.FavoritesDao
import com.evya.myweatherapp.model.weathermodel.Weather
import javax.inject.Inject

class FavoritesRepository @Inject constructor(private val favoritesDao: FavoritesDao) {

    suspend fun addCityDataToDB(weather: Weather) = favoritesDao.addCityDataToDB(weather)

    suspend fun fetchSpecificCity(cityName: String?) = favoritesDao.fetchSpecificCity(cityName ?: "")

    val fetchAllCitiesFromDB = favoritesDao.fetchAllCities()

   /* fun checkIfAlreadyAddedToDB(cityName: String) =
        favoritesDao.checkIfAlreadyAddedToDB(cityName)*/
    fun checkIfAlreadyInFav(cityName: String?) =
        favoritesDao.checkIfAlreadyInFav(cityName ?: "")

    suspend fun removeCityDataFromDB(cityName: String?) =
        favoritesDao.deleteSpecificFavorite(cityName ?: "")

    suspend fun deleteAllFavorite(isInFavorites: Boolean) =
        favoritesDao.deleteAllFavorite(isInFavorites)

    suspend fun nukeTable() = favoritesDao.nukeTable()

    fun updateFavorites(isInFavorites: Boolean, cityName: String?) =
        favoritesDao.updateFavorites(isInFavorites, cityName ?: "")
}