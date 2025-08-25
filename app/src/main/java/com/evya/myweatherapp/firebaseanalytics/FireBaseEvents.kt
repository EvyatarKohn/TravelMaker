package com.evya.myweatherapp.firebaseanalytics

import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import com.evya.myweatherapp.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsParamsStrings.*
import java.util.Locale

class FireBaseEvents {

    companion object {
        private var firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

        fun sendFireBaseCustomEvents(event: String, params: Bundle) {
            params.putString(PARAMS_APP_VERSION.paramsName, BuildConfig.VERSION_NAME)
            params.putInt(PARAMS_APP_BUILD_NUMBER.paramsName, BuildConfig.VERSION_CODE)
            params.putString(PARAMS_OS_VERSION.paramsName, System.getProperty("os.version"))
            params.putString(PARAMS_DEVICE.paramsName, Build.DEVICE)
            params.putString(PARAMS_SDK_VERSION.paramsName, Build.VERSION.RELEASE)
            params.putString(PARAMS_BRAND.paramsName, Build.BRAND)
            params.putString(PARAMS_MODEL.paramsName, Build.MODEL)
            params.putString(PARAMS_PRODUCT.paramsName, Build.PRODUCT)
            params.putString(PARAMS_LANGUAGE.paramsName, Locale.getDefault().displayLanguage)

            firebaseAnalytics.logEvent(event, params)
        }
    }
}