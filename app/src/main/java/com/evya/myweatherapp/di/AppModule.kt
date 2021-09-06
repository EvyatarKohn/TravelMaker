package com.evya.myweatherapp.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.evya.myweatherapp.network.MainApi
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

    private var logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private val client =OkHttpClient.Builder().addInterceptor(logging).build()

    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
    }

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson):MainApi =
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
            .create(MainApi::class.java)
}