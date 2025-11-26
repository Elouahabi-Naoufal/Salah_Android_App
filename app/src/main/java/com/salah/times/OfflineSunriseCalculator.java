package com.salah.times;

import android.util.Log;
import java.util.Calendar;
import java.util.Date;

public class OfflineSunriseCalculator {
    private static final String TAG = "OfflineCalculator";
    
    public static String calculateSunrise(String cityName, Date date) {
        try {
            City city = CitiesData.getCityByName(cityName);
            if (city == null) {
                Log.e(TAG, "City not found: " + cityName);
                return null;
            }
            
            double lat = city.getLatitude();
            double lon = city.getLongitude();
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
            
            Log.d(TAG, "Calculating for " + cityName + " at " + lat + ", " + lon);
            Log.d(TAG, "Day of year: " + dayOfYear);
            
            // Solar declination
            double declination = 23.44 * Math.sin(Math.toRadians(360 * (284 + dayOfYear) / 365.0));
            Log.d(TAG, "Declination: " + declination);
            
            // Hour angle calculation
            double latRad = Math.toRadians(lat);
            double decRad = Math.toRadians(declination);
            
            double cosHourAngle = -Math.tan(latRad) * Math.tan(decRad);
            
            // Check for polar regions
            if (Math.abs(cosHourAngle) > 1) {
                Log.e(TAG, "No sunrise/sunset for this location and date");
                return null;
            }
            
            double hourAngle = Math.toDegrees(Math.acos(cosHourAngle));
            
            // Solar noon calculation (Morocco is UTC+1)
            double solarNoon = 12 - (lon / 15.0 - 1);
            
            // Sunrise time
            double sunriseTime = solarNoon - (hourAngle / 15.0);
            
            // Convert to hours and minutes
            int hours = (int) sunriseTime;
            int minutes = (int) ((sunriseTime - hours) * 60);
            
            // Handle negative hours
            if (hours < 0) {
                hours += 24;
            }
            
            String result = String.format("%02d:%02d", hours, minutes);
            Log.d(TAG, "Final sunrise time: " + result);
            
            return result;
            
        } catch (Exception e) {
            Log.e(TAG, "Error calculating sunrise", e);
            return null;
        }
    }
    
    public static PrayerTimes calculateAllPrayerTimes(String cityName, Date date) {
        String sunrise = calculateSunrise(cityName, date);
        if (sunrise == null) {
            return null;
        }
        
        // Use ErrorHandler defaults when offline calculation fails
        if (sunrise == null) {
            return ErrorHandler.SafeDefaults.getDefaultPrayerTimes();
        }
        
        // Return calculated sunrise with reasonable estimates for other prayers
        return new PrayerTimes(
            "Calculated",
            "06:00", // Fajr - estimate
            sunrise, // Sunrise - calculated
            "13:00", // Dhuhr - estimate
            "16:00", // Asr - estimate
            "19:00", // Maghrib - estimate
            "20:30"  // Isha - estimate
        );
    }
}