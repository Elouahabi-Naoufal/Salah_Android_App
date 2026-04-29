package com.salah.times;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class OfflineDataManager {
    private static final String TAG = "OfflineDataManager";
    private final Context context;

    public OfflineDataManager(Context context) {
        this.context = context;
    }

    public PrayerTimes getPrayerTimes(String cityName) {
        City city = CitiesData.getCityByName(cityName);
        if (city == null) return null;
        try {
            return PrayerTimesService.fetchPrayerTimes(city).get();
        } catch (Exception e) {
            Log.e(TAG, "Failed to get prayer times for " + cityName, e);
            return null;
        }
    }

    public boolean hasOfflineData(String cityName) {
        return StorageManager.hasCityData(cityName);
    }

    public String getDataStatus(String cityName) {
        if (isOnline()) return "📶 Online";
        return hasOfflineData(cityName) ? "📶 Offline - Data available" : "❌ No data available";
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
