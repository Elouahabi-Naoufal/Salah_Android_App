package com.salah.times;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OfflineDataManager {
    private static final String TAG = "OfflineDataManager";
    private StorageManager storageManager;
    private PrayerTimesService prayerService;
    private Context context;
    
    public OfflineDataManager(Context context) {
        this.context = context;
        this.storageManager = new StorageManager();
        this.prayerService = new PrayerTimesService();
    }
    
    public PrayerTimes getPrayerTimes(String cityName) {
        if (isOnline() && shouldUpdate(cityName)) {
            return getOnlinePrayerTimes(cityName);
        } else {
            return getOfflinePrayerTimes(cityName);
        }
    }
    
    private PrayerTimes getOnlinePrayerTimes(String cityName) {
        try {
            City city = CitiesData.getCityByName(cityName);
            if (city == null) return getOfflinePrayerTimes(cityName);
            
            PrayerTimes times = PrayerTimesService.fetchPrayerTimes(city).get();
            if (times != null) {
                savePrayerTimesToStorage(cityName, times);
                storageManager.updateLastUpdateTime();
                Log.d(TAG, "Updated online data for " + cityName);
            }
            return times;
        } catch (Exception e) {
            Log.e(TAG, "Online fetch failed, falling back to offline", e);
            return getOfflinePrayerTimes(cityName);
        }
    }
    
    private PrayerTimes getOfflinePrayerTimes(String cityName) {
        try {
            JSONObject data = storageManager.loadCityData(cityName);
            if (data != null) {
                Log.d(TAG, "Loaded offline data for " + cityName);
                return parsePrayerTimesFromJson(data);
            } else {
                Log.w(TAG, "No offline data available for " + cityName);
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading offline data", e);
            return null;
        }
    }
    
    private void savePrayerTimesToStorage(String cityName, PrayerTimes times) {
        try {
            JSONObject data = new JSONObject();
            data.put("city", cityName);
            data.put("date", times.getDate());
            data.put("last_updated", getCurrentDate());
            
            JSONObject prayerData = new JSONObject();
            prayerData.put("fajr", times.getFajr());
            prayerData.put("sunrise", times.getSunrise());
            prayerData.put("dhuhr", times.getDhuhr());
            prayerData.put("asr", times.getAsr());
            prayerData.put("maghrib", times.getMaghrib());
            prayerData.put("isha", times.getIsha());
            
            data.put("prayer_times", prayerData);
            storageManager.saveCityData(cityName, data);
        } catch (Exception e) {
            Log.e(TAG, "Error saving prayer times", e);
        }
    }
    
    private PrayerTimes parsePrayerTimesFromJson(JSONObject data) {
        try {
            JSONObject prayerData = data.getJSONObject("prayer_times");
            String date = data.optString("date", getCurrentDate());
            return new PrayerTimes(
                date,
                prayerData.getString("fajr"),
                prayerData.getString("sunrise"),
                prayerData.getString("dhuhr"),
                prayerData.getString("asr"),
                prayerData.getString("maghrib"),
                prayerData.getString("isha")
            );
        } catch (Exception e) {
            Log.e(TAG, "Error parsing JSON data", e);
            return null;
        }
    }
    
    private boolean shouldUpdate(String cityName) {
        return !storageManager.hasCityData(cityName) || 
               storageManager.isDataExpired(cityName);
    }
    
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) 
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
    
    public String getDataStatus(String cityName) {
        if (isOnline()) {
            return "📶 Online";
        } else if (storageManager.hasCityData(cityName)) {
            if (storageManager.isDataExpired(cityName)) {
                return "📶 Offline - Data expired";
            } else {
                return "📶 Offline - Data available";
            }
        } else {
            return "❌ No data available";
        }
    }
    
    public boolean hasOfflineData(String cityName) {
        return storageManager.hasCityData(cityName);
    }
}