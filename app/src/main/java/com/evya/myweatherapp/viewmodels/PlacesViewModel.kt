package com.evya.myweatherapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evya.myweatherapp.model.placesmodel.Places
import com.evya.myweatherapp.repository.PlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlacesViewModel @Inject constructor(
    private val repository: PlacesRepository
) : ViewModel() {

    private var mPlacesLiveData = MutableLiveData<Places>()
    val placesRepo: LiveData<Places>
        get() = mPlacesLiveData

    private var mPlacesLiveDataError = MutableLiveData<Int>()
    val placesRepoError: LiveData<Int>
        get() = mPlacesLiveDataError

    fun getWhatToDo(lat: String, long: String, kind: String, error: Int) = viewModelScope.launch {
        repository.getWhatToDo(lat, long, kind).let { response ->
            if (response.isSuccessful) {
                mPlacesLiveData.postValue(response.body())
            } else {
                mPlacesLiveDataError.postValue(error)
            }
        }
    }
}