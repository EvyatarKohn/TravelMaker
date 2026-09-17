# Travelmaker

An Android app for local weather, nearby attractions, maps, and saved cities.

## Build

Open the project in Android Studio and select **JDK 17** for the Gradle JVM.
The project uses Gradle 9.7.1, Android Gradle Plugin 9.4.0, Kotlin 2.4.20, and Android SDK 37.
Configure your Android SDK path in `local.properties`, then sync Gradle.

From a terminal with `JAVA_HOME` pointing to JDK 17:

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

## Main flows

- **Weather:** local conditions, forecasts, and a heart button to save a city.
- **Maps:** choose another destination and open its weather.
- **Attractions:** browse nine categories or search the activity suggestions. Search distance is saved across launches. Searches show loading, cancel, empty, and retry states.
- **Favorites:** search saved cities, open their weather, or remove individual cities or all favorites with confirmation.

The app uses Kotlin, XML layouts, Navigation, Hilt, Retrofit, Room, and LiveData.

## Regression checks

Unit tests cover attraction-search network and HTTP failures, retries with the selected radius, duplicate requests, cancellation, and consuming results before returning from the map.

`FavoritesDatabaseTest` runs against an in-memory Room database on Android. It checks missing cities, deleting the last favorite, and preserving non-favorite cached cities when removing all favorites.

For a device smoke test:

1. Launch with location permission, then switch tabs while startup completes.
2. Save a city from Weather, filter it in Favorites, clear the search, and delete the last favorite.
3. Change the attraction distance, leave the screen, and reopen it.
4. Search while offline; confirm that the controls recover and retry is available.
5. Open attraction results and go back; the search screen should stay open.

See [the dependency upgrade notes](docs/dependency-upgrade.md) for library versions and migration details.
