package com.salah.times;

import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AlarmAppIntegration {
    
    public static void addPrayerAlarms(Context context, PrayerTimes prayerTimes) {
        String nextPrayer = getNextPrayer(prayerTimes);
        
        // If no prayer left today, set Fajr for tomorrow
        if (nextPrayer == null) {
            nextPrayer = "Fajr";
            String tomorrow = getTomorrowDate();
            PrayerTimes tomorrowTimes = DatabaseHelper.getInstance(context).loadPrayerTimes(
                SettingsManager.getDefaultCity(), tomorrow);
            
            if (tomorrowTimes != null) {
                String lastSetPrayer = getLastSetPrayer(context);
                if (!"Fajr_tomorrow".equals(lastSetPrayer)) {
                    if (addAlarm(context, nextPrayer, tomorrowTimes.getFajr())) {
                        markPrayerSet(context, "Fajr_tomorrow");
                    }
                }
            }
            return;
        }
        
        String lastSetPrayer = getLastSetPrayer(context);
        if (nextPrayer.equals(lastSetPrayer)) return;
        
        String time = getTimeForPrayer(nextPrayer, prayerTimes);
        if (addAlarm(context, nextPrayer, time)) {
            markPrayerSet(context, nextPrayer);
        }
    }
    
    private static String getTomorrowDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());
    }
    
    private static String getNextPrayer(PrayerTimes prayerTimes) {
        try {
            Calendar now = Calendar.getInstance();
            int currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);
            
            String[] prayers = {"Fajr", "Dhuhr", "Asr", "Maghrib", "Isha"};
            String[] times = {prayerTimes.getFajr(), prayerTimes.getDhuhr(), prayerTimes.getAsr(), 
                             prayerTimes.getMaghrib(), prayerTimes.getIsha()};
            
            for (int i = 0; i < prayers.length; i++) {
                int prayerMinutes = timeToMinutes(times[i]);
                if (prayerMinutes > currentMinutes) {
                    return prayers[i];
                }
            }
            
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    private static String getTimeForPrayer(String prayer, PrayerTimes prayerTimes) {
        switch (prayer) {
            case "Fajr": return prayerTimes.getFajr();
            case "Dhuhr": return prayerTimes.getDhuhr();
            case "Asr": return prayerTimes.getAsr();
            case "Maghrib": return prayerTimes.getMaghrib();
            case "Isha": return prayerTimes.getIsha();
            default: return "00:00";
        }
    }
    
    private static int timeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
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
    
    private static String getLastSetPrayer(Context context) {
        return DatabaseHelper.getInstance(context).getSetting("last_set_prayer", "");
    }
    
    private static void markPrayerSet(Context context, String prayer) {
        DatabaseHelper.getInstance(context).saveSetting("last_set_prayer", prayer);
    }
}
