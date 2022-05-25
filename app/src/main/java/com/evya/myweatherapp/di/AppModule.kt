package com.evya.myweatherapp.di

import android.app.Application
import com.evya.myweatherapp.db.FavoritesDB
import com.evya.myweatherapp.db.FavoritesDao
import com.evya.myweatherapp.network.TripApi
import com.evya.myweatherapp.network.WeatherApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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

    @Singleton
    @Provides
    fun provideWeatherRetrofit(gson: Gson): WeatherApi =
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(WeatherApi::class.java)

    @Singleton
    @Provides
    fun provideTripRetrofit(gson: Gson): TripApi =
        Retrofit.Builder()
            .baseUrl("https://api.opentripmap.com/0.1/en/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(TripApi::class.java)

    @Singleton
    @Provides
    fun getFavoritesDao(app: Application): FavoritesDao =
        FavoritesDB.getDB(app).attractionsDao()

}