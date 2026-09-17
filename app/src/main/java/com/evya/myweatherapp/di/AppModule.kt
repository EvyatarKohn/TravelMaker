package com.evya.myweatherapp.di

import android.app.Application
import com.android.volley.BuildConfig
import com.evya.myweatherapp.db.CitiesDB
import com.evya.myweatherapp.db.CitiesDao
import com.evya.myweatherapp.network.GeocodeApi
import com.evya.myweatherapp.network.NewWeatherApi
import com.evya.myweatherapp.network.TripApi
import com.evya.myweatherapp.network.WeatherApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
    }

    @Provides
    @Singleton
    fun provideGlobalOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level =/* if (BuildConfig.DEBUG) {*/
            HttpLoggingInterceptor.Level.BODY
        /*} else {
            HttpLoggingInterceptor.Level.NONE
        }*/
        return OkHttpClient().newBuilder().addInterceptor(httpLoggingInterceptor).build()
    }

    @Singleton
    @Provides
    fun provideWeatherRetrofit(gson: Gson, client: OkHttpClient): WeatherApi =
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
            .create(WeatherApi::class.java)

    @Singleton
    @Provides
    fun provideNewWeatherRetrofit(gson: Gson, client: OkHttpClient): NewWeatherApi =
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/3.0/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
            .create(NewWeatherApi::class.java)


    @Singleton
    @Provides
    fun provideGeoCodeRetrofit(gson: Gson, client: OkHttpClient): GeocodeApi =
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/geo/1.0/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
            .create(GeocodeApi::class.java)

    @Singleton
    @Provides
    fun provideTripRetrofit(gson: Gson, client: OkHttpClient): TripApi =
        Retrofit.Builder()
            .baseUrl("https://api.opentripmap.com/0.1/en/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
            .create(TripApi::class.java)

    @Singleton
    @Provides
    fun getCitiesDao(app: Application): CitiesDao =
        CitiesDB.getDB(app).attractionsDao()

}