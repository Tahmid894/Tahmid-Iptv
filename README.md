# Tahmid IPTV

A native Android IPTV streaming application crafted with Kotlin, Jetpack Compose, Material 3, and AndroidX Media3 ExoPlayer. Featuring a Tiwee IPTV-inspired dark purple and black interface, fast asynchronous M3U/M3U8 playlist parsing, full-screen playback, and support for global TV categories, countries, languages, and custom playlists.

## Features

- **22 Curated Categories**:
  - Live TV, Movies, Series, Animation, Music, Auto, Sports, News, Cooking, Kids, Education, Business, Relax, Entertainment, Lifestyle, Science, Comedy, Family, Shop, Countries, Languages, and Favorites.
- **Asynchronous M3U/M3U8 Parser**:
  - Full support for `#EXTM3U`, `#EXTINF`, `tvg-id`, `tvg-name`, `tvg-logo`, `group-title`, and stream URLs.
  - Streams channel metadata first without pre-downloading video content.
  - Automatic stream quality detection (`4K`, `FHD`, `HD`, `SD`).
- **Global Country Selector**:
  - 40+ countries with national flags and ISO country codes.
  - On-demand playlist loading (only fetches a country's playlist when selected).
- **International Language Support**:
  - 25+ broadcast languages with native script titles.
- **Media3 ExoPlayer Video Player**:
  - High performance HLS (`.m3u8`) and progressive HTTP/HTTPS streaming.
  - Portrait and landscape fullscreen playback.
  - Prev/Next channel switching, play/pause, aspect ratio adjustment (Fit, Zoom, Fill).
  - Graceful stream buffering and offline/error recovery.
- **Favorites & History**:
  - Save channels to Favorites with instant persistence.
  - Recently Played channel carousel for quick resumption.
- **Custom Playlists**:
  - Add and validate custom M3U/M3U8 subscription URLs.
  - Toggle built-in preset streams on or off.
  - Cache manager to clear temporary playlist files.

## Technical Stack

- **Platform**: Native Android (Min SDK 24, Target SDK 36)
- **Language**: Kotlin 2.2+
- **UI Toolkit**: Jetpack Compose with Material 3
- **Media Engine**: AndroidX Media3 ExoPlayer 1.5.1 (HLS & OkHttp data sources)
- **Image Loading**: Coil 2.7.0
- **Networking**: OkHttp 4.10.0 with 50MB disk caching & follow-redirects
- **Persistence**: Jetpack DataStore Preferences

## How to Build the APK

### Prerequisites
- JDK 17 or JDK 21
- Android SDK with platform tools (API 24 to API 36)

### Command Line Build
```bash
# To assemble the debug APK:
gradle :app:assembleDebug

# The generated APK will be located at:
# app/build/outputs/apk/debug/app-debug.apk
```

### Install onto Device / Emulator via ADB
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Application ID & Package
- **App Name**: Tahmid IPTV
- **Application ID**: `com.tahmid.iptv`
