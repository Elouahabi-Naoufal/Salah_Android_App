package com.salah.times;

import android.content.Context;
import android.os.Environment;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class MigrationHelper {
    private static final String PREF_MIGRATION = "migration_prefs";
    private static final String KEY_MIGRATED = "data_migrated_v1";

    public static void migrateIfNeeded(Context context) {
        if (isMigrated(context)) return;

        DatabaseHelper db = DatabaseHelper.getInstance(context);
        File baseDir = new File(Environment.getExternalStorageDirectory(), "SalahTimes");
        
        // Migrate settings
        migrateSettings(db, new File(baseDir, "config/settings.json"));
        
        // Migrate prayer times
        migratePrayerTimes(db, new File(baseDir, "cities"));
        
        markMigrated(context);
    }

    private static void migrateSettings(DatabaseHelper db, File settingsFile) {
        try {
            if (!settingsFile.exists()) return;
            
            BufferedReader reader = new BufferedReader(new FileReader(settingsFile));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();
            
            JSONObject settings = new JSONObject(content.toString());
            
            if (settings.has("default_city")) db.saveSetting("default_city", settings.getString("default_city"));
            if (settings.has("language")) db.saveSetting("language", settings.getString("language"));
            if (settings.has("notifications_enabled")) db.saveSetting("notifications_enabled", String.valueOf(settings.getBoolean("notifications_enabled")));
            if (settings.has("theme")) db.saveSetting("theme", settings.getString("theme"));
            if (settings.has("auto_update")) db.saveSetting("auto_update", String.valueOf(settings.getBoolean("auto_update")));
            if (settings.has("adan_enabled")) db.saveSetting("adan_enabled", String.valueOf(settings.getBoolean("adan_enabled")));
            
            if (settings.has("iqama_delays")) {
                JSONObject iqama = settings.getJSONObject("iqama_delays");
                String[] prayers = {"fajr", "dhuhr", "asr", "maghrib", "isha"};
                for (String prayer : prayers) {
                    if (iqama.has(prayer)) {
                        db.setIqamaDelay(prayer, iqama.getInt(prayer));
                    }
                }
            }
            
            if (settings.has("prayer_alarms")) {
                JSONObject alarms = settings.getJSONObject("prayer_alarms");
                String[] prayers = {"fajr", "dhuhr", "asr", "maghrib", "isha"};
                for (String prayer : prayers) {
                    if (alarms.has(prayer)) {
                        db.setPrayerAlarmEnabled(prayer, alarms.getBoolean(prayer));
                    }
                }
            }
        } catch (Exception e) {
            android.util.Log.e("Migration", "Settings migration failed", e);
        }
    }

    private static void migratePrayerTimes(DatabaseHelper db, File citiesDir) {
        try {
            if (!citiesDir.exists()) return;
            
            File[] cityFiles = citiesDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (cityFiles == null) return;
            
            for (File cityFile : cityFiles) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(cityFile));
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                    }
                    reader.close();
                    
                    JSONObject cityData = new JSONObject(content.toString());
                    String cityName = cityData.getString("city");
                    JSONObject prayerTimes = cityData.getJSONObject("prayer_times");
                    String date = prayerTimes.optString("date", "");
                    
                    db.savePrayerTimes(
                        cityName,
                        date,
                        prayerTimes.getString("fajr"),
                        prayerTimes.getString("sunrise"),
                        prayerTimes.getString("dhuhr"),
                        prayerTimes.getString("asr"),
                        prayerTimes.getString("maghrib"),
                        prayerTimes.getString("isha")
                    );
                } catch (Exception e) {
                    android.util.Log.e("Migration", "Failed to migrate city: " + cityFile.getName(), e);
                }
            }
        } catch (Exception e) {
            android.util.Log.e("Migration", "Prayer times migration failed", e);
        }
    }

    private static boolean isMigrated(Context context) {
        return context.getSharedPreferences(PREF_MIGRATION, Context.MODE_PRIVATE).getBoolean(KEY_MIGRATED, false);
    }

    private static void markMigrated(Context context) {
        context.getSharedPreferences(PREF_MIGRATION, Context.MODE_PRIVATE).edit().putBoolean(KEY_MIGRATED, true).apply();
    }
}
