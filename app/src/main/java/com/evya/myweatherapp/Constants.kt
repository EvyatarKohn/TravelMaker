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
    const val INCH = " inch"
    const val MILE = " mile"
    const val FROM_TOP_ADAPTER = "fromTopAdapter"
    const val CITY_NAME = "cityName"
    const val FROM_FAVORITES = "fromFavorites"
    const val LAT = "lat"
    const val LONG = "long"
    const val RAIN = "rain"
    const val LIGHT_RAIN = "light rain"
    const val SNOW = "snow"
    const val LIGHT_SNOW = "light snow"
    const val DEFAULT_ATTRACTION_RADIUS = "1000"

    enum class AirQuality(var airQuality: String) {
        GOOD("Good"),
        FAIR("Fair"),
        MODERATE("Moderate"),
        POOR("Poor"),
        VERY_POOR("Very Poor")
    }

    fun manipulatedList(): ArrayList<String> {
        val list: ArrayList<String> = ArrayList()
        attractionList.forEach {
            list.add(it.replace("_", " "))
        }
        return list
    }

    private val attractionList = arrayListOf(
        "abandoned_mineshafts",
        "abandoned_railway_stations",
        "adult_hotels",
        "alcohol",
        "alpine_hut",
        "amphitheatres",
        "amusement_parks",
        "apartments",
        "aquariums",
        "aquatic_protected_areas",
        "aqueducts",
        "archaeological_museums",
        "art_galleries",
        "atm",
        "automobile_museums",
        "aviation_museums",
        "bakeries",
        "bank",
        "bars",
        "battlefields",
        "bell_towers",
        "bicycle_rental",
        "biergartens",
        "biographical_museums",
        "black_sand_beaches",
        "boat_sharing",
        "brothels",
        "buddhist_temples",
        "bunkers",
        "bureau_de_change",
        "cafes",
        "campsites",
        "canals",
        "canyons",
        "car_rental",
        "car_sharing",
        "car_wash",
        "casino",
        "castles",
        "cathedrals",
        "catholic_churches",
        "cave_paintings",
        "caves",
        "cemeteries",
        "charging_station",
        "children_museums",
        "children_theatres",
        "cinemas",
        "circuses",
        "climbing",
        "clock_towers",
        "computer_museums",
        "concert_halls",
        "conveniences",
        "coral_islands",
        "crater_lakes",
        "cross_country_skiing",
        "crypts",
        "dams",
        "defensive_walls",
        "desert_islands",
        "destroyed_objects",
        "dive_centers",
        "dive_spots",
        "dolmens",
        "dry_lakes",
        "eastern_orthodox_churches",
        "egyptian_temples",
        "erotic_shops",
        "factories",
        "farms",
        "fashion_museums",
        "fast_food",
        "ferris_wheels",
        "fish_stores",
        "fishing_villages",
        "food_courts",
        "footbridges",
        "fortified_towers",
        "fountains",
        "fuel",
        "gardens_and_parks",
        "geysers",
        "golden_sand_beaches",
        "guest_houses",
        "heritage_railways",
        "high_islands",
        "hillforts",
        "hindu_temples",
        "historic_districts",
        "historic_house_museums",
        "historic_object",
        "history_museums",
        "hookah",
        "hostels",
        "hot_springs",
        "inland_islands",
        "installation",
        "kitesurfing",
        "kremlins",
        "lagoons",
        "lighthouses",
        "local_museums",
        "love_hotels",
        "malls",
        "manor_houses",
        "maritime_museums",
        "marketplaces",
        "mausoleums",
        "megaliths",
        "menhirs",
        "milestones",
        "military_museums",
        "mills",
        "minarets",
        "mineshafts",
        "miniature_parks",
        "mints",
        "monasteries",
        "monuments",
        "mosques",
        "motels",
        "mountain_peaks",
        "moveable_bridges",
        "music_venues",
        "national_museums",
        "national_parks",
        "natural_monuments",
        "nature_reserves_others",
        "necropolises",
        "nightclubs",
        "nude_beaches",
        "observation_towers",
        "open_air_baths",
        "open_air_museums",
        "opera_houses",
        "other_amusement_rides",
        "other_archaeological_sites",
        "other_bathhouses",
        "other_beaches",
        "other_bridges",
        "other_buildings",
        "other_buildings_and_structures",
        "other_burial_places",
        "other_churches",
        "other_fortifications",
        "other_hotels",
        "other_islands",
        "other_lakes",
        "other_museums",
        "other_nature_conservation_areas",
        "other_technology_museums",
        "other_temples",
        "other_theatres",
        "other_towers",
        "other_winter_sports",
        "outdoor",
        "palaces",
        "picnic_site",
        "planetariums",
        "pools",
        "power_stations",
        "pubs",
        "puppetries",
        "pyramids",
        "railway_museums",
        "railway_stations",
        "red_telephone_boxes",
        "reservoirs",
        "resorts",
        "restaurants",
        "rift_lakes",
        "rivers",
        "rock_formations",
        "rocks_beaches",
        "roller_coasters",
        "roman_bridges",
        "roman_villas",
        "rune_stones",
        "salt_lakes",
        "saunas",
        "science_museums",
        "sculptures",
        "settlements",
        "shingle_beaches",
        "skiing",
        "skyscrapers",
        "springs_others",
        "squares",
        "stadiums",
        "stone_bridges",
        "strip_clubs",
        "sundials",
        "supermarkets",
        "surfing",
        "suspension_bridges",
        "sylvan_theatres",
        "synagogues",
        "thermal_baths",
        "tidal_islands",
        "tourist_object",
        "transmitter_towers",
        "triumphal_archs",
        "tumuluses",
        "unclassified_objects",
        "urbans_beaches",
        "viaducts",
        "view_points",
        "villas_and_chalet",
        "volcanoes",
        "wall_painting",
        "war_graves",
        "war_memorials",
        "watchtowers",
        "water_parks",
        "water_towers",
        "waterfalls",
        "white_sand_beaches",
        "wildlife_reserves",
        "wineries",
        "wrecks",
        "zoos",
    )
}