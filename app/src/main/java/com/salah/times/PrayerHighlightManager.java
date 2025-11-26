package com.salah.times;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PrayerHighlightManager {
    private static final String[] PRAYER_ORDER = {"Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha"};
    
    public static String getCurrentPrayer(PrayerTimes prayerTimes) {
        if (prayerTimes == null) return null;
        
        Calendar now = Calendar.getInstance();
        int currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);
        
        String[] times = {
            prayerTimes.getFajr(),
            prayerTimes.getSunrise(),
            prayerTimes.getDhuhr(),
            prayerTimes.getAsr(),
            prayerTimes.getMaghrib(),
            prayerTimes.getIsha()
        };
        
        for (int i = 0; i < PRAYER_ORDER.length; i++) {
            int prayerMinutes = parseTimeToMinutes(times[i]);
            int nextPrayerMinutes;
            
            if (i < PRAYER_ORDER.length - 1) {
                nextPrayerMinutes = parseTimeToMinutes(times[i + 1]);
            } else {
                nextPrayerMinutes = 24 * 60; // End of day
            }
            
            if (prayerMinutes <= currentMinutes && currentMinutes < nextPrayerMinutes) {
                return PRAYER_ORDER[i];
            }
        }
        
        return null; // No current prayer (shouldn't happen)
    }
    
    public static String getNextPrayer(PrayerTimes prayerTimes) {
        if (prayerTimes == null) return null;
        
        Calendar now = Calendar.getInstance();
        int currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);
        
        String[] times = {
            prayerTimes.getFajr(),
            prayerTimes.getSunrise(),
            prayerTimes.getDhuhr(),
            prayerTimes.getAsr(),
            prayerTimes.getMaghrib(),
            prayerTimes.getIsha()
        };
        
        for (int i = 0; i < PRAYER_ORDER.length; i++) {
            int prayerMinutes = parseTimeToMinutes(times[i]);
            if (currentMinutes < prayerMinutes) {
                return PRAYER_ORDER[i];
            }
        }
        
        // If past all prayers, next is tomorrow's Fajr
        return "Fajr";
    }
    
    public static boolean isCurrentPrayer(String prayer, PrayerTimes prayerTimes) {
        String currentPrayer = getCurrentPrayer(prayerTimes);
        return prayer.equals(currentPrayer) || 
               (prayer.equals("Chorok") && "Sunrise".equals(currentPrayer));
    }
    
    public static long getTimeUntilNextPrayer(PrayerTimes prayerTimes) {
        if (prayerTimes == null) return 0;
        
        String nextPrayer = getNextPrayer(prayerTimes);
        if (nextPrayer == null) return 0;
        
        Calendar now = Calendar.getInstance();
        int currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);
        
        String nextPrayerTime = getTimeForPrayer(nextPrayer, prayerTimes);
        int nextPrayerMinutes = parseTimeToMinutes(nextPrayerTime);
        
        // If next prayer is tomorrow's Fajr
        if (nextPrayerMinutes <= currentMinutes) {
            nextPrayerMinutes += 24 * 60; // Add 24 hours
        }
        
        return (nextPrayerMinutes - currentMinutes) * 60 * 1000; // Convert to milliseconds
    }
    
    private static String getTimeForPrayer(String prayer, PrayerTimes prayerTimes) {
        switch (prayer) {
            case "Fajr": return prayerTimes.getFajr();
            case "Sunrise": return prayerTimes.getSunrise();
            case "Dhuhr": return prayerTimes.getDhuhr();
            case "Asr": return prayerTimes.getAsr();
            case "Maghrib": return prayerTimes.getMaghrib();
            case "Isha": return prayerTimes.getIsha();
            default: return "00:00";
        }
    }
    
    private static int parseTimeToMinutes(String timeStr) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date time = format.parse(timeStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(time);
            return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
        } catch (ParseException e) {
            return 0; // Fallback for invalid times
        }
    }
}