package com.salah.times;

import android.content.Context;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TestingManager {
    
    /**
     * Generate test prayer times with 15-second intervals
     * This creates REAL prayer times that will trigger actual notifications and iqama countdowns
     * 
     * Timeline:
     * - Fajr: Now + 15 seconds
     * - Dhuhr: Now + 30 seconds  
     * - Asr: Now + 45 seconds
     * - Maghrib: Now + 60 seconds
     * - Isha: Now + 75 seconds
     */
    public static PrayerTimes getTestPrayerTimes() {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(now.getTime());
        
        String fajr = getTimeAfterSeconds(now, 15);      // 15 seconds from now
        String sunrise = getTimeAfterSeconds(now, 20);   // 20 seconds (between fajr and dhuhr)
        String dhuhr = getTimeAfterSeconds(now, 30);     // 30 seconds from now
        String asr = getTimeAfterSeconds(now, 45);       // 45 seconds from now
        String maghrib = getTimeAfterSeconds(now, 60);   // 60 seconds from now
        String isha = getTimeAfterSeconds(now, 75);      // 75 seconds from now
        
        return new PrayerTimes(today, fajr, sunrise, dhuhr, asr, maghrib, isha);
    }
    
    /**
     * Generate test times for iqama countdown testing
     * Sets Dhuhr to 2 minutes AGO so iqama countdown appears immediately
     */
    public static PrayerTimes getIqamaTestTimes() {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(now.getTime());
        
        String fajr = getTimeAfterSeconds(now, -300);    // 5 minutes ago (passed)
        String sunrise = getTimeAfterSeconds(now, -240); // 4 minutes ago
        String dhuhr = getTimeAfterSeconds(now, -120);   // 2 minutes ago (iqama countdown should show)
        String asr = getTimeAfterSeconds(now, 300);      // 5 minutes from now (next prayer)
        String maghrib = getTimeAfterSeconds(now, 600);  // 10 minutes from now
        String isha = getTimeAfterSeconds(now, 900);     // 15 minutes from now
        
        return new PrayerTimes(today, fajr, sunrise, dhuhr, asr, maghrib, isha);
    }
    
    /**
     * Helper method to get time string after adding seconds
     */
    private static String getTimeAfterSeconds(Calendar calendar, int seconds) {
        Calendar cal = (Calendar) calendar.clone();
        cal.add(Calendar.SECOND, seconds);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return timeFormat.format(cal.getTime());
    }
    
    /**
     * Save test prayer times to database and schedule alarms
     * This makes the test times REAL - they will trigger actual notifications
     */
    public static void activateTestMode(Context context, PrayerTimes testTimes) {
        String cityName = SettingsManager.getDefaultCity();
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        
        // Save test times to database
        City city = CitiesData.getCityByName(cityName);
        if (city != null) {
            String ddMm = new SimpleDateFormat("dd/MM", Locale.getDefault()).format(new java.util.Date());
            db.savePrayerTimes(
                city.getTableName(),
                ddMm,
                testTimes.getFajr(),
                testTimes.getDhuhr(),
                testTimes.getAsr(),
                testTimes.getMaghrib(),
                testTimes.getIsha()
            );
        }
        
        // Schedule real alarms for test times
        PrayerAlarmScheduler.schedulePrayerAlarms(context, testTimes);
    }
    
    /**
     * Test notification immediately without waiting
     */
    public static void triggerTestNotification(Context context, String prayerName) {
        android.content.Intent intent = new android.content.Intent(context, PrayerAlarmReceiver.class);
        intent.putExtra("prayer_name", prayerName);
        context.sendBroadcast(intent);
    }
}
