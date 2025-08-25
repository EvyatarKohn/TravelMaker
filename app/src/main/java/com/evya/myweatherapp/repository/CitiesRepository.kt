package com.evya.myweatherapp.repository

import com.evya.myweatherapp.db.CitiesDao
import com.evya.myweatherapp.model.weathermodel.Weather
import javax.inject.Inject

class CitiesRepository @Inject constructor(private val citiesDao: CitiesDao) {

    suspend fun addCityDataToDB(weather: Weather) = citiesDao.addCityDataToDB(weather)

    suspend fun fetchSpecificCity(cityName: String?) = citiesDao.fetchSpecificCity(cityName ?: "")

    val fetchAllCitiesFromDB = citiesDao.fetchAllCities()

   /* fun checkIfAlreadyAddedToDB(cityName: String) =
        favoritesDao.checkIfAlreadyAddedToDB(cityName)*/
    fun checkIfAlreadyInFav(cityName: String?) =
        citiesDao.checkIfAlreadyInFav(cityName ?: "")

    suspend fun removeCityDataFromDB(cityName: String?) =
        citiesDao.deleteSpecificFavorite(cityName ?: "")

    suspend fun deleteAllFavorite(isInFavorites: Boolean) =
        citiesDao.deleteAllFavorite(isInFavorites)

    suspend fun nukeTable() = citiesDao.nukeTable()

    fun updateFavorites(isInFavorites: Boolean, cityName: String?) =
        citiesDao.updateFavorites(isInFavorites, cityName ?: "")
}