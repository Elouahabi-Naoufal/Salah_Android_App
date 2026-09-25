package com.salah.times;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class IqamaManager {
    private static final String IQAMA_CONFIG_FILE = "iqama_times.json";
    private Context context;
    private Gson gson = new Gson();
    
    public IqamaManager(Context context) {
        this.context = context;
        migrateLegacyFileOnce();
    }

    /**
     * One-time migration: the pre-2026 file store (iqama_times.json with
     * Fajr/Dhuhr/Asr/Maghrib/Isha keys) moves into the DB so Settings and
     * the countdown card share one source of truth. File is removed after.
     */
    private void migrateLegacyFileOnce() {
        try {
            File file = new File(context.getFilesDir(), IQAMA_CONFIG_FILE);
            if (!file.exists()) return;
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(fis);
            Type type = new TypeToken<Map<String, Integer>>(){}.getType();
            Map<String, Integer> legacy = gson.fromJson(reader, type);
            reader.close();
            if (legacy != null) {
                for (Map.Entry<String, Integer> e : legacy.entrySet()) {
                    if (e.getValue() != null) SettingsManager.setIqamaDelay(e.getKey(), e.getValue());
                }
            }
            file.delete();
        } catch (Exception e) {
            // Best effort: DB defaults apply
        }
    }
    
    public void saveIqamaTimes() {
        // No-op: values are persisted to the DB on every setIqamaDelay call.
    }
    
    /** Single source of truth: the DB (shared with the countdown card). */
    public int getIqamaDelay(String prayer) {
        try {
            return SettingsManager.getIqamaDelay(prayer);
        } catch (Exception e) {
            return 15;
        }
    }
    
    public void setIqamaDelay(String prayer, int minutes) {
        try {
            SettingsManager.setIqamaDelay(prayer, minutes);
        } catch (Exception e) {
            // Persist on next successful write
        }
    }
    
    public String getIqamaCountdown(String prayer, String prayerTime) {
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date prayerDateTime = timeFormat.parse(prayerTime);
            
            Calendar prayerCal = Calendar.getInstance();
            prayerCal.setTime(prayerDateTime);
            
            Calendar iqamaCal = (Calendar) prayerCal.clone();
            iqamaCal.add(Calendar.MINUTE, getIqamaDelay(prayer));
            
            Calendar now = Calendar.getInstance();
            
            long diffMillis = iqamaCal.getTimeInMillis() - now.getTimeInMillis();
            
            if (diffMillis <= 0) {
                return TranslationManager.tr("iqama_passed").replace("{}", TranslationManager.tr(prayer.toLowerCase()));
            }
            
            long hours = diffMillis / (60 * 60 * 1000);
            long minutes = (diffMillis % (60 * 60 * 1000)) / (60 * 1000);
            long seconds = (diffMillis % (60 * 1000)) / 1000;
            
            String prayerName = TranslationManager.tr(prayer.toLowerCase());
            return String.format("Time before Iqama of %s: %02d:%02d:%02d", prayerName, hours, minutes, seconds);
            
        } catch (ParseException e) {
            return "Iqama time calculation error";
        }
    }
    
    public boolean isIqamaTime(String prayer, String prayerTime) {
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date prayerDateTime = timeFormat.parse(prayerTime);
            
            Calendar prayerCal = Calendar.getInstance();
            prayerCal.setTime(prayerDateTime);
            
            Calendar iqamaCal = (Calendar) prayerCal.clone();
            iqamaCal.add(Calendar.MINUTE, getIqamaDelay(prayer));
            
            Calendar now = Calendar.getInstance();
            
            return now.after(prayerCal) && now.before(iqamaCal);
            
        } catch (ParseException e) {
            return false;
        }
    }
}