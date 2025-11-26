# Salah Times Application - Complete Documentation

## Overview
This documentation provides a comprehensive analysis of the `ultra_modern_salah.py` file, which implements a sophisticated Islamic prayer times application with multilingual support, offline functionality, and advanced system integration.

## Application Architecture

The application is built using PyQt5 and follows a modular architecture with clear separation of concerns:

### Core Components
1. **[Imports and Setup](01_imports_and_setup.md)** - Dependencies and initial configuration
2. **[Translation System](02_translations_system.md)** - Multilingual support (English, Arabic, French)
3. **[Cities Database](03_cities_database.md)** - Moroccan cities with coordinates and IDs
4. **[Settings Dialog](04_settings_dialog.md)** - Configuration interface with tabbed layout
5. **[City Selection Dialog](05_city_selection_dialog.md)** - First-time setup interface
6. **[Offline Calculator](06_offline_calculator.md)** - Astronomical calculations for offline mode
7. **[Prayer Time Worker](07_prayer_time_worker.md)** - Asynchronous data fetching and caching
8. **[Main Application](08_main_application.md)** - Core UI and coordination logic
9. **[Main Function](09_main_function_and_startup.md)** - Application startup and lifecycle

### Advanced Features
10. **[Hijri Date Integration](10_hijri_date_integration.md)** - Islamic calendar with API integration
11. **[Notification System](11_notification_system.md)** - Desktop notifications with sound alerts
12. **[Iqama Timing System](12_iqama_timing_system.md)** - Configurable prayer congregation timing
13. **[System Tray Integration](13_system_tray_integration.md)** - Comprehensive tray functionality
14. **[Advanced View Menu System](14_advanced_view_menu_system.md)** - Extensible view options
15. **[Geometry Persistence System](15_geometry_persistence_system.md)** - Window position/size memory
16. **[Real-Time Clock Display](16_real_time_clock_display.md)** - Prominent live clock with elegant typography
17. **[Prayer Highlighting System](17_prayer_highlighting_system.md)** - Automatic current prayer detection and visual emphasis
18. **[Comprehensive Error Handling](18_comprehensive_error_handling.md)** - Robust error recovery and graceful degradation
19. **[Update Timestamp Tracking](19_update_timestamp_tracking.md)** - Intelligent background update management

## Key Features

### User Interface
- **Modern Design** - Gradient backgrounds, glass effects, rounded corners
- **Real-Time Clock** - Large, elegant time display with live updates
- **Prayer Highlighting** - Automatic detection and visual emphasis of current prayer
- **Responsive Layout** - Grid-based prayer time display with real-time updates
- **System Tray Integration** - Comprehensive tray functionality with context menu
- **Multilingual Support** - English, Arabic, and French translations

### Functionality
- **Real-Time Prayer Times** - Web scraping from yabiladi.com with offline fallback
- **Hijri Date Display** - Islamic calendar integration with API support
- **Offline Mode** - Cached data and astronomical calculations
- **Iqama Timing** - Configurable delays between Adhan and Iqama with live countdown
- **Advanced Notifications** - System notifications with sound support and per-prayer configuration
- **Multiple Views** - Monthly calendar, weekly schedule, timezone comparison (extensible)

### Technical Features
- **Asynchronous Operations** - Non-blocking data fetching using QThread
- **Smart Caching** - Intelligent data storage and update management with timestamp tracking
- **Single Instance** - Prevents multiple application instances with PID-based locking
- **Configuration Persistence** - JSON-based settings storage with geometry memory
- **Comprehensive Error Handling** - Robust error recovery throughout all components
- **Update Management** - Background updates with intelligent scheduling

## Data Flow

### Startup Sequence
1. **Single Instance Check** - Verify no other instance running with lock file
2. **Configuration Loading** - Read saved settings or show setup dialog
3. **UI Initialization** - Create main window and components with geometry restoration
4. **Data Fetching** - Start prayer time worker thread with timestamp checking
5. **Tray Creation** - Initialize system tray icon with context menu
6. **Real-Time Updates** - Start timers for clock, countdown, and highlighting

