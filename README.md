# Salah Times Android App

Native Android conversion of the sophisticated Python desktop prayer times application.

## Features

- **Real-time Prayer Times**: Fetches accurate prayer times from yabiladi.com
- **42+ Moroccan Cities**: Complete database of major Moroccan cities with multilingual names
- **Live Clock**: Real-time digital clock display
- **Next Prayer Countdown**: Shows time remaining until next prayer
- **Modern UI**: Material Design 3 with cards and clean layout
- **Offline Support**: Cached prayer times and fallback calculations
- **Settings Management**: Persistent city selection and preferences

## Architecture

### Core Components
- `MainActivity.java`: Main activity with UI and real-time updates
- `PrayerTimesService.java`: Web scraping service for prayer times
- `CitiesData.java`: Static database of 42+ Moroccan cities
- `City.java`: City model with multilingual support
- `PrayerTimes.java`: Prayer times data model
- `SharedPrefsManager.java`: Settings and preferences management

### UI Layout
- `activity_main.xml`: Main layout with clock card and prayer grid
- Material Design 3 theme with Islamic green color scheme
- Responsive grid layout for prayer times display

## Dependencies

- **OkHttp**: HTTP client for network requests
- **Retrofit**: REST API client
- **Jsoup**: HTML parsing for web scraping
- **Gson**: JSON serialization/deserialization

## Build Instructions

1. Open project in Android Studio
2. Sync Gradle files
3. Build and run on device/emulator

## Original Python App

This Android app is a native conversion of the ultra-sophisticated Python desktop application featuring:
- 19 major components with 117+ functions
- Multilingual support (English/Arabic/French)
- System tray integration
- Desktop notifications
- Hijri calendar integration
- Advanced astronomical calculations
- Comprehensive error handling

The Android version preserves core functionality while adapting to mobile platform constraints.