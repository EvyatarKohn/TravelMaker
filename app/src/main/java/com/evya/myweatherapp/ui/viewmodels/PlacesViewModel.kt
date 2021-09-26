package com.evya.myweatherapp.ui.viewmodels

import androidx.lifecycle.*
import com.evya.myweatherapp.R
import com.evya.myweatherapp.model.placesmodel.Places
import com.evya.myweatherapp.repository.PlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlacesViewModel @Inject constructor(
    private val repository: PlacesRepository,
) : ViewModel() {

    private var mPlacesLiveData = MutableLiveData<Places>()
    val placesRepo: LiveData<Places>
        get() = mPlacesLiveData

    private var mPlacesLiveDataError = MutableLiveData<Int>()
    val placesRepoError: LiveData<Int>
        get() = mPlacesLiveDataError

    fun getMuseum(long: String, lat: String, kind: String) = viewModelScope.launch {
        repository.getWhatToDo(long, lat, kind).let { response ->
            if (response.isSuccessful) {
                mPlacesLiveData.postValue(response.body())
            } else {
                mPlacesLiveDataError.postValue(R.string.museum_request_error)
            }
        }
    }


    fun getCinemas(long: String, lat: String, kind: String) = viewModelScope.launch {
        repository.getWhatToDo(long, lat, kind).let { response ->
            if (response.isSuccessful) {
                mPlacesLiveData.postValue(response.body())
            } else {
                mPlacesLiveDataError.postValue(R.string.cinemas_request_error)
            }
        }
    }

    fun getAccommodations(long: String, lat: String, kind: String) = viewModelScope.launch {
        repository.getWhatToDo(long, lat, kind).let { response ->
            if (response.isSuccessful) {
                mPlacesLiveData.postValue(response.body())
            } else {
                mPlacesLiveDataError.postValue(R.string.accommodations_request_error)
            }
        }
    }

    fun getAdults(long: String, lat: String, kind: String) = viewModelScope.launch {
        repository.getWhatToDo(long, lat, kind).let { response ->
            if (response.isSuccessful) {
                mPlacesLiveData.postValue(response.body())
            } else {
                mPlacesLiveDataError.postValue(R.string.adults_request_error)
            }
        }
    }

    fun getAmusements(long: String, lat: String, kind: String) = viewModelScope.launch {
        repository.getWhatToDo(long, lat, kind).let { response ->
            if (response.isSuccessful) {
                mPlacesLiveData.postValue(response.body())
            } else {
                mPlacesLiveDataError.postValue(R.string.amusements_request_error)
            }
        }
    }


    fun getSports(long: String, lat: String, kind: String) = viewModelScope.launch {
        repository.getWhatToDo(long, lat, kind).let { response ->
            if (response.isSuccessful) {
                mPlacesLiveData.postValue(response.body())
            } else {
                mPlacesLiveDataError.postValue(R.string.sports_request_error)
            }
        }
    }

    fun getBanks(long: String, lat: String, kind: String) = viewModelScope.launch {
        repository.getWhatToDo(long, lat, kind).let { response ->
            if (response.isSuccessful) {
                mPlacesLiveData.postValue(response.body())
            } else {
                mPlacesLiveDataError.postValue(R.string.banks_request_error)
            }
        }
    }

    fun getFood(long: String, lat: String, kind: String) = viewModelScope.launch {
        repository.getWhatToDo(long, lat, kind).let { response ->
            if (response.isSuccessful) {
                mPlacesLiveData.postValue(response.body())
            } else {
                mPlacesLiveDataError.postValue(R.string.food_request_error)
            }
        }
    }

    fun getShops(long: String, lat: String, kind: String) = viewModelScope.launch {
        repository.getWhatToDo(long, lat, kind).let { response ->
            if (response.isSuccessful) {
                mPlacesLiveData.postValue(response.body())
            } else {
                mPlacesLiveDataError.postValue(R.string.shops_request_error)
            }
        }
    }

    fun getTransports(long: String, lat: String, kind: String) = viewModelScope.launch {
        repository.getWhatToDo(long, lat, kind).let { response ->
            if (response.isSuccessful) {
                mPlacesLiveData.postValue(response.body())
            } else {
                mPlacesLiveDataError.postValue(R.string.transports_request_error)
            }
        }
    }
}