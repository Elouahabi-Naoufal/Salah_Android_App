package com.salah.times;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

public class JsonStorageManager {
    private static final String STORAGE_DIR = "salah_times";
    private static final String CONFIG_DIR = "config";
    private static final String CITIES_DIR = "cities";
    
    private final Context context;
    private final Gson gson;
    private final File storageRoot;
    private final File configDir;
    private final File citiesDir;

    public JsonStorageManager(Context context) {
        this.context = context;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.storageRoot = new File(context.getFilesDir(), STORAGE_DIR);
        this.configDir = new File(storageRoot, CONFIG_DIR);
        this.citiesDir = new File(storageRoot, CITIES_DIR);
        
        createDirectories();
    }

    private void createDirectories() {
        storageRoot.mkdirs();
        configDir.mkdirs();
        citiesDir.mkdirs();
    }

    // Config files
    public void saveAppConfig(String city, String language) {
        Map<String, String> config = new HashMap<>();
        config.put("city", city);
        config.put("language", language);
        saveJsonFile(new File(configDir, "app_config.json"), config);
    }

    public Map<String, String> loadAppConfig() {
        return loadJsonFile(new File(configDir, "app_config.json"), 
            new TypeToken<Map<String, String>>(){}.getType());
    }

    public void saveIqamaTimes(Map<String, Integer> iqamaTimes) {
        saveJsonFile(new File(configDir, "iqama_times.json"), iqamaTimes);
    }

    public Map<String, Integer> loadIqamaTimes() {
        Map<String, Integer> defaults = new HashMap<>();
        defaults.put("Fajr", 20);
        defaults.put("Dohr", 15);
        defaults.put("Asr", 15);
        defaults.put("Maghreb", 10);
        defaults.put("Isha", 15);
        
        Map<String, Integer> loaded = loadJsonFile(new File(configDir, "iqama_times.json"), 
            new TypeToken<Map<String, Integer>>(){}.getType());
        return loaded != null ? loaded : defaults;
    }

    public void saveNotificationSettings(Map<String, Object> settings) {
        saveJsonFile(new File(configDir, "notifications.json"), settings);
    }

    public Map<String, Object> loadNotificationSettings() {
        return loadJsonFile(new File(configDir, "notifications.json"), 
            new TypeToken<Map<String, Object>>(){}.getType());
    }

    public void saveLastUpdate() {
        Map<String, Object> updateInfo = new HashMap<>();
        updateInfo.put("last_update", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(new Date()));
        updateInfo.put("cities_updated", CitiesData.getAllCities().size());
        saveJsonFile(new File(configDir, "last_update.json"), updateInfo);
    }

    public boolean shouldUpdate() {
        Map<String, Object> updateInfo = loadJsonFile(new File(configDir, "last_update.json"), 
            new TypeToken<Map<String, Object>>(){}.getType());
        
        if (updateInfo == null) return true;
        
        try {
            String lastUpdateStr = (String) updateInfo.get("last_update");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            Date lastUpdate = sdf.parse(lastUpdateStr);
            Date now = new Date();
            
            long daysDiff = (now.getTime() - lastUpdate.getTime()) / (1000 * 60 * 60 * 24);
            return daysDiff >= 1;
        } catch (Exception e) {
            return true;
        }
    }

    // City data files
    public void saveCityData(String cityName, Map<String, PrayerTimes> prayerTimesMap) {
        CityDataWrapper wrapper = new CityDataWrapper();
        wrapper.city = cityName;
        wrapper.last_updated = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(new Date());
        wrapper.prayer_times = prayerTimesMap;
        
        File cityFile = new File(citiesDir, cityName.toLowerCase() + ".json");
        saveJsonFile(cityFile, wrapper);
    }

    public CityDataWrapper loadCityData(String cityName) {
        File cityFile = new File(citiesDir, cityName.toLowerCase() + ".json");
        return loadJsonFile(cityFile, CityDataWrapper.class);
    }

    public PrayerTimes getTodaysPrayerTimes(String cityName) {
        CityDataWrapper cityData = loadCityData(cityName);
        if (cityData == null || cityData.prayer_times == null) return null;
        
        String today = new SimpleDateFormat("dd/MM").format(new Date());
        return cityData.prayer_times.get(today);
    }

    public boolean hasCachedDataForToday(String cityName) {
        return getTodaysPrayerTimes(cityName) != null;
    }

    public int calculateDaysRemaining(String cityName) {
        CityDataWrapper cityData = loadCityData(cityName);
        if (cityData == null || cityData.prayer_times == null) return 0;
        
        try {
            Calendar today = Calendar.getInstance();
            int maxDays = 0;
            
            for (String dateStr : cityData.prayer_times.keySet()) {
                String[] parts = dateStr.split("/");
                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                
                Calendar dateCalendar = Calendar.getInstance();
                dateCalendar.set(Calendar.DAY_OF_MONTH, day);
                dateCalendar.set(Calendar.MONTH, month - 1);
                
                if (dateCalendar.before(today)) {
                    dateCalendar.add(Calendar.YEAR, 1);
                }
                
                long diffInMillis = dateCalendar.getTimeInMillis() - today.getTimeInMillis();
                int daysFromToday = (int) (diffInMillis / (1000 * 60 * 60 * 24));
                maxDays = Math.max(maxDays, daysFromToday);
            }
            
            return Math.max(0, maxDays);
        } catch (Exception e) {
            return 0;
        }
    }

    // Generic JSON operations
    private <T> void saveJsonFile(File file, T data) {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T> T loadJsonFile(File file, Type type) {
        if (!file.exists()) return null;
        
        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private <T> T loadJsonFile(File file, Class<T> clazz) {
        if (!file.exists()) return null;
        
        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Data wrapper classes
    public static class CityDataWrapper {
        public String city;
        public String last_updated;
        public Map<String, PrayerTimes> prayer_times;
    }
}