### Prayer Time Updates
1. **Cache Check** - Load existing data immediately for responsiveness
2. **Timestamp Validation** - Check if cached data is fresh enough
3. **Network Fetch** - Scrape fresh data from web source if needed
4. **Data Processing** - Parse and structure prayer times with Chorok calculation
5. **UI Update** - Display times with current prayer highlighting
6. **Cache Storage** - Save data for offline access with timestamp

### Offline Fallback
1. **Connection Test** - Check internet availability
2. **Cache Lookup** - Use stored prayer times if available
3. **Calculation Fallback** - Use astronomical formulas for sunrise (Chorok)
4. **Partial Display** - Show available data with offline indicators
5. **Error Recovery** - Graceful degradation with user feedback

## File Structure

### Configuration Files
```
~/.salah_times/
├── config/
│   ├── app_config.json          # City and language settings
│   ├── main_geometry.json       # Main window position/size
│   ├── settings_geometry.json   # Settings dialog geometry
│   ├── iqama_times.json        # Prayer-specific Iqama delays
│   ├── notifications.json      # Notification preferences
│   └── last_update.json        # Update timestamp tracking
├── cities/
│   ├── tangier.json            # City-specific prayer times
│   ├── casablanca.json         # Cached data per city
│   └── ...
└── app.lock                    # Single instance lock file
```

## Integration Points

### External Dependencies
- **yabiladi.com** - Primary prayer time data source
- **aladhan.com API** - Hijri date conversion with fallback
- **System Notifications** - Desktop notification system integration
- **System Tray** - Operating system tray integration
- **Audio System** - Sound playback for notifications

### Internal Relationships
- **Settings ↔ Main App** - Configuration changes trigger UI updates and geometry persistence
- **Worker ↔ Main App** - Asynchronous data delivery via signals with error handling
- **Calculator ↔ Worker** - Offline fallback calculations with timestamp coordination
- **Translation ↔ All Components** - Multilingual text throughout with dynamic updates
- **Timer ↔ UI Components** - Real-time updates for clock, countdown, and highlighting
- **Tray ↔ Main App** - Bidirectional communication for window management

## Error Handling Strategy

### Network Issues
- Graceful degradation to cached data with timestamp validation
- Offline calculation fallbacks with astronomical formulas
- User-friendly error messages with actionable guidance
- Automatic retry mechanisms with intelligent scheduling

### Configuration Errors
- Default value fallbacks with automatic config creation
- Safe directory creation with permission handling
- JSON parsing error recovery with data validation
- Geometry restoration safeguards with screen boundary checking

### UI Resilience
- Component isolation prevents cascading failures
- Proper signal/slot error handling with timeout protection
- Resource cleanup on application exit with lock file management
- Cross-platform compatibility considerations

## Development Insights

### Code Organization
- Clear class separation with single responsibilities
- Consistent naming conventions and comprehensive documentation
- Proper error handling throughout all components
- Modular design enabling easy maintenance and extension

### Performance Optimizations
- Asynchronous operations prevent UI blocking
- Smart caching reduces network requests with timestamp tracking
- Efficient data structures for quick lookups
- Minimal resource usage for system tray and real-time updates

### User Experience Focus
- Instant cached data loading for responsiveness
- Smooth animations and transitions with modern styling
- Intuitive settings organization with tabbed interface
- Comprehensive offline functionality with visual feedback

## Advanced Technical Features

### Real-Time Systems
- **Live Clock Display** - Prominent time display with elegant typography
- **Prayer Highlighting** - Automatic current prayer detection and emphasis
- **Countdown Timers** - Multiple simultaneous countdowns (next prayer, Iqama)
- **Background Updates** - Non-blocking data refresh with timestamp management

### System Integration
- **Tray Functionality** - Custom icon, context menu, minimize-to-tray behavior
- **Notification System** - Desktop notifications with sound and per-prayer configuration
- **Geometry Persistence** - Window position/size memory across sessions
- **Single Instance** - PID-based application instance management

### Data Management
- **Smart Caching** - Multi-city data storage with expiration tracking
- **Update Coordination** - Centralized timestamp tracking for all cities
- **Error Recovery** - Comprehensive fallback mechanisms at all levels
- **Configuration System** - JSON-based settings with validation and defaults

This documentation serves as a complete reference for understanding, maintaining, and extending the sophisticated Salah Times application with its comprehensive feature set and robust architecture.