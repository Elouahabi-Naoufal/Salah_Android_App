package com.salah.times;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PrayerTimeWorker {
    private static final String TAG = "PrayerTimeWorker";
    private static final String CACHE_DIR = "prayer_cache";
    private Context context;
    private Gson gson = new Gson();
    
    public interface PrayerTimeCallback {
        void onSuccess(PrayerTimes prayerTimes);
        void onError(String error);
        void onCachedData(PrayerTimes prayerTimes, int daysRemaining);
    }
    
    public PrayerTimeWorker(Context context) {
        this.context = context;
    }
    
    public void loadPrayerTimes(City city, PrayerTimeCallback callback) {
        // Load cached data immediately
        PrayerTimes cachedData = loadCachedData(city);
        if (cachedData != null) {
            int daysRemaining = calculateDaysRemaining(cachedData);
            callback.onCachedData(cachedData, daysRemaining);
        }
        
        // Check if update needed
        if (shouldUpdateData(city)) {
            updateDataInBackground(city, callback);
        }
    }
    
    private void updateDataInBackground(City city, PrayerTimeCallback callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                // First try web scraping
                return PrayerTimesService.fetchPrayerTimes(city).get();
            } catch (Exception e) {
                Log.w(TAG, "Web scraping failed, trying offline calculation", e);
                // Fallback to offline calculation
                PrayerTimes offlineTimes = OfflineSunriseCalculator.calculateAllPrayerTimes(
                    city.getNameEn(), new java.util.Date());
                if (offlineTimes != null) {
                    return offlineTimes;
                }
                throw new RuntimeException("Both web scraping and offline calculation failed", e);
            }
        }).thenAccept(prayerTimes -> {
            saveCachedData(city, prayerTimes);
            UpdateTimestampManager timestampManager = new UpdateTimestampManager(context);
            timestampManager.saveUpdateTimestamp();
            callback.onSuccess(prayerTimes);
        }).exceptionally(throwable -> {
            // Final fallback to safe defaults
            PrayerTimes defaultTimes = ErrorHandler.SafeDefaults.getDefaultPrayerTimes();
            callback.onSuccess(defaultTimes);
            return null;
        });
    }
    
    private PrayerTimes loadCachedData(City city) {
        try {
            File cacheFile = getCacheFile(city);
            if (!cacheFile.exists()) {
                Log.d(TAG, "No cache file exists for " + city.getNameEn());
                return null;
            }
            
            FileInputStream fis = new FileInputStream(cacheFile);
            InputStreamReader reader = new InputStreamReader(fis);
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> data = gson.fromJson(reader, type);
            reader.close();
            
            if (data != null && data.containsKey("prayer_times")) {
                Map<String, Object> prayerData = (Map<String, Object>) data.get("prayer_times");
                Log.d(TAG, "Loaded cached prayer times for " + city.getNameEn());
                
                // Extract actual cached times if available
                String fajr = getTimeFromCache(prayerData, "fajr", "06:00");
                String sunrise = getTimeFromCache(prayerData, "sunrise", "07:30");
                String dhuhr = getTimeFromCache(prayerData, "dhuhr", "13:00");
                String asr = getTimeFromCache(prayerData, "asr", "16:30");
                String maghrib = getTimeFromCache(prayerData, "maghrib", "19:00");
                String isha = getTimeFromCache(prayerData, "isha", "20:30");
                
                return new PrayerTimes("Cached", fajr, sunrise, dhuhr, asr, maghrib, isha);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading cached data", e);
        }
        return null;
    }
    
    private String getTimeFromCache(Map<String, Object> data, String key, String defaultValue) {
        Object value = data.get(key);
        return value != null ? value.toString() : defaultValue;
    }
    
    private void saveCachedData(City city, PrayerTimes prayerTimes) {
        try {
            File cacheDir = new File(context.getCacheDir(), CACHE_DIR);
            if (!cacheDir.exists()) cacheDir.mkdirs();
            
            File cacheFile = getCacheFile(city);
            Map<String, Object> data = new HashMap<>();
            data.put("city", city.getNameEn());
            data.put("last_updated", System.currentTimeMillis());
            
            // Save prayer times as individual fields for easier extraction
            Map<String, String> prayerData = new HashMap<>();
            prayerData.put("fajr", prayerTimes.getFajr());
            prayerData.put("sunrise", prayerTimes.getSunrise());
            prayerData.put("dhuhr", prayerTimes.getDhuhr());
            prayerData.put("asr", prayerTimes.getAsr());
            prayerData.put("maghrib", prayerTimes.getMaghrib());
            prayerData.put("isha", prayerTimes.getIsha());
            data.put("prayer_times", prayerData);
            
            FileOutputStream fos = new FileOutputStream(cacheFile);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            gson.toJson(data, writer);
            writer.close();
            
            Log.d(TAG, "Cached prayer times saved for " + city.getNameEn());
        } catch (Exception e) {
            Log.e(TAG, "Error saving cached data", e);
        }
    }
    
    private boolean shouldUpdateData(City city) {
        UpdateTimestampManager timestampManager = new UpdateTimestampManager(context);
        return timestampManager.shouldUpdateData();
    }
    
    private int calculateDaysRemaining(PrayerTimes prayerTimes) {
        // Simplified calculation - assume 7 days of cached data
        return 7;
    }
    
    private File getCacheFile(City city) {
        File cacheDir = new File(context.getCacheDir(), CACHE_DIR);
        return new File(cacheDir, city.getNameEn().toLowerCase() + ".json");
    }
}