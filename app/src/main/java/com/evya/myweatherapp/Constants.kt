package com.evya.myweatherapp

object Constants {

    const val THREE_SEC = 3000L
    const val PERMISSIONS_REQUEST_ID = 1000
    const val REQUEST_CODE_LOCATION_SETTING = 100
    const val IMPERIAL = "imperial"
    const val METRIC = "metric"
    const val IMPERIAL_DEGREE = " miles/hr"
    const val METRIC_DEGREE = " m/s"
    const val KM = " Km"
    const val MM = " mm"
    const val MILE = " mile"
    const val FROM_ADAPTER = "fromTopAdapter"
    const val CITY_NAME = "cityName"
    const val FROM_FAVORITES = "fromFavorites"
    const val LAT = "lat"
    const val LONG = "long"
    const val RAIN = "rain"
    const val LIGHT_RAIN = "light rain"
    const val SNOW = "snow"
    const val LIGHT_SNOW = "light snow"

    enum class Pollution(var pollution: String) {
        GOOD("good"),
        FAIR("Fair"),
        MODERATE("Moderate"),
        POOR("Poor"),
        VERY_POOR("Very Poor")
    }

    fun replacedList(): ArrayList<String> {
        val list: ArrayList<String> = ArrayList()
        attractionList.forEach {
            list.add(it.replace("_", " "))
        }

        return list
    }

    private val attractionList = arrayListOf(
        "alpine_hut",
        "apartments",
        "campsites",
        "guest_houses",
        "hostels",
        "other_hotels",
        "love_hotels",
        "motels",
        "resorts",
        "villas_and_chalet",
        "alcohol",
        "brothels",
        "casino",
        "erotic_shops",
        "hookah",
        "adult_hotels",
        "nightclubs",
        "strip_clubs",
        "amusement_parks",
        "other_bathhouses",
        "open_air_baths",
        "saunas",
        "thermal_baths",
        "ferris_wheels",
        "miniature_parks",
        "other_amusement_rides",
        "roller_coasters",
        "water_parks",
        "aqueducts",
        "footbridges",
        "moveable_bridges",
        "other_bridges",
        "roman_bridges",
        "stone_bridges",
        "suspension_bridges",
        "viaducts",
        "amphitheatres",
        "other_buildings_and_structures",
        "destroyed_objects",
        "farms",
        "manor_houses",
        "palaces",
        "pyramids",
        "triumphal_archs",
        "wineries",
        "lighthouses",
        "skyscrapers",
        "bell_towers",
        "clock_towers",
        "minarets",
        "observation_towers",
        "other_towers",
        "transmitter_towers",
        "watchtowers",
        "water_towers",
        "aquariums",
        "archaeological_museums",
        "art_galleries",
        "biographical_museums",
        "children_museums",
        "fashion_museums",
        "historic_house_museums",
        "history_museums",
        "local_museums",
        "military_museums",
        "automobile_museums",
        "aviation_museums",
        "computer_museums",
        "heritage_railways",
        "maritime_museums",
        "other_technology_museums",
        "railway_museums",
        "national_museums",
        "open_air_museums",
        "other_museums",
        "planetariums",
        "science_museums",
        "zoos",
        "circuses",
        "concert_halls",
        "cinemas",
        "music_venues",
        "opera_houses",
        "other_theatres",
        "puppetries",
        "sylvan_theatres",
        "children_theatres",
        "fountains",
        "gardens_and_parks",
        "installation",
        "sculptures",
        "squares",
        "wall_painting",
        "other_archaeological_sites",
        "cave_paintings",
        "megaliths",
        "menhirs",
        "roman_villas",
        "rune_stones",
        "settlements",
        "cemeteries",
        "crypts",
        "dolmens",
        "mausoleums",
        "necropolises",
        "other_burial_places",
        "tumuluses",
        "war_graves",
        "war_memorials",
        "bunkers",
        "castles",
        "defensive_walls",
        "fortified_towers",
        "hillforts",
        "kremlins",
        "other_fortifications",
        "battlefields",
        "fishing_villages",
        "historic_districts",
        "milestones",
        "monuments",
        "abandoned_mineshafts",
        "abandoned_railway_stations",
        "dams",
        "factories",
        "mills",
        "mineshafts",
        "mints",
        "other_buildings",
        "power_stations",
        "railway_stations",
        "black_sand_beaches",
        "golden_sand_beaches",
        "nude_beaches",
        "other_beaches",
        "rocks_beaches",
        "shingle_beaches",
        "urbans_beaches",
        "white_sand_beaches",
        "canyons",
        "caves",
        "mountain_peaks",
        "rock_formations",
        "volcanoes",
        "coral_islands",
        "desert_islands",
        "high_islands",
        "inland_islands",
        "other_islands",
        "tidal_islands",
        "geysers",
        "hot_springs",
        "springs_others",
        "aquatic_protected_areas",
        "national_parks",
        "natural_monuments",
        "other_nature_conservation_areas",
        "nature_reserves_others",
        "wildlife_reserves",
        "canals",
        "crater_lakes",
        "dry_lakes",
        "lagoons",
        "other_lakes",
        "reservoirs",
        "rift_lakes",
        "rivers",
        "salt_lakes",
        "waterfalls",
        "red_telephone_boxes",
        "sundials",
        "historic_object",
        "tourist_object",
        "unclassified_objects",
        "view_points",
        "buddhist_temples",
        "cathedrals",
        "catholic_churches",
        "eastern_orthodox_churches",
        "other_churches",
        "egyptian_temples",
        "hindu_temples",
        "monasteries",
        "mosques",
        "other_temples",
        "synagogues",
        "climbing",
        "dive_centers",
        "dive_spots",
        "wrecks",
        "kitesurfing",
        "pools",
        "stadiums",
        "surfing",
        "cross_country_skiing",
        "other_winter_sports",
        "skiing",
        "atm",
        "bank",
        "bureau_de_change",
        "bars",
        "biergartens",
        "cafes",
        "fast_food",
        "food_courts",
        "picnic_site",
        "pubs",
        "restaurants",
        "bakeries",
        "conveniences",
        "fish_stores",
        "malls",
        "marketplaces",
        "outdoor",
        "supermarkets",
        "bicycle_rental",
        "boat_sharing",
        "car_rental",
        "car_sharing",
        "car_wash",
        "charging_station",
        "fuel"
    )
}