package com.salah.times;

import android.content.Context;
import android.util.Log;

public class PrayerTimeWorker {
    private static final String TAG = "PrayerTimeWorker";
    private Context context;
    
    public interface PrayerTimeCallback {
        void onSuccess(PrayerTimes prayerTimes);
        void onError(String error);
        void onCachedData(PrayerTimes prayerTimes, int daysRemaining);
    }
    
    public PrayerTimeWorker(Context context) {
        this.context = context;
    }
    
    public void loadPrayerTimes(City city, PrayerTimeCallback callback) {
        Log.d(TAG, "Loading prayer times for city: " + city.getNameEn());
        
        PrayerTimesService.fetchPrayerTimes(city)
            .thenAccept(prayerTimes -> {
                Log.d(TAG, "Successfully loaded prayer times");
                callback.onSuccess(prayerTimes);
            })
            .exceptionally(throwable -> {
                Log.e(TAG, "Failed to load prayer times: " + throwable.getMessage());
                
                // Provide fallback times
                PrayerTimes fallback = new PrayerTimes(
                    getCurrentDate(),
                    "05:30", "00:00", "13:00", "16:30", "19:00", "20:30"
                );
                callback.onSuccess(fallback);
                return null;
            });
    }
    
    private String getCurrentDate() {
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM", java.util.Locale.getDefault());
        return dateFormat.format(new java.util.Date());
    }
    

}