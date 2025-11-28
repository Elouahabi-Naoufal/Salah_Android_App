package com.salah.times;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsManager {
    private static final String PREF_NAME = "SalahTimesPrefs";
    private static final String KEY_DEFAULT_CITY = "default_city";
    private static final String KEY_LANGUAGE = "language";
    private static Context appContext;
    
    private SharedPreferences prefs;
    
    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }
    
    public SharedPrefsManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public void setDefaultCity(String cityName) {
        prefs.edit().putString(KEY_DEFAULT_CITY, cityName).apply();
    }
    
    public String getDefaultCity() {
        return prefs.getString(KEY_DEFAULT_CITY, "");
    }
    
    public void setLanguage(String language) {
        prefs.edit().putString(KEY_LANGUAGE, language).apply();
    }
    
    public String getLanguage() {
        return prefs.getString(KEY_LANGUAGE, "ar");
    }
    
    public static String getString(String key, String defaultValue) {
        if (appContext == null) return defaultValue;
        SharedPreferences prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(key, defaultValue);
    }
    
    public static boolean getBoolean(String key, boolean defaultValue) {
        if (appContext == null) return defaultValue;
        SharedPreferences prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(key, defaultValue);
    }
    
    public static void putString(String key, String value) {
        if (appContext == null) return;
        SharedPreferences prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(key, value).apply();
    }
    
    public static void putBoolean(String key, boolean value) {
        if (appContext == null) return;
        SharedPreferences prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(key, value).apply();
    }
}