package com.salah.times;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class PrayerAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String prayerName = intent.getStringExtra("prayer_name");
        boolean isTest = intent.getBooleanExtra("is_test", false);
        boolean isSnooze = intent.getBooleanExtra("is_snooze", false);
        
        android.util.Log.d("PrayerAlarm", "Alarm received for " + prayerName + (isTest ? " (TEST)" : "") + (isSnooze ? " (SNOOZE)" : ""));
        
        if (SettingsManager.getAdanEnabled() || isTest || isSnooze) {
            // Launch full-screen alarm activity
            Intent alarmIntent = new Intent(context, AdhanAlarmActivity.class);
            alarmIntent.putExtra("prayer_name", prayerName);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(alarmIntent);
        }
    }
}