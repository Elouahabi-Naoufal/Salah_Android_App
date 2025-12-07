package com.salah.times;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.AlarmClock;
import android.widget.Toast;

public class AlarmAppIntegration {
    private static final String PREFS_NAME = "alarm_prefs";
    private static final String KEY_ALARMS_SET = "alarms_set_";
    
    public static void addPrayerAlarms(Context context, PrayerTimes prayerTimes) {
        String date = prayerTimes.getDate();
        if (areAlarmsSet(context, date)) return;
        
        boolean success = true;
        success &= addAlarm(context, "Fajr", prayerTimes.getFajr());
        success &= addAlarm(context, "Dhuhr", prayerTimes.getDhuhr());
        success &= addAlarm(context, "Asr", prayerTimes.getAsr());
        success &= addAlarm(context, "Maghrib", prayerTimes.getMaghrib());
        success &= addAlarm(context, "Isha", prayerTimes.getIsha());
        
        if (success) {
            markAlarmsSet(context, date);
        }
    }
    
    private static boolean addAlarm(Context context, String prayerName, String time) {
        try {
            String[] parts = time.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            
            String translatedName = TranslationManager.tr("prayers." + prayerName.toLowerCase());
            
            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
            intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
            intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
            intent.putExtra(AlarmClock.EXTRA_MESSAGE, translatedName);
            intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            context.startActivity(intent);
            return true;
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("Alarm already exists")) {
                return true;
            }
            Toast.makeText(context, TranslationManager.tr("messages.alarm_failed"), Toast.LENGTH_SHORT).show();
            return false;
        } catch (Exception e) {
            Toast.makeText(context, TranslationManager.tr("messages.alarm_failed"), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    
    private static boolean areAlarmsSet(Context context, String date) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_ALARMS_SET + date, false);
    }
    
    private static void markAlarmsSet(Context context, String date) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_ALARMS_SET + date, true).apply();
    }
}
