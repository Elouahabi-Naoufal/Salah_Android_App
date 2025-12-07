package com.salah.times;

import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;
import java.util.Calendar;

public class AlarmAppIntegration {
    
    public static void addPrayerAlarms(Context context, PrayerTimes prayerTimes) {
        addAlarm(context, "Fajr", prayerTimes.getFajr());
        addAlarm(context, "Dhuhr", prayerTimes.getDhuhr());
        addAlarm(context, "Asr", prayerTimes.getAsr());
        addAlarm(context, "Maghrib", prayerTimes.getMaghrib());
        addAlarm(context, "Isha", prayerTimes.getIsha());
    }
    
    private static void addAlarm(Context context, String prayerName, String time) {
        try {
            String[] parts = time.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            
            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
            intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
            intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
            intent.putExtra(AlarmClock.EXTRA_MESSAGE, prayerName + " Prayer");
            intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            context.startActivity(intent);
        } catch (Exception e) {
            android.util.Log.e("AlarmAppIntegration", "Failed to add alarm for " + prayerName, e);
        }
    }
}
