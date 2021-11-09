package com.evya.myweatherapp.util

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class FireBaseEvents {

    companion object {
        private lateinit var mFirebaseAnalytics: FirebaseAnalytics

        fun init() {
            // Obtain the FirebaseAnalytics instance.
            mFirebaseAnalytics = Firebase.analytics
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
        MoveToWeather,
        ChangeTempUnits,
        MoveToGoogleMap,
        SearchInGoogleMap,
        ShowWeather,
        MoveToAttractions,
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