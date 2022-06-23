package com.evya.myweatherapp.util

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics

class FireBaseEvents {

    companion object {
        private lateinit var mFirebaseAnalytics: FirebaseAnalytics

        fun init(context: Context) {
            // Obtain the FirebaseAnalytics instance.
            FirebaseApp.initializeApp(context)
//            mFirebaseAnalytics = Firebase.analytics
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(context)

        }

        fun sendFireBaseCustomEvents(event: FirebaseEventsStrings) {
            mFirebaseAnalytics.logEvent(event.toString(), null)
        }

        fun sendFireBaseCustomEvents(event: String) {
            mFirebaseAnalytics.logEvent(event, null)
        }
    }

    enum class FirebaseEventsStrings {
        ChooseCityFromTopAdapter,
        ChooseCityFromFavorites,
        DeleteCityFromFavorites,
        DeleteAllCitiesFromFavorites,
        MoveToWeather,
        ChangeTempUnits,
        MoveToGoogleMap,
        SearchInGoogleMap,
        ShowWeather,
        MoveToAttractions,
        MoveToFavorites,
        ShowInfo,
        SearchAttractions,
        HotelAttractions,
        NightLifeAttractions,
        TransportAttraction,
        BanksAttraction,
        FoodAttraction,
        MuseumsAttraction,
        HistoricAttraction,
        CulturalAttraction,
        NatureAttraction
    }
}