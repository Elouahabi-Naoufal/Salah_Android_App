package com.salah.times;

import android.content.Context;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TestingManager {
    private static final String PREFS_NAME = "testing_prefs";
    private static final String KEY_TESTING_MODE = "testing_mode";
    private static final String KEY_TEST_FAJR = "test_fajr";
    private static final String KEY_TEST_DHUHR = "test_dhuhr";
    private static final String KEY_TEST_ASR = "test_asr";
    private static final String KEY_TEST_MAGHRIB = "test_maghrib";
    private static final String KEY_TEST_ISHA = "test_isha";
    
    public static boolean isTestingMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_TESTING_MODE, false);
    }
    
    public static void setTestingMode(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_TESTING_MODE, enabled).apply();
        
        if (!enabled) {
            // Cancel test alarm when disabling
            android.app.AlarmManager alarmManager = (android.app.AlarmManager) context.getSystemService(android.content.Context.ALARM_SERVICE);
            if (alarmManager != null) {
                android.content.Intent intent = new android.content.Intent(context, com.salah.times.PrayerAlarmReceiver.class);
                android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(context, 9999, intent, 
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);
                alarmManager.cancel(pendingIntent);
            }
        }
    }
    
    public static void setTestTime(Context context, String prayer, String time) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String key = getKeyForPrayer(prayer);
        if (key != null) {
            prefs.edit().putString(key, time).apply();
        }
    }
    
    public static PrayerTimes getTestPrayerTimes(Context context, PrayerTimes original) {
        if (!isTestingMode(context)) return original;
        
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        String fajr = prefs.getString(KEY_TEST_FAJR, original.getFajr());
        String dhuhr = prefs.getString(KEY_TEST_DHUHR, original.getDhuhr());
        String asr = prefs.getString(KEY_TEST_ASR, original.getAsr());
        String maghrib = prefs.getString(KEY_TEST_MAGHRIB, original.getMaghrib());
        String isha = prefs.getString(KEY_TEST_ISHA, original.getIsha());
        
        return new PrayerTimes(original.getDate(), fajr, original.getSunrise(), dhuhr, asr, maghrib, isha);
    }
    
    public static void setNextPrayerInMinutes(Context context, int minutes) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, minutes);
        
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String testTime = timeFormat.format(now.getTime());
        
        // Set the next prayer to this time
        String nextPrayer = getCurrentOrNextPrayer();
        setTestTime(context, nextPrayer, testTime);
        setTestingMode(context, true);
        
        // Force immediate alarm scheduling
        scheduleTestAlarm(context, testTime, nextPrayer);
    }
    
    public static void setNextPrayerInSeconds(Context context, int seconds) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.SECOND, seconds);
        
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String testTime = timeFormat.format(now.getTime());
        
        String nextPrayer = getCurrentOrNextPrayer();
        setTestTime(context, nextPrayer, testTime);
        setTestingMode(context, true);
        
        // Schedule immediate test alarm
        scheduleTestAlarm(context, now.getTimeInMillis(), nextPrayer);
    }
    
    private static void scheduleTestAlarm(Context context, String time, String prayerName) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
            java.util.Date prayerTime = format.parse(time);
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(prayerTime);
            
            Calendar now = Calendar.getInstance();
            calendar.set(Calendar.YEAR, now.get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, now.get(Calendar.MONTH));
            calendar.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
            
            scheduleTestAlarm(context, calendar.getTimeInMillis(), prayerName);
        } catch (Exception e) {
            android.util.Log.e("TestingManager", "Error scheduling test alarm", e);
        }
    }
    
    private static void scheduleTestAlarm(Context context, long timeInMillis, String prayerName) {
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) context.getSystemService(android.content.Context.ALARM_SERVICE);
        if (alarmManager == null) return;
        
        android.content.Intent intent = new android.content.Intent(context, com.salah.times.PrayerAlarmReceiver.class);
        intent.putExtra("prayer_name", prayerName);
        intent.putExtra("is_test", true);
        
        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(context, 9999, intent, 
            android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);
        
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            } else {
                alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            }
            android.util.Log.d("TestingManager", "Scheduled test alarm for " + prayerName + " at " + new java.util.Date(timeInMillis));
        } catch (SecurityException e) {
            android.util.Log.e("TestingManager", "Permission denied for test alarm", e);
        }
    }
    
    private static String getCurrentOrNextPrayer() {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        
        if (hour < 6) return "Fajr";
        else if (hour < 13) return "Dhuhr";
        else if (hour < 16) return "Asr";
        else if (hour < 19) return "Maghrib";
        else return "Isha";
    }
    
    private static String getKeyForPrayer(String prayer) {
        switch (prayer.toLowerCase()) {
            case "fajr": return KEY_TEST_FAJR;
            case "dhuhr": case "dohr": return KEY_TEST_DHUHR;
            case "asr": return KEY_TEST_ASR;
            case "maghrib": case "maghreb": return KEY_TEST_MAGHRIB;
            case "isha": return KEY_TEST_ISHA;
            default: return null;
        }
    }
}