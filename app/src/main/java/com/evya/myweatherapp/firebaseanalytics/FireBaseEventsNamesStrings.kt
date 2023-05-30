package com.evya.myweatherapp.firebaseanalytics

enum class FireBaseEventsNamesStrings(val eventName: String) {
    CHOOSE_CITY_FROM_TOP_ADAPTER("choose_city_from_top_adapter"),
    CHOOSE_CITY_FROM_FAVORITES("choose_city_from_favorites"),
    DELETE_CITY_FROM_FAVORITES("delete_city_from_favorites"),
    DELETE_ALL_CITIES_FROM_FAVORITES("delete_all_cities_from_favorites"),
    NAVIGATE_TO_WEATHER("navigate_to_weather"),
    CHANGE_TEMP_UNITS("change_temp_units"),
    NAVIGATE_TO_GOOGLE_MAP("navigate_to_google_map"),
    SEARCH_IN_GOOGLE_MAP("search_in_google_map"),
    SHOW_WEATHER("show_weather"),
    NAVIGATE_TO_ATTRACTIONS("navigate_to_attractions"),
    NAVIGATE_TO_FAVORITES("navigate_to_favorites"),
    NAVIGATE_TO_SHOW_INFO("navigate_to_show_info"),
    SEARCH_ATTRACTIONS("search_attractions"),
    WHAT_TO_DO("what_to_do"),
    CLICK_ON_AD("click_on_ad"),
}