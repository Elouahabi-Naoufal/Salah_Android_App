package com.salah.times;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PrayerHighlightManager {
    private static final String[] PRAYER_ORDER = {"Fajr", "Dohr", "Asr", "Maghreb", "Isha"};
    
    public static String getCurrentPrayer(PrayerTimes prayerTimes) {
        if (prayerTimes == null) return null;
        
        Calendar now = Calendar.getInstance();
        int currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);
        
        String[] times = {
            prayerTimes.getFajr(),
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
        
        // After Isha, next is tomorrow's Fajr (matching Python logic)
        return "Fajr";
    }
    
    public static boolean isCurrentPrayer(String prayer, PrayerTimes prayerTimes) {
        String currentPrayer = getCurrentPrayer(prayerTimes);
        return prayer.equals(currentPrayer);
    }
    
    public static long getTimeUntilNextPrayer(PrayerTimes prayerTimes) {
        if (prayerTimes == null) return 0;
        
        Calendar now = Calendar.getInstance();
        int currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);
        
        String[] times = {
            prayerTimes.getFajr(),
            prayerTimes.getDhuhr(),
            prayerTimes.getAsr(),
            prayerTimes.getMaghrib(),
            prayerTimes.getIsha()
        };
        
        // Check for next prayer today
        for (int i = 0; i < PRAYER_ORDER.length; i++) {
            int prayerMinutes = parseTimeToMinutes(times[i]);
            if (currentMinutes < prayerMinutes) {
                return (prayerMinutes - currentMinutes) * 60 * 1000;
            }
        }
        
        // After Isha: calculate time to tomorrow's Fajr (Python logic)
        int fajrMinutes = parseTimeToMinutes(prayerTimes.getFajr());
        int minutesUntilTomorrowFajr = (24 * 60) - currentMinutes + fajrMinutes;
        return minutesUntilTomorrowFajr * 60 * 1000;
    }
    
    private static String getTimeForPrayer(String prayer, PrayerTimes prayerTimes) {
        switch (prayer) {
            case "Fajr": return prayerTimes.getFajr();
            case "Dohr": return prayerTimes.getDhuhr();
            case "Asr": return prayerTimes.getAsr();
            case "Maghreb": return prayerTimes.getMaghrib();
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