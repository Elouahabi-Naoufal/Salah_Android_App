import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';

class StorageService {
  static SharedPreferences? _prefs;

  static Future<void> init() async {
    _prefs = await SharedPreferences.getInstance();
  }

  // City settings
  static String get currentCity => _prefs?.getString('current_city') ?? 'Tangier';
  static set currentCity(String city) => _prefs?.setString('current_city', city);

  // Language settings
  static String get currentLanguage => _prefs?.getString('current_language') ?? 'en';
  static set currentLanguage(String language) => _prefs?.setString('current_language', language);

  // Iqama times
  static Map<String, int> get iqamaTimes {
    final String? data = _prefs?.getString('iqama_times');
    if (data != null) {
      return Map<String, int>.from(jsonDecode(data));
    }
    return {
      'Fajr': 20,
      'Dohr': 15,
      'Asr': 15,
      'Maghreb': 10,
      'Isha': 15,
    };
  }
  
  static set iqamaTimes(Map<String, int> times) {
    _prefs?.setString('iqama_times', jsonEncode(times));
  }

  // Notification settings
  static Map<String, dynamic> get notificationSettings {
    final String? data = _prefs?.getString('notification_settings');
    if (data != null) {
      return Map<String, dynamic>.from(jsonDecode(data));
    }
    return {
      'sound_enabled': true,
      'Fajr': {'enabled': true, 'repeat_count': 3},
      'Dohr': {'enabled': true, 'repeat_count': 3},
      'Asr': {'enabled': true, 'repeat_count': 3},
      'Maghreb': {'enabled': true, 'repeat_count': 3},
      'Isha': {'enabled': true, 'repeat_count': 3},
    };
  }
  
  static set notificationSettings(Map<String, dynamic> settings) {
    _prefs?.setString('notification_settings', jsonEncode(settings));
  }

  // Cache prayer times
  static Future<void> cachePrayerTimes(String city, Map<String, dynamic> data) async {
    await _prefs?.setString('prayer_cache_$city', jsonEncode(data));
    await _prefs?.setString('cache_timestamp_$city', DateTime.now().toIso8601String());
  }

  static Map<String, dynamic>? getCachedPrayerTimes(String city) {
    final String? data = _prefs?.getString('prayer_cache_$city');
    final String? timestamp = _prefs?.getString('cache_timestamp_$city');
    
    if (data != null && timestamp != null) {
      final cacheTime = DateTime.parse(timestamp);
      final now = DateTime.now();
      
      // Cache valid for 24 hours
      if (now.difference(cacheTime).inHours < 24) {
        return Map<String, dynamic>.from(jsonDecode(data));
      }
    }
    return null;
  }

  // First run check
  static bool get isFirstRun => _prefs?.getBool('first_run') ?? true;
  static set isFirstRun(bool value) => _prefs?.setBool('first_run', value);
}