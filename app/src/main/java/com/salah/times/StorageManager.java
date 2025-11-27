package com.salah.times;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import org.json.JSONObject;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StorageManager {
    private static final String TAG = "StorageManager";
    private static final String APP_FOLDER = "SalahTimes";
    private static final String CITIES_FOLDER = "cities";
    private static final String CONFIG_FOLDER = "config";
    
    private File appDir;
    private File citiesDir;
    private File configDir;
    
    public StorageManager() {
        initDirectories();
    }
    
    private void initDirectories() {
        File externalStorage = Environment.getExternalStorageDirectory();
        appDir = new File(externalStorage, APP_FOLDER);
        citiesDir = new File(appDir, CITIES_FOLDER);
        configDir = new File(appDir, CONFIG_FOLDER);
        
        createDirectories();
    }
    
    private void createDirectories() {
        if (!appDir.exists()) appDir.mkdirs();
        if (!citiesDir.exists()) citiesDir.mkdirs();
        if (!configDir.exists()) configDir.mkdirs();
    }
    
    public void saveCityData(String cityName, JSONObject data) {
        try {
            File cityFile = new File(citiesDir, cityName.toLowerCase() + ".json");
            FileWriter writer = new FileWriter(cityFile);
            writer.write(data.toString());
            writer.close();
            Log.d(TAG, "Saved data for " + cityName);
        } catch (IOException e) {
            Log.e(TAG, "Error saving city data", e);
        }
    }
    
    public JSONObject loadCityData(String cityName) {
        try {
            File cityFile = new File(citiesDir, cityName.toLowerCase() + ".json");
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
            Log.e(TAG, "Error loading city data", e);
            return null;
        }
    }
    
    public boolean hasCityData(String cityName) {
        File cityFile = new File(citiesDir, cityName.toLowerCase() + ".json");
        return cityFile.exists();
    }
    
    public boolean isDataExpired(String cityName) {
        try {
            JSONObject data = loadCityData(cityName);
            if (data == null) return true;
            
            String lastUpdated = data.getString("last_updated");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date updateDate = sdf.parse(lastUpdated);
            Date today = new Date();
            
            long diffInMillis = today.getTime() - updateDate.getTime();
            long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);
            
            return diffInDays >= 1;
        } catch (Exception e) {
            return true;
        }
    }
    
    public void saveConfig(String key, String value) {
        try {
            File configFile = new File(configDir, key + ".json");
            JSONObject config = new JSONObject();
            config.put("value", value);
            config.put("timestamp", System.currentTimeMillis());
            
            FileWriter writer = new FileWriter(configFile);
            writer.write(config.toString());
            writer.close();
        } catch (Exception e) {
            Log.e(TAG, "Error saving config", e);
        }
    }
    
    public String loadConfig(String key, String defaultValue) {
        try {
            File configFile = new File(configDir, key + ".json");
            if (!configFile.exists()) return defaultValue;
            
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();
            
            JSONObject config = new JSONObject(content.toString());
            return config.getString("value");
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    public void updateLastUpdateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        saveConfig("last_update", sdf.format(new Date()));
    }
    
    public boolean isStorageAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}