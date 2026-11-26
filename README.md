# اُکالا استورز — Okala Stores

A modern, unofficial Android client for browsing nearby Okala stores in Iran. Built with Kotlin, Jetpack Compose (Material 3), and forced RTL Persian UI.

> **Note:** This is an independent concept app. It is not affiliated with, endorsed by, or officially connected to Okala. Store data comes from Okala's public web API.

## Features

- Live nearby store list from Okala's public store API
- Search, category filters, and sorting (distance / rating / popularity)
- Store detail screen with rating, ETA, delivery fee, minimum order, labels, and delivery methods
- "Navigate" button opening maps with the store's coordinates
- "Shop" button opening the real Okala store page inside an in-app WebView
- Location permission with a Tehran fallback, plus offline cache of the last result
- Adaptive red app icon with the Okala wordmark and teal dot; Vazirmatn Persian typography

## Tech stack

- Kotlin 2.0.20, AGP 8.5.2, Gradle 8.7, JDK 17
- Jetpack Compose + Material 3 (Compose BOM 2024.09.02)
- Retrofit + OkHttp + kotlinx.serialization
- Coil for store logos
- Platform `LocationManager` (no Google Play Services dependency)
- minSdk 26, targetSdk/compileSdk 34

## Building locally

```bash
./gradlew assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

## Building from Termux

Use `build_okala_from_zip.sh` (see `/sdcard/Jeez/`). It asks for the path to `OkalaStores-source.zip`, stages and validates the project, asks for a GitHub token with `repo` and `workflow` scopes, pushes to a GitHub repo, waits for GitHub Actions, downloads the APK, and publishes it as a GitHub Release. The token is read with a hidden prompt and is never stored in the script or on disk.
