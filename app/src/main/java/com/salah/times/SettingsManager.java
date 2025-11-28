package com.salah.times;

import android.os.Environment;
import org.json.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SettingsManager {
    private static final File CONFIG_DIR = new File(Environment.getExternalStorageDirectory(), "SalahTimes/config");
    private static final File SETTINGS_FILE = new File(CONFIG_DIR, "settings.json");
    
    public static void setDefaultCity(String cityName) {
        JSONObject settings = loadSettings();
        try {
            settings.put("default_city", cityName);
            settings.put("last_modified", getCurrentTimestamp());
            saveSettings(settings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String getDefaultCity() {
        JSONObject settings = loadSettings();
        return settings.optString("default_city", "Casablanca");
    }
    
    public static void setLanguage(String language) {
        JSONObject settings = loadSettings();
        try {
            settings.put("language", language);
            settings.put("last_modified", getCurrentTimestamp());
            saveSettings(settings);
            TranslationManager.setLanguage(language);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String getLanguage() {
        JSONObject settings = loadSettings();
        return settings.optString("language", "ar");
    }
    
    public static void setNotificationsEnabled(boolean enabled) {
        JSONObject settings = loadSettings();
        try {
            settings.put("notifications_enabled", enabled);
            settings.put("last_modified", getCurrentTimestamp());
            saveSettings(settings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static boolean getNotificationsEnabled() {
        JSONObject settings = loadSettings();
        return settings.optBoolean("notifications_enabled", true);
    }
    
    public static void setTheme(String theme) {
        JSONObject settings = loadSettings();
        try {
            settings.put("theme", theme);
            settings.put("last_modified", getCurrentTimestamp());
            saveSettings(settings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String getTheme() {
        JSONObject settings = loadSettings();
        return settings.optString("theme", "auto");
    }
    
    public static void setAutoUpdate(boolean enabled) {
        JSONObject settings = loadSettings();
        try {
            settings.put("auto_update", enabled);
            settings.put("last_modified", getCurrentTimestamp());
            saveSettings(settings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static boolean getAutoUpdate() {
        JSONObject settings = loadSettings();
        return settings.optBoolean("auto_update", true);
    }
    
    public static void setAdanEnabled(boolean enabled) {
        JSONObject settings = loadSettings();
        try {
            settings.put("adan_enabled", enabled);
            settings.put("last_modified", getCurrentTimestamp());
            saveSettings(settings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static boolean getAdanEnabled() {
        JSONObject settings = loadSettings();
        return settings.optBoolean("adan_enabled", true);
    }
    
    public static void setPrayerAlarmEnabled(String prayer, boolean enabled) {
        JSONObject settings = loadSettings();
        try {
            JSONObject prayerAlarms = settings.optJSONObject("prayer_alarms");
            if (prayerAlarms == null) {
                prayerAlarms = new JSONObject();
            }
            prayerAlarms.put(prayer.toLowerCase(), enabled);
            settings.put("prayer_alarms", prayerAlarms);
            settings.put("last_modified", getCurrentTimestamp());
            saveSettings(settings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static boolean getPrayerAlarmEnabled(String prayer) {
        JSONObject settings = loadSettings();
        JSONObject prayerAlarms = settings.optJSONObject("prayer_alarms");
        if (prayerAlarms != null) {
            return prayerAlarms.optBoolean(prayer.toLowerCase(), true);
        }
        return true;
    }
    
    public static void setAdhanRingtone(int type) {
        JSONObject settings = loadSettings();
        try {
            settings.put("adhan_ringtone", type);
            settings.put("last_modified", getCurrentTimestamp());
            saveSettings(settings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static int getAdhanRingtone() {
        JSONObject settings = loadSettings();
        return settings.optInt("adhan_ringtone", 0);
    }
    
    public static void setAdhanVolume(int volume) {
        JSONObject settings = loadSettings();
        try {
            settings.put("adhan_volume", volume);
            settings.put("last_modified", getCurrentTimestamp());
            saveSettings(settings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static int getAdhanVolume() {
        JSONObject settings = loadSettings();
        return settings.optInt("adhan_volume", 80);
    }
    
    public static void setIqamaDelay(String prayer, int minutes) {
        JSONObject settings = loadSettings();
        try {
            JSONObject iqamaDelays = settings.optJSONObject("iqama_delays");
            if (iqamaDelays == null) {
                iqamaDelays = new JSONObject();
            }
            iqamaDelays.put(prayer.toLowerCase(), minutes);
            settings.put("iqama_delays", iqamaDelays);
            settings.put("last_modified", getCurrentTimestamp());
            saveSettings(settings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static int getIqamaDelay(String prayer) {
        JSONObject settings = loadSettings();
        JSONObject iqamaDelays = settings.optJSONObject("iqama_delays");
        if (iqamaDelays != null) {
            return iqamaDelays.optInt(prayer.toLowerCase(), 10);
        }
        return 10; // Default 10 minutes
    }
    
    private static JSONObject loadSettings() {
        try {
            if (!SETTINGS_FILE.exists()) {
                return createDefaultSettings();
            }
            
            BufferedReader reader = new BufferedReader(new FileReader(SETTINGS_FILE));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();
            
            return new JSONObject(content.toString());
        } catch (Exception e) {
            return createDefaultSettings();
        }
    }
    
    private static void saveSettings(JSONObject settings) {
        try {
            CONFIG_DIR.mkdirs();
            FileWriter writer = new FileWriter(SETTINGS_FILE);
            writer.write(settings.toString(2));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static JSONObject createDefaultSettings() {
        try {
            JSONObject defaults = new JSONObject();
            defaults.put("default_city", "Casablanca");
            defaults.put("language", "ar");
            defaults.put("notifications_enabled", true);
            defaults.put("theme", "auto");
            defaults.put("auto_update", true);
            defaults.put("adan_enabled", true);
            
            // Default iqama delays (10 minutes for all prayers)
            JSONObject iqamaDelays = new JSONObject();
            iqamaDelays.put("fajr", 10);
            iqamaDelays.put("dhuhr", 10);
            iqamaDelays.put("asr", 10);
            iqamaDelays.put("maghrib", 5);
            iqamaDelays.put("isha", 10);
            defaults.put("iqama_delays", iqamaDelays);
            defaults.put("created", getCurrentTimestamp());
            defaults.put("last_modified", getCurrentTimestamp());
            saveSettings(defaults);
            return defaults;
        } catch (Exception e) {
            return new JSONObject();
        }
    }
    
    private static String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(new Date());
    }
}