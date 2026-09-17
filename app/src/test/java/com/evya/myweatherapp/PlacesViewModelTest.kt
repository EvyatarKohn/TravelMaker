package com.evya.myweatherapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.evya.myweatherapp.model.placesmodel.*
import com.evya.myweatherapp.network.TripApi
import com.evya.myweatherapp.repository.PlacesRepository
import com.evya.myweatherapp.viewmodels.PlacesSearchState
import com.evya.myweatherapp.viewmodels.PlacesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.*
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class PlacesViewModelTest {
    @get:Rule val instantExecutor = InstantTaskExecutorRule()
    private val dispatcher = StandardTestDispatcher()
    private val api = FakeTripApi()
    private lateinit var model: PlacesViewModel

    @Before fun setUp() {
        Dispatchers.setMain(dispatcher)
        MainData.attractionRadius = "1000"
        model = PlacesViewModel(PlacesRepository(api))
    }

    @After fun tearDown() {
        model.cancel()
        Dispatchers.resetMain()
    }

    @Test fun networkFailureCanBeRetriedWithoutLosingQuery() = runTest(dispatcher) {
        api.response = { throw IOException("Offline") }
        model.search("32.1", "34.8", "museums")
        assertEquals(PlacesSearchState.Loading, model.state.value)
        runCurrent()
        assertEquals(PlacesSearchState.Error, model.state.value)
        api.response = { Response.success(Places(emptyList(), "FeatureCollection")) }
        MainData.attractionRadius = "5000"
        model.retry()
        runCurrent()
        assertEquals(PlacesSearchState.Empty, model.state.value)
        assertEquals(listOf("5000", "34.8", "32.1", "museums"), api.lastQuery)
        assertEquals(2, api.calls)
    }

    @Test fun unsuccessfulAndMissingResponsesBecomeRecoverableErrors() = runTest(dispatcher) {
        api.response = { Response.error(503, "Unavailable".toResponseBody()) }
        model.search("32", "34", "foods")
        runCurrent()
        assertEquals(PlacesSearchState.Error, model.state.value)
        api.response = { Response.success(null) }
        model.retry()
        runCurrent()
        assertEquals(PlacesSearchState.Error, model.state.value)
    }

    @Test fun repeatedTapsDoNotStartConcurrentRequestsAndCancelAllowsAnotherSearch() = runTest(dispatcher) {
        api.response = { awaitCancellation() }
        model.search("32", "34", "foods")
        model.search("32", "34", "museums")
        runCurrent()
        assertEquals(1, api.calls)
        model.cancel()
        runCurrent()
        assertEquals(PlacesSearchState.Idle, model.state.value)
        api.response = { Response.success(Places(emptyList(), "FeatureCollection")) }
        model.search("32", "34", "natural")
        runCurrent()
        assertEquals(2, api.calls)
        assertEquals(PlacesSearchState.Empty, model.state.value)
    }

    @Test fun successfulResultsAreConsumedBeforeReturningFromMap() = runTest(dispatcher) {
        val places = Places(listOf(Feature(
            Geometry(listOf(34.8, 32.1), "Point"), "1",
            Properties(10.0, "museums", "Museum", "", 1, "", "1"), "Feature"
        )), "FeatureCollection")
        api.response = { Response.success(places) }
        model.search("32", "34", "museums")
        runCurrent()
        assertEquals(PlacesSearchState.Success(places), model.state.value)
        model.consumeResult()
        assertEquals(PlacesSearchState.Idle, model.state.value)
    }

    private class FakeTripApi : TripApi {
        var calls = 0
        var lastQuery = emptyList<String>()
        var response: suspend () -> Response<Places> = { Response.success(Places(emptyList(), "")) }
        override suspend fun getWhatToDo(radius: String, long: String, lat: String, kinds: String, apiKey: String): Response<Places> {
            calls++
            lastQuery = listOf(radius, long, lat, kinds)
            return response()
        }
    }
}
