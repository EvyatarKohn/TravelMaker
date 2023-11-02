package com.evya.myweatherapp.repository

import com.evya.myweatherapp.db.FavoritesDao
import com.evya.myweatherapp.model.weathermodel.Weather
import javax.inject.Inject

class FavoritesRepository @Inject constructor(private val favoritesDao: FavoritesDao) {

    suspend fun addCityDataToDB(weather: Weather) = favoritesDao.addCityDataToDB(weather)

    fun fetchSpecificCity(cityName: String?) = favoritesDao.fetchSpecificCity(cityName ?: "")

    val fetchAllCitiesFromDB = favoritesDao.fetchAllCities()

   /* fun checkIfAlreadyAddedToDB(cityName: String) =
        favoritesDao.checkIfAlreadyAddedToDB(cityName)*/
    fun checkIfAlreadyInFav(cityName: String?) =
        favoritesDao.checkIfAlreadyInFav(cityName ?: "")

    suspend fun removeCityDataFromDB(cityName: String?) =
        favoritesDao.deleteSpecificFavorite(cityName ?: "")


    suspend fun deleteAllFavorites() = favoritesDao.deleteAllFavorites()

    fun update(isInFavorites: Boolean, cityName: String?) =
        favoritesDao.update(isInFavorites, cityName ?: "")
}