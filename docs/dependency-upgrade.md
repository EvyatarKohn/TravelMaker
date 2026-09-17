# Dependency upgrade — September 2026

Versions were checked against Google Maven, Maven Central, Gradle's release service, and the chip-navigation-bar release page.

| Component | Version |
| --- | --- |
| Gradle wrapper | 9.7.1 |
| Android Gradle Plugin | 9.4.0 |
| Kotlin | 2.4.20 |
| Compile / target SDK | 37 / 37 (Android 17) |
| Minimum SDK | 26 |
| Java source / target | 17 |
| AndroidX Core | 1.19.0 |
| AppCompat | 1.8.0 |
| ConstraintLayout | 2.2.2 |
| RecyclerView | 1.4.0 |
| SwipeRefreshLayout | 1.2.0 |
| Fragment | 1.9.0 |
| Lifecycle | 2.11.0 |
| Navigation | 2.10.1 |
| Room | 2.8.5 |
| Material Components | 1.14.0 |
| Lottie | 6.7.1 |
| Chip Navigation Bar | 1.4.0 |
| Hilt (plugin, runtime, compiler, testing) | 2.60.1 |
| Coroutines (runtime and tests) | 1.11.0 |
| Retrofit / Gson converter | 3.0.0 |
| Gson | 2.14.0 |
| OkHttp logging interceptor | 5.5.0 |
| Google Maps | 20.0.0 |
| Google Location | 21.4.0 |
| Google Places | 5.3.0 |
| Google Mobile Ads | 25.4.0 |
| Firebase BoM | 34.19.0 |
| Google Services plugin | 4.5.0 |
| Firebase Crashlytics plugin | 3.0.8 |
| Core library desugaring | 2.1.5 |
| AndroidX Test JUnit | 1.3.0 |
| Espresso | 3.7.0 |
| JUnit / Architecture core testing | 4.13.2 / 2.2.0 (already current) |

## Migration details

- AGP supplies built-in Kotlin support. Data Binding still requires annotation processing, so the project uses AGP's `com.android.legacy-kapt` plugin instead of the incompatible standalone Kotlin kapt plugin.
- Firebase Analytics, Authentication, Firestore, and Crashlytics use their main modules and BoM-managed versions. Retired standalone Firebase KTX dependencies and duplicate declarations were removed.
- Removed unused legacy Places, lifecycle-extensions, pre-AndroidX Room compiler, AndroidX Hilt compiler, Maps server/utilities libraries, and the unused secrets plugin. Removed the duplicate Ads Lite dependency.
- Places uses `DISPLAY_NAME` / `displayName` instead of the fields removed in SDK 5.
- System-bar, cutout, and keyboard insets are handled for the higher target SDK.
- Location permissions request precise and approximate access together and accept approximate permission.
- Gradle distribution integrity is checked using the published SHA-256 checksum.
- Tab switches navigate explicitly and clear the previous tab's back stack; reassigning the same graph no longer switches destinations in current Navigation.
- Attraction search waits for the full-screen ad dismissal to finish before presenting results.
- Attraction results open in a dedicated `AttractionMapActivity` backed by `MapView`. This keeps the results screen outside the Navigation fragment manager and removes the `FragmentNavigator.kt:236` failure path.
- OpenTripMap's optional `osm` and `wikidata` fields are nullable, matching real API responses and preventing Parcelable restoration failures.
- Reverse geocoding runs off the main thread and treats service failures as recoverable, avoiding UI stalls and platform geocoder crashes.
- Removed the obsolete Ads services manifest override, whose referenced resource no longer exists in the updated Ads SDK.

## Validation

- Debug app and instrumentation APKs build successfully.
- All five JVM tests and three Room database tests pass; database tests ran on the Android 17 emulator with a 16 KB page-size image.
- Android lint reports zero errors. Existing warnings include deprecated APIs, accessibility, and resource cleanup suggestions.
- Android 17 smoke checks passed for weather loading with approximate location, switching between Attractions, Favorites, and Maps, opening a Hotels result map with markers, returning from the map, and readable system bars with content outside the status/navigation insets.

## References

- [AGP 9.4 compatibility](https://developer.android.com/build/releases/agp-9-4-0-release-notes)
- [Built-in Kotlin migration](https://developer.android.com/build/migrate-to-built-in-kotlin)
- [Firebase Android releases and KTX migration](https://firebase.google.com/support/release-notes/android)
- [Places Android release notes](https://developers.google.com/maps/documentation/places/android-sdk/release-notes)
- [Android 17 target behavior](https://developer.android.com/about/versions/17/behavior-changes-17)
