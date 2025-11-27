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
import org.json.JSONArray;

public class StorageManager {
    private static final File BASE_DIR = new File(Environment.getExternalStorageDirectory(), "SalahTimes");
    private static final File CITIES_DIR = new File(BASE_DIR, "cities");
    private static final File CONFIG_DIR = new File(BASE_DIR, "config");
    
    public static void saveCityData(String cityName, JSONObject prayerTimes) {
        try {
            CITIES_DIR.mkdirs();
            File cityFile = new File(CITIES_DIR, cityName.toLowerCase() + ".json");
            
            JSONObject cityData = new JSONObject();
            cityData.put("city", cityName);
            cityData.put("last_updated", getCurrentTimestamp());
            cityData.put("prayer_times", prayerTimes);
            
            FileWriter writer = new FileWriter(cityFile);
            writer.write(cityData.toString(2));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static JSONObject loadCityData(String cityName) {
        try {
            File cityFile = new File(CITIES_DIR, cityName.toLowerCase() + ".json");
            if (!cityFile.exists()) return null;
            
            BufferedReader reader = new BufferedReader(new FileReader(cityFile));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();
            
            return new JSONObject(content.toString());
        } catch (Exception e) {
            return null;
        }
    }
    
    public static void saveLastUpdate() {
        try {
            CONFIG_DIR.mkdirs();
            File updateFile = new File(CONFIG_DIR, "last_update.json");
            
            JSONObject updateData = new JSONObject();
            updateData.put("last_update", getCurrentTimestamp());
            updateData.put("cities_updated", 42);
            
            FileWriter writer = new FileWriter(updateFile);
            writer.write(updateData.toString(2));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static boolean needsUpdate() {
        try {
            File updateFile = new File(CONFIG_DIR, "last_update.json");
            if (!updateFile.exists()) return true;
            
            BufferedReader reader = new BufferedReader(new FileReader(updateFile));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();
            
            JSONObject updateData = new JSONObject(content.toString());
            String lastUpdate = updateData.getString("last_update");
            
            // Check if more than 1 day old
            long lastTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(lastUpdate).getTime();
            long now = System.currentTimeMillis();
            return (now - lastTime) > 24 * 60 * 60 * 1000; // 1 day
        } catch (Exception e) {
            return true;
        }
    }
    
    public static boolean hasCityData(String cityName) {
        File cityFile = new File(CITIES_DIR, cityName.toLowerCase() + ".json");
        return cityFile.exists();
    }
    
    public static boolean isDataExpired(String cityName) {
        return needsUpdate();
    }
    
    public static void updateLastUpdateTime() {
        saveLastUpdate();
    }
    
    public static boolean shouldUpdateToday() {
        try {
            File updateFile = new File(CONFIG_DIR, "last_update.json");
            if (!updateFile.exists()) return true;
            
            BufferedReader reader = new BufferedReader(new FileReader(updateFile));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();
            
            JSONObject updateData = new JSONObject(content.toString());
            String lastUpdate = updateData.getString("last_update");
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            
            return !lastUpdate.startsWith(today);
        } catch (Exception e) {
            return true;
        }
    }
    
    public static int countCityFiles() {
        File[] files = CITIES_DIR.listFiles((dir, name) -> name.endsWith(".json"));
        return files != null ? files.length : 0;
    }
    
    public static void updateLastUpdateWithResults(int cityCount, java.util.List<String> failedCities) {
        try {
            CONFIG_DIR.mkdirs();
            
            JSONObject updateData = new JSONObject();
            updateData.put("last_update", getCurrentTimestamp());
            updateData.put("cities_updated", cityCount);
            updateData.put("total_cities", 42);
            
            if (!failedCities.isEmpty()) {
                org.json.JSONArray failedArray = new org.json.JSONArray();
                for (String city : failedCities) {
                    failedArray.put(city);
                }
                updateData.put("failed_cities", failedArray);
                
                // Save failed cities to separate file
                File failedFile = new File(CONFIG_DIR, "failed_cities.json");
                JSONObject failedData = new JSONObject();
                failedData.put("failed_cities", failedArray);
                failedData.put("timestamp", getCurrentTimestamp());
                
                FileWriter failedWriter = new FileWriter(failedFile);
                failedWriter.write(failedData.toString(2));
                failedWriter.close();
            }
            
            File updateFile = new File(CONFIG_DIR, "last_update.json");
            FileWriter writer = new FileWriter(updateFile);
            writer.write(updateData.toString(2));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void clearAllCityData() {
        try {
            if (CITIES_DIR.exists()) {
                File[] files = CITIES_DIR.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.getName().endsWith(".json")) {
                            file.delete();
                        }
                    }
                }
            }
            
            // Also clear last update
            File updateFile = new File(CONFIG_DIR, "last_update.json");
            if (updateFile.exists()) {
                updateFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(new Date());
    }
}