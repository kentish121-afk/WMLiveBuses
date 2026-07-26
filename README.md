# WM Live Buses

Android app for browsing **live and recent UK / West Midlands bus vehicles** using public data from [bustimes.org](https://bustimes.org).

Designed as a companion to:
- [NextStopRealtime](https://github.com/kentish121-afk/NextStopRealtime) – open a stop or journey
- [WMBP-Forum-Android](https://github.com/kentish121-afk/WMBP-Forum-Android) – search / post about a fleet

## Features

- Search vehicles by fleet number, registration or free text
- Filter by operator (especially useful for NXWM, Diamond, Arriva Midlands, National Express West Midlands, etc.)
- View vehicle details: fleet code, reg, type, livery, operator, garage, special features
- Deep links:
  - “Open in NextStopRealtime” (where a trip is known)
  - “Search on WM Bus Photos Forum” (opens the forum app or browser)
- Clean Material 3 UI (Jetpack Compose)
- Respectful User-Agent and caching-friendly API usage

## Data Source

bustimes.org public API (`/api/vehicles/`, `/api/operators/`, vehicle journeys where available).

Please be respectful of the volunteer-run service – the app uses sensible limits and a clear User-Agent.

## Tech Stack

- Kotlin + Jetpack Compose + Material 3
- Retrofit + kotlinx.serialization
- ViewModel + StateFlow
- Min SDK 26

## Build & Run

1. Open in Android Studio
2. Sync Gradle
3. Run on device or emulator (API 26+)

## Deep linking notes

Package names used for intents (update if you change them):
- NextStopRealtime: `com.example.nextstoprealtime`
- WMBP Forum: `com.wmbusphotos.forum`

## Licence

Educational / demonstration. Bus data remains under original open licences. Credit bustimes.org.
