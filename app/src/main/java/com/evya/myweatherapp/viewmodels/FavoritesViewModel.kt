package com.evya.myweatherapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.evya.myweatherapp.db.FavoritesDB
import com.evya.myweatherapp.model.weathermodel.Weather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritesViewModel(app: Application): AndroidViewModel(app) {
    private val favoritesDao = FavoritesDB.getDB(app).attractionsDao()
    private lateinit var mWeather: Weather

    fun addCityDataToDB(weather: Weather) = viewModelScope.launch {
        favoritesDao.addFavorites(weather)
    }

    val fetchAllCities: LiveData<List<Weather>>
        get() = favoritesDao.fetchAllCities().flowOn(Dispatchers.Main)
            .asLiveData(context = viewModelScope.coroutineContext)

    fun setWeather(weather: Weather) {
        mWeather = weather
    }

    val checkIfAlreadyAdded: LiveData<Boolean>
        get() = favoritesDao.checkIfAlreadyAdded(mWeather.name).flowOn(Dispatchers.Main)
            .asLiveData(context = viewModelScope.coroutineContext)

  /*  fun checkIfAlreadyAdded(weather: Weather) = viewModelScope.launch {
        favoritesDao.checkIfAlreadyAdded(weather.name)
    }*/

    fun removeCityDataFromDB(weather: Weather) = viewModelScope.launch {
        favoritesDao.deleteSpecificFavorite(weather.name)
    }

    fun deleteAllFavorites() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            favoritesDao.deleteAllFavorites()
        }
    }
}