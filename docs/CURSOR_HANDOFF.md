# TravelMaker improvement handoff

User request: improve the Android weather/travel app, including UI, weather-aware outings, attractions, saved trips, widgets and optional notifications. Work within the available Codex allowance and leave a buildable checkpoint for Cursor. Do not install on a physical phone without explicit authorization.

## Implemented in this checkpoint

- Weather dashboard (hero, hourly, daily, metrics, outdoor window, pull-to-refresh, cache messaging).
- Day plan (local SharedPreferences itinerary), attraction map filters/actions, weather widget.
- Map selection / Places LOCATION field fixes; crash and RecyclerView fixes preserved.
- **Temperature units:** `Weather.responseUnits`, Room v11 migration, latest-wins ViewModel jobs, dashboard toggle sync.
- **English UI locale:** day names, relative “Updated …”, alert times, geocoder, day-plan dates use `Locale.ENGLISH` (device may still be Hebrew).
- **Weather-aware attractions:** `AttractionWeather.kt` indoor/outdoor classification; discover tip banner; results sorted indoor-first when outdoor window is poor/alerts/stormy.
- **Day-plan forecast:** when a date is set, loads One Call `day_summary` for high/low and rain (needs location from weather tab).
- **Units preference:** `UnitPrefs` persists ℃/℉ across process death.
- **Widget refresh:** `WeatherWidgetWorker` via WorkManager ~every 6 hours when online (stores lat/lon/units from last publish).
- **Dark theme pass:** main shell, maps chrome, spinner use `canvas` / `card_surface` / night `grey_f7`; night palette already covered dashboard tokens. Some legacy dialogs still use hardcoded white.

## Remaining scope

- Device/visual QA (emulator shell was unreliable earlier). No physical-phone install without authorization.
- Multiple named trips; routed itineraries / travel times.
- Attraction photos/details (only with a supported provider already available).
- Opt-in rain/alert notifications (permissions + settings UI).
- Draggable map detail sheet, search-this-map-area.
- Finish remaining dialog/legacy layouts for dark mode; DailyDialog still uses `MainData.degreesUnits` for labels.
- Onboarding / skeleton loading / bottom-nav redesign (not started).

## Verification status

- Unit tests including `AttractionWeatherTest` / outdoor / day-plan / units: `:app:testDebugUnitTest` passed with `:app:assembleDebug` (this milestone).
- Lint not re-run on this milestone; prior lintDebug succeeded with warnings only.
- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`.
- Runtime/device QA still pending.

## Build

```powershell
$env:JAVA_HOME = 'C:\Users\evyatar-c\.jdks\jbr-17.0.14'
$env:JAVA_TOOL_OPTIONS = '-Djdk.net.unixdomain.tmpdir=C:\Users\evyatar-c\Documents\TravelMaker'
.\gradlew.bat --no-daemon :app:testDebugUnitTest :app:assembleDebug :app:lintDebug --console=plain
```

Preserve existing work. Do not print signing secrets. Do not install on a physical phone without explicit authorization. Update this handoff after each milestone.
