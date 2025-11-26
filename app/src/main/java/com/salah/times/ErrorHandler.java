package com.salah.times;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ErrorHandler {
    private static final String TAG = "ErrorHandler";
    private static final String ERROR_LOG_FILE = "error_log.txt";
    private Context context;
    
    public ErrorHandler(Context context) {
        this.context = context;
    }
    
    public static void handleNetworkError(Context context, Exception e) {
        Log.e(TAG, "Network error occurred", e);
        showUserMessage(context, "Network connection failed. Using cached data.");
        logError(context, "Network Error", e);
    }
    
    public static void handleFileError(Context context, Exception e) {
        Log.e(TAG, "File operation error", e);
        showUserMessage(context, "Configuration error. Using defaults.");
        logError(context, "File Error", e);
    }
    
    public static void handleParsingError(Context context, Exception e) {
        Log.e(TAG, "Data parsing error", e);
        showUserMessage(context, "Data format error. Retrying...");
        logError(context, "Parsing Error", e);
    }
    
    public static void handleApiError(Context context, Exception e) {
        Log.e(TAG, "API error occurred", e);
        showUserMessage(context, "Service unavailable. Using fallback data.");
        logError(context, "API Error", e);
    }
    
    public static boolean validatePrayerTime(String timeStr) {
        try {
            String[] parts = timeStr.split(":");
            if (parts.length != 2) return false;
            
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            
            return hours >= 0 && hours <= 23 && minutes >= 0 && minutes <= 59;
        } catch (Exception e) {
            return false;
        }
    }
    
    public static boolean isNetworkAvailable(Context context) {
        try {
            android.net.ConnectivityManager cm = 
                (android.net.ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            android.net.NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } catch (Exception e) {
            Log.e(TAG, "Error checking network availability", e);
            return false;
        }
    }
    
    public static void safeFileOperation(Context context, String filename, FileOperation operation) {
        try {
            File file = new File(context.getFilesDir(), filename);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            operation.execute(file);
        } catch (Exception e) {
            handleFileError(context, e);
        }
    }
    
    private static void showUserMessage(Context context, String message) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
    
    private static void logError(Context context, String errorType, Exception e) {
        try {
            File logFile = new File(context.getFilesDir(), ERROR_LOG_FILE);
            FileWriter writer = new FileWriter(logFile, true);
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String timestamp = dateFormat.format(new Date());
            
            writer.write(String.format("[%s] %s: %s\n", timestamp, errorType, e.getMessage()));
            writer.close();
        } catch (IOException ioException) {
            Log.e(TAG, "Failed to write error log", ioException);
        }
    }
    
    public interface FileOperation {
        void execute(File file) throws Exception;
    }
    
    public static class SafeDefaults {
        public static final String DEFAULT_CITY = "Casablanca";
        public static final String DEFAULT_LANGUAGE = "en";
        public static final String DEFAULT_TIME = "00:00";
        public static final int DEFAULT_IQAMA_DELAY = 15;
        
        public static PrayerTimes getDefaultPrayerTimes() {
            return new PrayerTimes(
                "Default",
                "06:00", // Fajr
                "07:30", // Sunrise
                "13:00", // Dhuhr
                "16:30", // Asr
                "19:00", // Maghrib
                "20:30"  // Isha
            );
        }
    }
}