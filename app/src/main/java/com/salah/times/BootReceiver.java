package com.salah.times;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            DatabaseHelper db = DatabaseHelper.getInstance(context);
            String cityName = db.getSetting("default_city", "Casablanca");
            City city = CitiesData.getCityByName(cityName);
            String todayDdMm = new SimpleDateFormat("dd/MM", Locale.getDefault()).format(new java.util.Date());
            PrayerTimes times = (city != null) ? db.loadPrayerTimes(city.getTableName(), todayDdMm) : null;
            if (times != null) {
                PrayerAlarmScheduler.schedulePrayerAlarms(context, times);
            }
        }
    }
}
