package com.evya.myweatherapp.viewmodels

import androidx.lifecycle.*
import com.evya.myweatherapp.model.weathermodel.Weather
import com.evya.myweatherapp.repository.FavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: FavoritesRepository
) : ViewModel() {

    private var mCityName: String? = null
    fun setCityName(cityName: String?) {
        mCityName = cityName
    }

    val fetchAllCitiesFromDB: LiveData<List<Weather>>
        get() = repository.fetchAllCitiesFromDB.flowOn(Dispatchers.IO)
            .asLiveData(context = viewModelScope.coroutineContext)

    fun addCityDataToDB(weather: Weather) = viewModelScope.launch {
        repository.addCityDataToDB(weather)
    }

//    val fetchSpecificCity = repository.fetchSpecificCity(mCityName)

    suspend fun fetchSpecificCity(cityName: String) = repository.fetchSpecificCity(cityName)

/*    val checkIfAlreadyAddedToDB: LiveData<Boolean>
        get() = repository.checkIfAlreadyAddedToDB(mCityName).flowOn(Dispatchers.IO)
            .asLiveData(context = viewModelScope.coroutineContext)*/

    val checkIfAlreadyInFav: LiveData<Boolean>
        get() = repository.checkIfAlreadyInFav(mCityName).flowOn(Dispatchers.IO)
            .asLiveData(context = viewModelScope.coroutineContext)

    fun removeCityDataFromDB(cityName: String) = viewModelScope.launch {
        repository.removeCityDataFromDB(cityName)
    }

    fun deleteAllFavorite(isInFavorites: Boolean) = viewModelScope.launch {
        repository.deleteAllFavorite(isInFavorites)
    }


    fun nukeTable() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            repository.nukeTable()
        }
    }

    fun updateFavorites(isInFavorites: Boolean, cityName: String) = CoroutineScope(Dispatchers.IO).launch {
        repository.updateFavorites(isInFavorites, cityName)
    }
}