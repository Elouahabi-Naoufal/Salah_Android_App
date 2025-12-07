package com.salah.times;

import android.content.Context;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StorageManager {
    private static Context appContext;
    
    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    public static void saveCityData(String cityName, JSONObject monthData) {
        try {
            DatabaseHelper db = DatabaseHelper.getInstance(appContext);
            java.util.Iterator<String> keys = monthData.keys();
            
            while (keys.hasNext()) {
                String dateKey = keys.next();
                JSONObject dayData = monthData.getJSONObject(dateKey);
                
                if (!dayData.has("Fajr")) continue;
                
                String fullDate = convertToFullDate(dayData.getString("Date"));
                
                db.savePrayerTimes(
                    cityName,
                    fullDate,
                    dayData.getString("Fajr"),
                    "00:00",
                    dayData.getString("Dohr"),
                    dayData.getString("Asr"),
                    dayData.getString("Maghreb"),
                    dayData.getString("Isha")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static String convertToFullDate(String ddMM) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputFormat.parse(ddMM);
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(date);
            cal.set(java.util.Calendar.YEAR, java.util.Calendar.getInstance().get(java.util.Calendar.YEAR));
            return outputFormat.format(cal.getTime());
        } catch (Exception e) {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        }
    }
    
    public static JSONObject loadCityData(String cityName) {
        try {
            DatabaseHelper db = DatabaseHelper.getInstance(appContext);
            String today = new SimpleDateFormat("dd/MM", Locale.getDefault()).format(new Date());
            String todayFull = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            
            PrayerTimes times = db.loadPrayerTimes(cityName, todayFull);
            if (times == null) return null;
            
            JSONObject cityData = new JSONObject();
            cityData.put("city", cityName);
            cityData.put("date", todayFull);
            
            JSONObject prayerTimes = new JSONObject();
            JSONObject todayData = new JSONObject();
            todayData.put("Date", today);
            todayData.put("Fajr", times.getFajr());
            todayData.put("Dohr", times.getDhuhr());
            todayData.put("Asr", times.getAsr());
            todayData.put("Maghreb", times.getMaghrib());
            todayData.put("Isha", times.getIsha());
            prayerTimes.put(today, todayData);
            
            cityData.put("prayer_times", prayerTimes);
            return cityData;
        } catch (Exception e) {
            return null;
        }
    }
    
    public static void saveLastUpdate() {
        DatabaseHelper db = DatabaseHelper.getInstance(appContext);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        db.setLastUpdateDate(today);
    }
    
    public static boolean needsUpdate() {
        try {
            DatabaseHelper db = DatabaseHelper.getInstance(appContext);
            String lastUpdateDate = db.getLastUpdateDate();
            if (lastUpdateDate == null) return true;
            
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            return !today.equals(lastUpdateDate);
        } catch (Exception e) {
            return true;
        }
    }
    
    public static boolean hasCityData(String cityName) {
        DatabaseHelper db = DatabaseHelper.getInstance(appContext);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return db.loadPrayerTimes(cityName, today) != null;
    }
    
    public static boolean isDataExpired(String cityName) {
        return needsUpdate();
    }
    
    public static void updateLastUpdateTime() {
        saveLastUpdate();
    }
    
    public static boolean shouldUpdateToday() {
        return needsUpdate();
    }
    
    public static int countCityFiles() {
        return 0;
    }
    
    public static void updateLastUpdateWithResults(int cityCount, java.util.List<String> failedCities) {
        saveLastUpdate();
    }
    
    public static void clearAllCityData() {
        DatabaseHelper db = DatabaseHelper.getInstance(appContext);
        db.clearAllData();
    }
    
    private static String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(new Date());
    }
}