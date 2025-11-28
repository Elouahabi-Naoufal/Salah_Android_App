package com.salah.times;

import android.util.Log;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class HijriDateManager {
    private static final String TAG = "HijriDateManager";
    private static final String API_URL = "https://api.aladhan.com/v1/gToH/";
    
    private static final Map<String, String> hijriMonthsAr = new HashMap<>();
    private static final Map<String, String> hijriMonthsEn = new HashMap<>();
    
    static {
        hijriMonthsAr.put("Muharram", "محرم");
        hijriMonthsAr.put("Safar", "صفر");
        hijriMonthsAr.put("Rabi' al-awwal", "ربيع الأول");
        hijriMonthsAr.put("Rabi' al-thani", "ربيع الثاني");
        hijriMonthsAr.put("Jumada al-awwal", "جمادى الأولى");
        hijriMonthsAr.put("Jumada al-thani", "جمادى الثانية");
        hijriMonthsAr.put("Rajab", "رجب");
        hijriMonthsAr.put("Sha'ban", "شعبان");
        hijriMonthsAr.put("Ramadan", "رمضان");
        hijriMonthsAr.put("Shawwal", "شوال");
        hijriMonthsAr.put("Dhu al-Qi'dah", "ذو القعدة");
        hijriMonthsAr.put("Dhu al-Hijjah", "ذو الحجة");
        
        hijriMonthsEn.put("Muharram", "Muharram");
        hijriMonthsEn.put("Safar", "Safar");
        hijriMonthsEn.put("Rabi' al-awwal", "Rabi al-Awwal");
        hijriMonthsEn.put("Rabi' al-thani", "Rabi al-Thani");
        hijriMonthsEn.put("Jumada al-awwal", "Jumada al-Awwal");
        hijriMonthsEn.put("Jumada al-thani", "Jumada al-Thani");
        hijriMonthsEn.put("Rajab", "Rajab");
        hijriMonthsEn.put("Sha'ban", "Shaban");
        hijriMonthsEn.put("Ramadan", "Ramadan");
        hijriMonthsEn.put("Shawwal", "Shawwal");
        hijriMonthsEn.put("Dhu al-Qi'dah", "Dhu al-Qidah");
        hijriMonthsEn.put("Dhu al-Hijjah", "Dhu al-Hijjah");
    }
    
    public static CompletableFuture<String> fetchHijriDate() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String currentDate = dateFormat.format(new Date());
                
                URL url = new URL(API_URL + currentDate);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
                JsonObject data = json.getAsJsonObject("data");
                JsonObject hijri = data.getAsJsonObject("hijri");
                
                String day = hijri.get("day").getAsString();
                String year = hijri.get("year").getAsString();
                JsonObject month = hijri.getAsJsonObject("month");
                String monthEn = month.get("en").getAsString();
                
                return formatHijriDate(day, monthEn, year);
                
            } catch (Exception e) {
                Log.e(TAG, "Error fetching Hijri date from API", e);
                return getApproximateHijriDate();
            }
        });
    }
    
    private static String formatHijriDate(String day, String monthEn, String year) {
        String language = TranslationManager.getCurrentLanguage();
        
        if ("ar".equals(language)) {
            String monthAr = hijriMonthsAr.getOrDefault(monthEn, monthEn);
            return day + " " + monthAr + " " + year + " هـ";
        } else {
            String monthLocal = hijriMonthsEn.getOrDefault(monthEn, monthEn);
            return day + " " + monthLocal + " " + year + " AH";
        }
    }
    
    private static String getApproximateHijriDate() {
        try {
            Calendar cal = Calendar.getInstance();
            int gregorianYear = cal.get(Calendar.YEAR);
            
            // Approximate conversion: Hijri year ≈ (Gregorian year - 622) × 1.030684
            int hijriYear = (int) ((gregorianYear - 622) * 1.030684);
            
            String language = TranslationManager.getCurrentLanguage();
            if ("ar".equals(language)) {
                return hijriYear + " هـ (تقريبي)";
            } else {
                return hijriYear + " AH (Approximate)";
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error calculating approximate Hijri date", e);
            return TranslationManager.tr("hijri_unavailable");
        }
    }
}