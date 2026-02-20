package com.salah.times;

import android.content.Context;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TestingManager {
    private static final String PREFS_NAME = "testing_prefs";
    private static final String KEY_TESTING_MODE = "testing_mode";
    private static final String KEY_TEST_FAJR = "test_fajr";
    private static final String KEY_TEST_DHUHR = "test_dhuhr";
    private static final String KEY_TEST_ASR = "test_asr";
    private static final String KEY_TEST_MAGHRIB = "test_maghrib";
    private static final String KEY_TEST_ISHA = "test_isha";
    
    public static boolean isTestingMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_TESTING_MODE, false);
    }
    
    public static void setTestingMode(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_TESTING_MODE, enabled).apply();
    }
    
    public static void setTestTime(Context context, String prayer, String time) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String key = getKeyForPrayer(prayer);
        if (key != null) {
            prefs.edit().putString(key, time).apply();
        }
    }
    
    public static PrayerTimes getTestPrayerTimes(Context context, PrayerTimes original) {
        if (!isTestingMode(context)) return original;
        
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        String fajr = prefs.getString(KEY_TEST_FAJR, original.getFajr());
        String dhuhr = prefs.getString(KEY_TEST_DHUHR, original.getDhuhr());
        String asr = prefs.getString(KEY_TEST_ASR, original.getAsr());
        String maghrib = prefs.getString(KEY_TEST_MAGHRIB, original.getMaghrib());
        String isha = prefs.getString(KEY_TEST_ISHA, original.getIsha());
        
        return new PrayerTimes(original.getDate(), fajr, original.getSunrise(), dhuhr, asr, maghrib, isha);
    }
    
    private static String getKeyForPrayer(String prayer) {
        switch (prayer.toLowerCase()) {
            case "fajr": return KEY_TEST_FAJR;
            case "dhuhr": case "dohr": return KEY_TEST_DHUHR;
            case "asr": return KEY_TEST_ASR;
            case "maghrib": case "maghreb": return KEY_TEST_MAGHRIB;
            case "isha": return KEY_TEST_ISHA;
            default: return null;
        }
    }
}