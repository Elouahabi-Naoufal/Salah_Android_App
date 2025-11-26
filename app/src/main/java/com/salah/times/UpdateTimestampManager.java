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
            File timestampFile = new File(context.getFilesDir(), TIMESTAMP_FILE);
            if (!timestampFile.exists()) {
                Log.d(TAG, "No timestamp file exists, update needed");
                return true;
            }
            
            FileInputStream fis = new FileInputStream(timestampFile);
            InputStreamReader reader = new InputStreamReader(fis);
            JsonObject updateInfo = gson.fromJson(reader, JsonObject.class);
            reader.close();
            
            if (updateInfo == null || !updateInfo.has("last_update")) {
                Log.d(TAG, "Invalid timestamp data, update needed");
                return true;
            }
            
            long lastUpdateTime = updateInfo.get("last_update").getAsLong();
            long currentTime = System.currentTimeMillis();
            long timeDifference = currentTime - lastUpdateTime;
            
            boolean needsUpdate = timeDifference > TimeUnit.HOURS.toMillis(UPDATE_INTERVAL_HOURS);
            
            Log.d(TAG, "Last update: " + new Date(lastUpdateTime));
            Log.d(TAG, "Hours since update: " + TimeUnit.MILLISECONDS.toHours(timeDifference));
            Log.d(TAG, "Needs update: " + needsUpdate);
            
            return needsUpdate;
            
        } catch (Exception e) {
            Log.e(TAG, "Error checking update timestamp", e);
            return true; // If we can't check, assume update is needed
        }
    }
    
    public void saveUpdateTimestamp() {
        saveUpdateTimestamp(CitiesData.getAllCities().size());
    }
    
    public void saveUpdateTimestamp(int citiesUpdated) {
        try {
            JsonObject updateInfo = new JsonObject();
            updateInfo.addProperty("last_update", System.currentTimeMillis());
            updateInfo.addProperty("cities_updated", citiesUpdated);
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
                int citiesUpdated = updateInfo.has("cities_updated") ? updateInfo.get("cities_updated").getAsInt() : 0;
                String updateDate = updateInfo.has("update_date") ? updateInfo.get("update_date").getAsString() : "Unknown";
                
                return new UpdateInfo(lastUpdate, citiesUpdated, updateDate);
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