package com.evya.myweatherapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evya.myweatherapp.model.placesmodel.Places
import com.evya.myweatherapp.repository.PlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PlacesSearchState {
    object Idle : PlacesSearchState()
    object Loading : PlacesSearchState()
    object Empty : PlacesSearchState()
    object Error : PlacesSearchState()
    data class Success(val places: Places) : PlacesSearchState()
}

@HiltViewModel
class PlacesViewModel @Inject constructor(private val repository: PlacesRepository) : ViewModel() {
    private val mutableState = MutableLiveData<PlacesSearchState>(PlacesSearchState.Idle)
    val state: LiveData<PlacesSearchState> = mutableState
    private var searchJob: Job? = null
    private var lastSearch: Triple<String, String, String>? = null

    fun search(lat: String, long: String, kind: String) {
        if (mutableState.value == PlacesSearchState.Loading) return
        lastSearch = Triple(lat, long, kind)
        mutableState.value = PlacesSearchState.Loading
        searchJob = viewModelScope.launch {
            try {
                val response = repository.getWhatToDo(lat, long, kind)
                val places = response.body()
                mutableState.value = when {
                    !response.isSuccessful || places == null -> PlacesSearchState.Error
                    places.features.isEmpty() -> PlacesSearchState.Empty
                    else -> PlacesSearchState.Success(places)
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableState.value = PlacesSearchState.Error
            }
        }
    }

    fun retry() {
        lastSearch?.let { (lat, long, kind) -> search(lat, long, kind) }
    }

    fun cancel() {
        searchJob?.cancel()
        mutableState.value = PlacesSearchState.Idle
    }

    fun consumeResult() {
        if (mutableState.value is PlacesSearchState.Success) mutableState.value = PlacesSearchState.Idle
    }
}
