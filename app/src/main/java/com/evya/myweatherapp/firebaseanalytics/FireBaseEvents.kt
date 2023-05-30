package com.evya.myweatherapp.firebaseanalytics

import android.os.Bundle
import com.evya.myweatherapp.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsParamsStrings.*

class FireBaseEvents {

    companion object {
        private var firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

        fun sendFireBaseCustomEvents(event: FireBaseEventsNamesStrings, params: Bundle) {
            params.putString(PARAMS_APP_VERSION.paramsName, BuildConfig.VERSION_NAME)
            params.putInt(PARAMS_APP_BUILD_NUMBER.paramsName, BuildConfig.VERSION_CODE)

            firebaseAnalytics.logEvent(event.toString(), params)
        }
    }
}