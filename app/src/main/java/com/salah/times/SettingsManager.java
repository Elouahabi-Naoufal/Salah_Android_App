package com.salah.times;

import android.content.Context;

public class SettingsManager {
    private static Context appContext;
    
    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }
    
    public static void setDefaultCity(String cityName) {
        DatabaseHelper.getInstance(appContext).saveSetting("default_city", cityName);
    }
    
    public static String getDefaultCity() {
        return DatabaseHelper.getInstance(appContext).getSetting("default_city", "Casablanca");
    }
    
    public static void setLanguage(String language) {
        DatabaseHelper.getInstance(appContext).saveSetting("language", language);
        TranslationManager.setLanguage(language);
    }
    
    public static String getLanguage() {
        return DatabaseHelper.getInstance(appContext).getSetting("language", "ar");
    }
    
    public static void setNotificationsEnabled(boolean enabled) {
        DatabaseHelper.getInstance(appContext).saveSetting("notifications_enabled", String.valueOf(enabled));
    }
    
    public static boolean getNotificationsEnabled() {
        return Boolean.parseBoolean(DatabaseHelper.getInstance(appContext).getSetting("notifications_enabled", "true"));
    }
    
    public static void setTheme(String theme) {
        DatabaseHelper.getInstance(appContext).saveSetting("theme", theme);
    }
    
    public static String getTheme() {
        return DatabaseHelper.getInstance(appContext).getSetting("theme", "auto");
    }
    
    public static void setAutoUpdate(boolean enabled) {
        DatabaseHelper.getInstance(appContext).saveSetting("auto_update", String.valueOf(enabled));
    }
    
    public static boolean getAutoUpdate() {
        return Boolean.parseBoolean(DatabaseHelper.getInstance(appContext).getSetting("auto_update", "true"));
    }
    
    public static void setAdanEnabled(boolean enabled) {
        DatabaseHelper.getInstance(appContext).saveSetting("adan_enabled", String.valueOf(enabled));
    }
    
    public static boolean getAdanEnabled() {
        return Boolean.parseBoolean(DatabaseHelper.getInstance(appContext).getSetting("adan_enabled", "true"));
    }
    
    public static void setPrayerAlarmEnabled(String prayer, boolean enabled) {
        DatabaseHelper.getInstance(appContext).setPrayerAlarmEnabled(prayer, enabled);
    }
    
    public static boolean getPrayerAlarmEnabled(String prayer) {
        return DatabaseHelper.getInstance(appContext).getPrayerAlarmEnabled(prayer, true);
    }
    
    public static void setAdhanRingtone(int type) {
        DatabaseHelper.getInstance(appContext).saveSetting("adhan_ringtone", String.valueOf(type));
    }
    
    public static int getAdhanRingtone() {
        return Integer.parseInt(DatabaseHelper.getInstance(appContext).getSetting("adhan_ringtone", "0"));
    }
    
    public static void setAdhanVolume(int volume) {
        DatabaseHelper.getInstance(appContext).saveSetting("adhan_volume", String.valueOf(volume));
    }
    
    public static int getAdhanVolume() {
        return Integer.parseInt(DatabaseHelper.getInstance(appContext).getSetting("adhan_volume", "80"));
    }
    
    public static void setIqamaDelay(String prayer, int minutes) {
        DatabaseHelper.getInstance(appContext).setIqamaDelay(prayer, minutes);
    }
    
    public static int getIqamaDelay(String prayer) {
        return DatabaseHelper.getInstance(appContext).getIqamaDelay(prayer, 10);
    }
    

}