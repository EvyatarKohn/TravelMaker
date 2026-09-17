# Local installation

For normal Android Studio development, open **Build > Select Build Variant** (or the Build Variants tool window), choose **debug** for `app`, and run again.

`INSTALL_BASELINE_PROFILE_FAILED` for `app-release.apk` means the release installation's on-device profile compilation failed. It is separate from a runtime weather or navigation crash. Keep the release build non-debuggable; do not remove production profiles just to run locally.

If you need to test a release APK without Android Studio's profile-compilation step, install it directly using `adb -s <device-serial> install -r <release-apk-path>`. This does not verify baseline-profile optimization. The existing app must have the same signing certificate; do not uninstall an existing app without considering its local data.

See [Android baseline-profile debugging](https://developer.android.com/topic/performance/baselineprofiles/debug-baseline-profiles).

# Network failures

Weather and reverse-geocoding requests convert DNS failures, timeouts, and other I/O failures into a recoverable connection error. The weather screen offers Retry and retains cached weather during unsuccessful refreshes. Coroutine cancellation still propagates normally.

Validation: nine JVM tests pass, including injected DNS failure, timeout/retry, HTTP/empty responses, and cancellation. Debug APK assembly and lint pass. Final emulator interaction could not be completed because the emulator stopped responding to shell commands.
