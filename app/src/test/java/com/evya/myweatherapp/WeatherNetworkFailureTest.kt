package com.evya.myweatherapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.evya.myweatherapp.network.NewWeatherApi
import com.evya.myweatherapp.network.GeocodeApi
import com.evya.myweatherapp.repository.NewWeatherRepository
import com.evya.myweatherapp.repository.GeoCodeRepository
import com.evya.myweatherapp.viewmodels.NewWeatherViewModel
import com.evya.myweatherapp.viewmodels.weatherRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.*
import org.junit.Assert.*
import retrofit2.Response
import java.net.UnknownHostException
import java.net.SocketTimeoutException

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherNetworkFailureTest {
    @get:Rule val instantExecutor = InstantTaskExecutorRule()
    private val dispatcher = StandardTestDispatcher()
    @Before fun setUp() { Dispatchers.setMain(dispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    private val offlineWeather = object : NewWeatherApi {
        override suspend fun getWeatherByLocation(lat: String, lon: String, units: String, appid: String): Response<com.evya.myweatherapp.model.weathermodel.Weather> = throw UnknownHostException()
        override suspend fun getWeatherForSpecificDay(lat: String, lon: String, date: String, units: String, appid: String): Response<com.evya.myweatherapp.model.dailyweathermodel.DailyWeather> = throw UnknownHostException()
        override suspend fun getWeatherForTimeMachine(lat: String, lon: String, time: Int, units: String, appid: String): Response<com.evya.myweatherapp.model.timemachinemodel.TimeMachineWeather> = throw UnknownHostException()
    }
    private val offlineGeocode = object : GeocodeApi {
        override suspend fun getCityNameByLocation(lat: String, lon: String, appid: String): Response<com.evya.myweatherapp.model.geocode.GeoCode> = throw UnknownHostException()
    }

    @Test fun dnsFailuresReachEveryWeatherObserverWithoutCrashing() = runTest(dispatcher) {
        val model = NewWeatherViewModel(NewWeatherRepository(offlineWeather),
            GeoCodeRepository(offlineGeocode))
        model.getWeatherByLocation("32", "34", "metric")
        model.getWeatherForSpecificDay("32", "34", "2026-09-17", "metric")
        model.getCityNameByLocation("32", "34")
        runCurrent()
        assertEquals(R.string.weather_connection_error, model.weatherData.value?.second)
        assertEquals(R.string.weather_connection_error, model.dailyWeatherData.value?.second)
        assertEquals(R.string.weather_connection_error, model.cityNameData.value?.second)
    }

    @Test fun timeoutCanBeFollowedBySuccessfulRetry() = runTest {
        assertEquals(null to R.string.weather_connection_error,
            weatherRequest<String>(123) { throw SocketTimeoutException() })
        assertEquals("fresh" to null, weatherRequest(123) { Response.success("fresh") })
    }

    @Test fun httpAndEmptyResponsesRemainErrors() = runTest {
        assertEquals(null to 123, weatherRequest<String>(123) { Response.error(503, "".toResponseBody()) })
        assertEquals(null to 123, weatherRequest<String>(123) { Response.success(null) })
    }

    @Test fun cancellationIsNeverConvertedIntoConnectionError() = runTest {
        try {
            weatherRequest<String>(123) { throw CancellationException("cancelled") }
            fail("Cancellation must propagate")
        } catch (_: CancellationException) { }
    }
}
