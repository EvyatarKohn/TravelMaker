package com.evya.myweatherapp.ui.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.evya.myweatherapp.R
import com.evya.myweatherapp.model.placesmodel.Places
import com.evya.myweatherapp.model.weathermodel.Weather
import com.evya.myweatherapp.repository.PlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlacesViewModel @Inject constructor(
    private val repository: PlacesRepository,
    application: Application
) : AndroidViewModel(application) {

    private var mPlacesLiveData = MutableLiveData<Places>()
    val placesRepo: LiveData<Places>
        get() = mPlacesLiveData

    fun getMuseum(long: String, lat: String, kind: String) = viewModelScope.launch {
        repository.getWhatToDo(long, lat, kind).let { response ->
            if (response.isSuccessful) {
                mPlacesLiveData.postValue(response.body())
            } else {
                showToast(R.string.museum_request_error)
            }
        }
    }


    fun getCinemas(long: String, lat: String, kind: String) = viewModelScope.launch {
        repository.getWhatToDo(long, lat, kind).let { response ->
            if (response.isSuccessful) {
                mPlacesLiveData.postValue(response.body())
            } else {
                showToast(R.string.cinemas_request_error)
            }
        }
    }

    fun getAccommodations(long: String, lat: String, kind: String) = viewModelScope.launch {
        repository.getWhatToDo(long, lat, kind).let { response ->
            if (response.isSuccessful) {
                mPlacesLiveData.postValue(response.body())
            } else {
                showToast(R.string.accommodations_request_error)
            }
        }
    }

    fun getAdults(long: String, lat: String, kind: String) = viewModelScope.launch {
        repository.getWhatToDo(long, lat, kind).let { response ->
            if (response.isSuccessful) {
                mPlacesLiveData.postValue(response.body())
            } else {
                showToast(R.string.adults_request_error)
            }
        }
    }

    fun getAmusements(long: String, lat: String, kind: String) = viewModelScope.launch {
        repository.getWhatToDo(long, lat, kind).let { response ->
            if (response.isSuccessful) {
                mPlacesLiveData.postValue(response.body())
            } else {
                showToast(R.string.amusements_request_error)
            }
        }
    }


    fun getSports(long: String, lat: String, kind: String) = viewModelScope.launch {
        repository.getWhatToDo(long, lat, kind).let { response ->
            if (response.isSuccessful) {
                mPlacesLiveData.postValue(response.body())
            } else {
                showToast(R.string.sports_request_error)
            }
        }
    }

    fun getBanks(long: String, lat: String, kind: String) = viewModelScope.launch {
        repository.getWhatToDo(long, lat, kind).let { response ->
            if (response.isSuccessful) {
                mPlacesLiveData.postValue(response.body())
            } else {
                showToast(R.string.banks_request_error)
            }
        }
    }

    fun getFood(long: String, lat: String, kind: String) = viewModelScope.launch {
        repository.getWhatToDo(long, lat, kind).let { response ->
            if (response.isSuccessful) {
                mPlacesLiveData.postValue(response.body())
            } else {
                showToast(R.string.food_request_error)
            }
        }
    }

    fun getShops(long: String, lat: String, kind: String) = viewModelScope.launch {
        repository.getWhatToDo(long, lat, kind).let { response ->
            if (response.isSuccessful) {
                mPlacesLiveData.postValue(response.body())
            } else {
                showToast(R.string.shops_request_error)
            }
        }
    }

    fun getTransports(long: String, lat: String, kind: String) = viewModelScope.launch {
        repository.getWhatToDo(long, lat, kind).let { response ->
            if (response.isSuccessful) {
                mPlacesLiveData.postValue(response.body())
            } else {
                showToast(R.string.transports_request_error)
            }
        }
    }


    private fun showToast(RString: Int) {
        Toast.makeText(
            getApplication<Application>().applicationContext,
            getApplication<Application>().applicationContext.resources.getString(RString),
            Toast.LENGTH_LONG
        ).show()
    }

}