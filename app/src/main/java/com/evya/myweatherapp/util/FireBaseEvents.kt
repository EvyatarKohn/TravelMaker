package com.evya.myweatherapp.util

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class FireBaseEvents {

    companion object {
        private var firebaseAnalytics: FirebaseAnalytics = Firebase.analytics
        
        fun sendFireBaseCustomEvents(event: FirebaseEventsStrings) {
            firebaseAnalytics.logEvent(event.toString(), null)
        }

        fun sendFireBaseCustomEvents(event: String) {
            firebaseAnalytics.logEvent(event, null)
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
        NatureAttraction,
        ShowAd,
        CloseAd,
        ClickOnAd,
        FailedToLoadAd
    }
}