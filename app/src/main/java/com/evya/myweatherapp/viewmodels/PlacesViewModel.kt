package com.evya.myweatherapp.viewmodels

import androidx.lifecycle.*
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

    fun getWhatToDo(long: String, lat: String, kind: String, error: Int) = viewModelScope.launch {
        repository.getWhatToDo(long, lat, kind).let { response ->
            if (response.isSuccessful) {
                mPlacesLiveData.postValue(response.body())
            } else {
                mPlacesLiveDataError.postValue(error)
            }
        }
    }
}