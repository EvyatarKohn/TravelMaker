package com.evya.myweatherapp.firebaseanalytics

enum class FireBaseEventsParamsStrings(val paramsName: String) {
    // Global parameters
    PARAMS_APP_BUILD_NUMBER("app_build_number"),
    PARAMS_APP_VERSION("app_version"),
    PARAMS_OS_VERSION("os_version"),
    PARAMS_DEVICE("device_id"),
    PARAMS_SDK_VERSION("sdk_version"),
    PARAMS_BRAND("brand"),
    PARAMS_MODEL("model"),
    PARAMS_PRODUCT("product"),
    PARAMS_LANGUAGE("language"),

    // Local parameters
    PARAMS_CITY_NAME("city_name"),
    PARAMS_WHAT_TO_DO("what_to_do"),
    PARAMS_NAVIGATE_TO("navigate_to"),
    PARAMS_TEMPERATURE_UNITS("temperature_units"),
    PARAMS_FAILED_TO_LOAD_BANNER_AD("failed_to_load_banner_ad"),
    PARAMS_FAILED_TO_LOAD_INTERSTITIAL_AD("failed_to_load_interstitial_ad"),
    PARAMS_ALERT("alert"),
    PARAMS_ALERT_DESCRIPTION("alert_description"),
    PARAMS_CLICKED_ATTRACTION("clicked_attraction"),
}