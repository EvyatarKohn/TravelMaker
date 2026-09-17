# TravelMaker improvement handoff

User request: improve the Android weather/travel app, including UI, weather-aware outings, attractions, saved trips, widgets and optional notifications. Work within the available Codex allowance and leave a buildable checkpoint for Cursor. Do not install on a physical phone without explicit authorization.

## Implemented in this checkpoint

- Weather dashboard: mint gradient hero, rounded cards, 24-hour strip, destination-local daily dates, weather metrics, pull-to-refresh, manual retry, last-update time and cached/offline message. New surfaces have night colors; this is not a complete app-wide theme migration.
- `OutdoorForecast.kt`: chooses a contiguous two-hour daylight window within 24 hours, considering precipitation, feels-like temperature, wind, UV and weather condition codes. Handles imperial conversion, gaps and missing forecasts; UI suppresses recommendations when alerts exist. Thresholds are product heuristics, not safety guarantees.
- `DailyWeatherAdapter`: daily highs/lows now stay with their own dates instead of sorting temperatures independently. Destination timezone used for dates; double timezone offset removed from Weather time formatting.
- Attraction results: rounded cards, name filtering, category filtering, selected marker color/label, fit-all action, directions, and add-to-day-plan action.
- `DayPlanActivity`, `DayPlanStore`, `DayPlan`: one local itinerary, saved date/notes/stops, duplicate prevention, move up/down, removal with Undo, directions per stop, and native share sheet. Uses SharedPreferences JSON; no account/backend/migration. Accessible from dashboard and attraction results.
- `WeatherWidget`: last successful in-app forecast, condition and explicit update timestamp; tap opens app. No periodic/background refresh. Add through launcher widgets picker.
- Map selection: coordinate fallback shown immediately; reverse geocoding moved to IO with view-lifecycle cancellation. Autocomplete uses returned `Place.Field.LOCATION` instead of a second name geocode. This is the field supported by installed Places 5.3.0.
- Existing RecyclerView and network fixes are preserved. Earlier crash fixes appear to have been committed before this request; do not revert them.
- **Temperature units (Cursor milestone):** `Weather.responseUnits` stores the units of the API values on each Room row. Labels/helpers (`getDegreeUnits`, wind, visibility, precipitation, outdoor-window imperial flag) use `responseUnits`, not global `MainData.degreesUnits`. Room DB version 11 with non-destructive `MIGRATION_10_11` (`ALTER TABLE … ADD COLUMN responseUnits … DEFAULT 'metric'`). ViewModels stamp `responseUnits` on successful fetches. Dashboard syncs the ℃|F toggle from the forecast on screen. Overlapping weather requests are latest-wins (`Job` cancel) in `NewWeatherViewModel` and `WeatherViewModel`.

## Remaining scope

- FIRST: device/visual QA. Existing emulator-5582 appears online in adb but shell commands hang. No APK was installed on the connected physical phone. Verify small screens, large text, dark mode, repeated tab changes, process recreation, map callbacks and widget launcher behavior.
- Multiple named trips, forecasts for trip dates, routed itineraries. Current day plan date is organizational only; no date forecast or route optimization.
- Attraction photos/details using a supported provider; do not fabricate data or assume a new paid API is available.
- Periodic widget refresh and opt-in notifications (permissions, notification settings, background work, battery/network constraints).
- Weather-aware attraction ranking/indoor category suggestions. Current weather-aware recommendation is the dashboard time window only.
- Draggable map detail sheet, search-this-map-area, richer place detail screen.
- Full app-wide dark theme and accessibility/large-font/device QA. Legacy screens still have hardcoded white/black resources. No new bottom-nav structure, onboarding or skeleton loading was added.
- Optional follow-ups on units: persist preferred units across process death; `DailyDialog` still reads `MainData.degreesUnits` for day-detail labels; legacy `weathermodelold` helpers still use globals.

## Verification status

- Unit tests: 21 total (previous 20 + `WeatherUnitsTest`); all passed (`:app:testDebugUnitTest`).
- `:app:assembleDebug` and `:app:lintDebug` succeeded (15m 13s). Lint reports warnings only (e.g. DefaultLocale, UnusedAttribute); no lint errors. Earlier RepeatOnLifecycleDetector FIR crash did not reproduce on this run.
- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`.
- No emulator screenshots or runtime interaction were verified in this session. Do not install on a physical phone.

## Files to start with

- `ui/fragments/CityFragment.kt`, `res/layout/city_fragment_layout.xml`, `res/values/dashboard.xml`
- `util/OutdoorForecast.kt`, `util/WeatherPresentation.kt`, `ui/adapters/HourlyWeatherAdapter.kt`
- `ui/AttractionMapActivity.kt`, `ui/DayPlanActivity.kt`, `repository/DayPlanStore.kt`, `model/DayPlan.kt`
- `ui/WeatherWidget.kt`, `res/xml/weather_widget_info.xml`
- Units/cache: `model/weathermodel/Weather.kt`, `db/CitiesDB.kt`, `db/DataConverter.kt` (`MIGRATION_10_11`), `viewmodels/NewWeatherViewModel.kt`, `viewmodels/WeatherViewModel.kt`
- Tests: `OutdoorForecastTest.kt`, `DayPlanTest.kt`, `WeatherUnitsTest.kt`

Kotlin paths above are under `app/src/main/java/com/evya/myweatherapp`; resources are under `app/src/main`, tests under `app/src/test/java/com/evya/myweatherapp`.

## Build and verification

PowerShell from `C:\Users\evyatar-c\Documents\TravelMaker`:

```powershell
$env:JAVA_HOME = 'C:\Users\evyatar-c\.jdks\jbr-17.0.14'
$env:JAVA_TOOL_OPTIONS = '-Djdk.net.unixdomain.tmpdir=C:\Users\evyatar-c\Documents\TravelMaker'
.\gradlew.bat --no-daemon :app:testDebugUnitTest :app:assembleDebug :app:lintDebug --console=plain
```

Use `git -c safe.directory=C:/Users/evyatar-c/Documents/TravelMaker status --short` and inspect the current diff before editing. Preserve all existing work. Do not print signing secrets from Gradle/local properties. Debug APK: `app/build/outputs/apk/debug/app-debug.apk`. Unit tests do not replace device navigation and visual testing. Previous emulator attempts were unreliable.

## Prompt for Cursor

Read `docs/CURSOR_HANDOFF.md`, inspect the current git diff and applicable AGENTS.md instructions, and continue the unfinished TravelMaker improvements listed here. Preserve completed features and crash fixes. Keep changes buildable, add meaningful tests for business logic, run debug unit tests/build/lint, and report exactly what is verified versus still pending. Use an emulator for runtime checks; do not install on a physical phone. Update this handoff after each completed milestone.
