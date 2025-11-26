package com.salah.times;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class IqamaManager {
    private static final String IQAMA_CONFIG_FILE = "iqama_times.json";
    private Context context;
    private Map<String, Integer> iqamaTimes;
    private Gson gson = new Gson();
    
    public IqamaManager(Context context) {
        this.context = context;
        loadIqamaTimes();
    }
    
    private void loadIqamaTimes() {
        try {
            File file = new File(context.getFilesDir(), IQAMA_CONFIG_FILE);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(fis);
                Type type = new TypeToken<Map<String, Integer>>(){}.getType();
                iqamaTimes = gson.fromJson(reader, type);
                reader.close();
            }
        } catch (Exception e) {
            // Use defaults if loading fails
        }
        
        if (iqamaTimes == null) {
            setDefaultIqamaTimes();
        }
    }
    
    private void setDefaultIqamaTimes() {
        iqamaTimes = new HashMap<>();
        iqamaTimes.put("Fajr", 20);
        iqamaTimes.put("Dhuhr", 15);
        iqamaTimes.put("Asr", 15);
        iqamaTimes.put("Maghrib", 10);
        iqamaTimes.put("Isha", 15);
    }
    
    public void saveIqamaTimes() {
        try {
            File file = new File(context.getFilesDir(), IQAMA_CONFIG_FILE);
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            gson.toJson(iqamaTimes, writer);
            writer.close();
        } catch (Exception e) {
            // Handle save error
        }
    }
    
    public int getIqamaDelay(String prayer) {
        return iqamaTimes.getOrDefault(prayer, 15);
    }
    
    public void setIqamaDelay(String prayer, int minutes) {
        iqamaTimes.put(prayer, minutes);
        saveIqamaTimes();
    }
    
    public String getIqamaCountdown(String prayer, String prayerTime) {
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date prayerDateTime = timeFormat.parse(prayerTime);
            
            Calendar prayerCal = Calendar.getInstance();
            prayerCal.setTime(prayerDateTime);
            
            Calendar iqamaCal = (Calendar) prayerCal.clone();
            iqamaCal.add(Calendar.MINUTE, getIqamaDelay(prayer));
            
            Calendar now = Calendar.getInstance();
            
            long diffMillis = iqamaCal.getTimeInMillis() - now.getTimeInMillis();
            
            if (diffMillis <= 0) {
                return TranslationManager.tr("iqama_passed").replace("{}", TranslationManager.tr(prayer.toLowerCase()));
            }
            
            long hours = diffMillis / (60 * 60 * 1000);
            long minutes = (diffMillis % (60 * 60 * 1000)) / (60 * 1000);
            long seconds = (diffMillis % (60 * 1000)) / 1000;
            
            String prayerName = TranslationManager.tr(prayer.toLowerCase());
            return String.format("Time before Iqama of %s: %02d:%02d:%02d", prayerName, hours, minutes, seconds);
            
        } catch (ParseException e) {
            return "Iqama time calculation error";
        }
    }
    
    public boolean isIqamaTime(String prayer, String prayerTime) {
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date prayerDateTime = timeFormat.parse(prayerTime);
            
            Calendar prayerCal = Calendar.getInstance();
            prayerCal.setTime(prayerDateTime);
            
            Calendar iqamaCal = (Calendar) prayerCal.clone();
            iqamaCal.add(Calendar.MINUTE, getIqamaDelay(prayer));
            
            Calendar now = Calendar.getInstance();
            
            return now.after(prayerCal) && now.before(iqamaCal);
            
        } catch (ParseException e) {
            return false;
        }
    }
}