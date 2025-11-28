package com.salah.times;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsManager {
    private static final String PREF_NAME = "SalahTimesPrefs";
    private static final String KEY_DEFAULT_CITY = "default_city";
    private static final String KEY_LANGUAGE = "language";
    
    private SharedPreferences prefs;
    
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
}