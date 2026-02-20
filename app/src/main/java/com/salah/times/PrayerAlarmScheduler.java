package com.salah.times;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PrayerAlarmScheduler {
    
    public static void schedulePrayerAlarms(Context context, PrayerTimes times) {
        if (times == null) return;
        
        // Check if notifications are enabled
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        boolean notificationsEnabled = Boolean.parseBoolean(db.getSetting("notifications_enabled", "true"));
        
        if (!notificationsEnabled) {
            cancelAllAlarms(context); // Cancel alarms if notifications disabled
            return;
        }
        
        String[] prayers = {"fajr", "dhuhr", "asr", "maghrib", "isha"};
        String[] prayerTimes = {times.getFajr(), times.getDhuhr(), times.getAsr(), times.getMaghrib(), times.getIsha()};
        
        for (int i = 0; i < prayers.length; i++) {
            if (prayerTimes[i] != null) {
                scheduleAlarm(context, prayers[i], prayerTimes[i]);
            }
        }
    }
    
    private static void scheduleAlarm(Context context, String prayerName, String prayerTime) {
        try {
            Calendar calendar = Calendar.getInstance();
            String[] parts = prayerTime.split(":");
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
            calendar.set(Calendar.SECOND, 0);
            
            // If time has passed today, schedule for tomorrow
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            
            Intent intent = new Intent(context, PrayerAlarmReceiver.class);
            intent.putExtra("prayer_name", prayerName);
            
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                prayerName.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pendingIntent
                        );
                    }
                } else {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void cancelAllAlarms(Context context) {
        String[] prayers = {"fajr", "dhuhr", "asr", "maghrib", "isha"};
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        for (String prayer : prayers) {
            Intent intent = new Intent(context, PrayerAlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                prayer.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }
        }
    }
}
