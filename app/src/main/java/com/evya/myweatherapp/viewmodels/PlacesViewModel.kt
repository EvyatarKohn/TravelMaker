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

    private var mPlacesLiveData = MutableLiveData<Pair<Places?, Int?>>()
    val placesRepo: LiveData<Pair<Places?, Int?>>
        get() = mPlacesLiveData


    fun getWhatToDo(lat: String, long: String, kind: String, error: Int) = viewModelScope.launch {
        repository.getWhatToDo(lat, long, kind).let { response ->
            if (response.isSuccessful) {
                mPlacesLiveData.postValue(Pair(response.body(), null))
            } else {
                mPlacesLiveData.postValue(Pair(null, error))
            }
        }
    }
}