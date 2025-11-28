package com.salah.times;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class UpdateTimestampManager {
    private static final String TAG = "UpdateTimestamp";
    private static final String TIMESTAMP_FILE = "last_update.json";
    private static final long UPDATE_INTERVAL_HOURS = 24; // 24 hours
    
    private Context context;
    private Gson gson = new Gson();
    
    public UpdateTimestampManager(Context context) {
        this.context = context;
    }
    
    public boolean shouldUpdateData() {
        try {
            // First check if we have all 43 cities
            int existingCities = countExistingCityJsonFiles();
            if (existingCities < 43) {
                Log.d(TAG, "Missing cities: " + existingCities + "/43, update needed");
                return true;
            }
            
            // Only check date if we have all cities
            File timestampFile = new File(context.getFilesDir(), TIMESTAMP_FILE);
            if (!timestampFile.exists()) {
                return false; // Have all cities, no need to update
            }
            
            FileInputStream fis = new FileInputStream(timestampFile);
            InputStreamReader reader = new InputStreamReader(fis);
            JsonObject updateInfo = gson.fromJson(reader, JsonObject.class);
            reader.close();
            
            if (updateInfo == null || !updateInfo.has("last_update")) {
                return false;
            }
            
            long lastUpdateTime = updateInfo.get("last_update").getAsLong();
            long currentTime = System.currentTimeMillis();
            long timeDifference = currentTime - lastUpdateTime;
            
            return timeDifference > TimeUnit.HOURS.toMillis(UPDATE_INTERVAL_HOURS);
            
        } catch (Exception e) {
            return true;
        }
    }
    
    private int countExistingCityJsonFiles() {
        try {
            File citiesDir = new File(context.getFilesDir(), "salah_times/cities");
            if (!citiesDir.exists()) {
                return 0;
            }
            
            int count = 0;
            for (City city : CitiesData.getAllCities()) {
                File cityFile = new File(citiesDir, city.getNameEn().toLowerCase() + ".json");
                if (cityFile.exists()) {
                    count++;
                }
            }
            return count;
        } catch (Exception e) {
            return 0;
        }
    }


    
    public void saveUpdateTimestamp() {
        saveUpdateTimestamp(CitiesData.getAllCities().size());
    }
    
    public void saveUpdateTimestamp(int citiesUpdated) {
        try {
            JsonObject updateInfo = new JsonObject();
            updateInfo.addProperty("last_update", System.currentTimeMillis());
            updateInfo.addProperty("update_date", getCurrentDateString());
            
            File timestampFile = new File(context.getFilesDir(), TIMESTAMP_FILE);
            FileOutputStream fos = new FileOutputStream(timestampFile);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            gson.toJson(updateInfo, writer);
            writer.close();
            
            Log.d(TAG, "Update timestamp saved: " + getCurrentDateString());
            
        } catch (Exception e) {
            Log.e(TAG, "Error saving update timestamp", e);
        }
    }
    
    public UpdateInfo getLastUpdateInfo() {
        try {
            File timestampFile = new File(context.getFilesDir(), TIMESTAMP_FILE);
            if (!timestampFile.exists()) {
                return new UpdateInfo(0, 0, "Never");
            }
            
            FileInputStream fis = new FileInputStream(timestampFile);
            InputStreamReader reader = new InputStreamReader(fis);
            JsonObject updateInfo = gson.fromJson(reader, JsonObject.class);
            reader.close();
            
            if (updateInfo != null) {
                long lastUpdate = updateInfo.has("last_update") ? updateInfo.get("last_update").getAsLong() : 0;
                String updateDate = updateInfo.has("update_date") ? updateInfo.get("update_date").getAsString() : "Unknown";
                
                return new UpdateInfo(lastUpdate, 0, updateDate);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error reading update info", e);
        }
        
        return new UpdateInfo(0, 0, "Error");
    }
    
    public long getHoursSinceLastUpdate() {
        UpdateInfo info = getLastUpdateInfo();
        if (info.lastUpdateTime == 0) {
            return Long.MAX_VALUE; // Never updated
        }
        
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - info.lastUpdateTime;
        return TimeUnit.MILLISECONDS.toHours(timeDifference);
    }
    
    public boolean isDataFresh() {
        return getHoursSinceLastUpdate() < UPDATE_INTERVAL_HOURS;
    }
    
    public void forceUpdate() {
        try {
            File timestampFile = new File(context.getFilesDir(), TIMESTAMP_FILE);
            if (timestampFile.exists()) {
                timestampFile.delete();
            }
            Log.d(TAG, "Forced update by deleting timestamp file");
        } catch (Exception e) {
            Log.e(TAG, "Error forcing update", e);
        }
    }
    
    private String getCurrentDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date());
    }
    
    public static class UpdateInfo {
        public final long lastUpdateTime;
        public final int citiesUpdated;
        public final String updateDate;
        
        public UpdateInfo(long lastUpdateTime, int citiesUpdated, String updateDate) {
            this.lastUpdateTime = lastUpdateTime;
            this.citiesUpdated = citiesUpdated;
            this.updateDate = updateDate;
        }
        
        public String getFormattedLastUpdate() {
            if (lastUpdateTime == 0) {
                return "Never updated";
            }
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            return dateFormat.format(new Date(lastUpdateTime));
        }
        
        public boolean isValid() {
            return lastUpdateTime > 0 && citiesUpdated > 0;
        }
    }
}