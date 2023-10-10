package com.evya.myweatherapp.viewmodels

import androidx.lifecycle.*
import com.evya.myweatherapp.model.weathermodel.Weather
import com.evya.myweatherapp.repository.FavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: FavoritesRepository
) : ViewModel() {

    private lateinit var mWeather: Weather
    fun setWeather(weather: Weather) {
        mWeather = weather
    }

    val fetchAllCitiesFromDB: LiveData<List<Weather>>
        get() = repository.fetchAllCitiesFromDB.flowOn(Dispatchers.IO)
            .asLiveData(context = viewModelScope.coroutineContext)

    fun addCityDataToDB(weather: Weather) = viewModelScope.launch {
        repository.addCityDataToDB(weather)
    }

    val checkIfAlreadyAddedToDB: LiveData<Boolean>
        get() = repository.checkIfAlreadyAddedToDB(mWeather.cityName).flowOn(Dispatchers.IO)
            .asLiveData(context = viewModelScope.coroutineContext)

    fun removeCityDataFromDB(cityName: String) = viewModelScope.launch {
        repository.removeCityDataFromDB(cityName)
    }

    fun deleteAllFavoritesFromDB() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            repository.deleteAllFavorites()
        }
    }
}