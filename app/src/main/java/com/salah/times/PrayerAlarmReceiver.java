package com.salah.times;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class PrayerAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String prayerName = intent.getStringExtra("prayer_name");
        
        if (SettingsManager.getAdanEnabled()) {
            PrayerAlarmManager.playAlarm(context);
            Toast.makeText(context, "Prayer time: " + prayerName, Toast.LENGTH_LONG).show();
        }
    }
}