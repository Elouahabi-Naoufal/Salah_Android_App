package com.salah.times;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PrayerAlarmManager {
    private static final int[] PRAYER_REQUEST_CODES = {1001, 1002, 1003, 1004, 1005};
    private static final String[] PRAYER_NAMES = {"Fajr", "Dhuhr", "Asr", "Maghrib", "Isha"};
    
    public static void scheduleAllPrayerAlarms(Context context, PrayerTimes prayerTimes) {
        if (!SettingsManager.getAdanEnabled()) {
            return;
        }
        
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        String[] times = {prayerTimes.getFajr(), prayerTimes.getDhuhr(), prayerTimes.getAsr(), 
                         prayerTimes.getMaghrib(), prayerTimes.getIsha()};
        
        for (int i = 0; i < times.length; i++) {
            scheduleAlarm(context, alarmManager, times[i], PRAYER_NAMES[i], PRAYER_REQUEST_CODES[i]);
        }
    }
    
    private static void scheduleAlarm(Context context, AlarmManager alarmManager, String time, String prayerName, int requestCode) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date prayerTime = format.parse(time);
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(prayerTime);
            
            Calendar now = Calendar.getInstance();
            calendar.set(Calendar.YEAR, now.get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, now.get(Calendar.MONTH));
            calendar.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
            
            if (calendar.before(now)) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            
            Intent intent = new Intent(context, PrayerAlarmReceiver.class);
            intent.putExtra("prayer_name", prayerName);
            
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void cancelAllAlarms(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        for (int requestCode : PRAYER_REQUEST_CODES) {
            Intent intent = new Intent(context, PrayerAlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            alarmManager.cancel(pendingIntent);
        }
    }
    
    public static void playAlarm(Context context) {
        try {
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            
            Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
            ringtone.play();
            
            // Stop after 30 seconds
            new android.os.Handler().postDelayed(() -> {
                if (ringtone.isPlaying()) {
                    ringtone.stop();
                }
            }, 30000);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}